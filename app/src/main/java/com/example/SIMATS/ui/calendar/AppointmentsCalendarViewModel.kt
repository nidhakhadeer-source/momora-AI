package com.example.SIMATS.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.AppointmentBookingData
import com.example.SIMATS.data.repository.AppointmentRepository
import com.example.SIMATS.data.repository.AppointmentRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AppointmentsCalendarViewModel(
    private val repository: AppointmentRepository = AppointmentRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsCalendarUiState())
    val uiState: StateFlow<AppointmentsCalendarUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: AppointmentsCalendarUiEvent) {
        when (event) {
            is AppointmentsCalendarUiEvent.OnVisitTypeToggled -> {
                // Video visit option removed; always maintain in-person appointment mode
                _uiState.value = _uiState.value.copy(isInPerson = true)
            }
            is AppointmentsCalendarUiEvent.OnDateSelected -> {
                val selectedDateItem = _uiState.value.bookingData?.availableDates?.find { it.dayOfMonth == event.dayOfMonth }
                val monthTitle = _uiState.value.selectedMonthTitle
                    ?: _uiState.value.bookingData?.monthTitle
                    ?: SimpleDateFormat("MMMM yyyy", Locale.US).format(Date())
                val availableSlots = _uiState.value.bookingData?.availableTimeSlots ?: listOf("09:00", "10:30", "14:00")
                val validSlots = getValidTimeSlotsForDay(event.dayOfMonth, monthTitle, availableSlots)

                val currentSlot = _uiState.value.selectedTimeSlot
                val newSlot = if (currentSlot != null && validSlots.contains(currentSlot)) {
                    currentSlot
                } else {
                    validSlots.firstOrNull() ?: currentSlot ?: "10:30"
                }

                _uiState.value = _uiState.value.copy(
                    selectedDayOfMonth = event.dayOfMonth,
                    selectedDayOfWeek = selectedDateItem?.dayOfWeek,
                    selectedTimeSlot = newSlot
                )
            }
            is AppointmentsCalendarUiEvent.OnTimeSlotSelected -> {
                _uiState.value = _uiState.value.copy(selectedTimeSlot = event.timeSlot)
            }
            is AppointmentsCalendarUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            AppointmentsCalendarUiEvent.OnConfirmAppointmentClicked -> {
                confirmBooking()
            }
            AppointmentsCalendarUiEvent.OnDismissConfirmationDialog -> {
                _uiState.value = _uiState.value.copy(isConfirmedSuccess = false, confirmationMessage = null)
            }
            AppointmentsCalendarUiEvent.OnNextMonthClicked -> {}
            AppointmentsCalendarUiEvent.OnBackClicked -> {}
            AppointmentsCalendarUiEvent.OnNotificationClicked -> {}
            AppointmentsCalendarUiEvent.OnRetryClicked -> {
                loadData()
            }
        }
    }

    private fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getBookingData().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        val defaultDate = data.availableDates.find { it.isSelected }
                            ?: data.availableDates.firstOrNull()
                        val selectedDay = defaultDate?.dayOfMonth
                        val selectedDow = defaultDate?.dayOfWeek
                        val monthTitle = data.monthTitle.ifBlank {
                            SimpleDateFormat("MMMM yyyy", Locale.US).format(Date())
                        }

                        val validSlots = getValidTimeSlotsForDay(selectedDay, monthTitle, data.availableTimeSlots)
                        val defaultSlot = validSlots.firstOrNull()
                            ?: data.availableTimeSlots.firstOrNull()
                            ?: "10:30"

                        _uiState.value = _uiState.value.copy(
                            bookingData = data,
                            selectedDayOfMonth = selectedDay,
                            selectedDayOfWeek = selectedDow,
                            selectedMonthTitle = monthTitle,
                            selectedTimeSlot = defaultSlot,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load appointments"
                        )
                    }
                )
            }
        }
    }

    private fun getValidTimeSlotsForDay(
        dayOfMonth: Int?,
        monthTitle: String,
        allSlots: List<String>
    ): List<String> {
        if (dayOfMonth == null) return allSlots
        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_MONTH)
        val currentMonthYear = SimpleDateFormat("MMMM yyyy", Locale.US).format(now.time)

        if (dayOfMonth == currentDay && monthTitle.equals(currentMonthYear, ignoreCase = true)) {
            val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
            val futureSlots = allSlots.filter { slot ->
                val parts = slot.split(":")
                if (parts.size == 2) {
                    val slotMinutes = (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
                    slotMinutes > currentMinutes
                } else true
            }
            return if (futureSlots.isNotEmpty()) futureSlots else allSlots
        }
        return allSlots
    }

    private fun confirmBooking() {
        val state = _uiState.value
        val doctor = state.bookingData?.doctorName ?: "Dr Amelia Chen"
        val day = state.selectedDayOfMonth ?: 19
        val monthTitle = state.selectedMonthTitle
            ?: state.bookingData?.monthTitle
            ?: SimpleDateFormat("MMMM yyyy", Locale.US).format(Date())
        val dateText = "$day $monthTitle"
        val timeText = state.selectedTimeSlot ?: "10:30"
        val isInPerson = true

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            repository.confirmAppointment(doctor, dateText, timeText, isInPerson)
                .collect { result ->
                    result.onSuccess {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isConfirmedSuccess = true,
                            confirmationMessage = "Your in-person appointment with $doctor on $dateText at $timeText has been confirmed."
                        )
                    }.onFailure { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Booking could not be confirmed"
                        )
                    }
                }
        }
    }

    class Factory(private val repository: AppointmentRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppointmentsCalendarViewModel(repository) as T
        }
    }
}
