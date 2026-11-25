package com.example.SIMATS.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.AppointmentBookingData
import com.example.SIMATS.data.model.CalendarDateItem
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
import java.util.TimeZone

class AppointmentsCalendarViewModel(
    private val repository: AppointmentRepository = AppointmentRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsCalendarUiState())
    val uiState: StateFlow<AppointmentsCalendarUiState> = _uiState.asStateFlow()

    private var baseTimeSlots: List<String> = listOf("09:00", "10:30", "14:00")

    init {
        loadData()
    }

    private fun getClinicCalendar(): Calendar {
        return Calendar.getInstance(TimeZone.getDefault())
    }

    fun onEvent(event: AppointmentsCalendarUiEvent) {
        when (event) {
            is AppointmentsCalendarUiEvent.OnVisitTypeToggled -> {
                _uiState.value = _uiState.value.copy(isInPerson = true)
            }
            is AppointmentsCalendarUiEvent.OnDateSelected -> {
                handleDateSelected(event.dayOfMonth)
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
            AppointmentsCalendarUiEvent.OnNextMonthClicked -> {
                val newOffset = if (_uiState.value.monthOffset == 0) 1 else 0
                _uiState.value = _uiState.value.copy(monthOffset = newOffset)
                applyDynamicDates(newOffset)
            }
            AppointmentsCalendarUiEvent.OnBackClicked -> {}
            AppointmentsCalendarUiEvent.OnNotificationClicked -> {}
            AppointmentsCalendarUiEvent.OnRetryClicked -> {
                loadData()
            }
        }
    }

    private fun handleDateSelected(dayOfMonth: Int) {
        val state = _uiState.value
        val allSlots = baseTimeSlots
        val selectedDateItem = state.bookingData?.availableDates?.find { it.dayOfMonth == dayOfMonth }
        val monthTitle = state.selectedMonthTitle
            ?: state.bookingData?.monthTitle
            ?: SimpleDateFormat("MMMM yyyy", Locale.US).format(getClinicCalendar().time)

        val validSlots = getValidTimeSlotsForDay(dayOfMonth, monthTitle, allSlots)
        val currentSlot = state.selectedTimeSlot
        val newSlot = if (currentSlot != null && validSlots.contains(currentSlot)) {
            currentSlot
        } else {
            validSlots.firstOrNull() ?: currentSlot ?: "10:30"
        }

        val updatedDates = state.bookingData?.availableDates?.map {
            it.copy(isSelected = it.dayOfMonth == dayOfMonth)
        } ?: emptyList()

        _uiState.value = state.copy(
            bookingData = state.bookingData?.copy(availableDates = updatedDates),
            selectedDayOfMonth = dayOfMonth,
            selectedDayOfWeek = selectedDateItem?.dayOfWeek,
            selectedTimeSlot = newSlot,
            availableTimeSlotsForSelectedDate = validSlots
        )
    }

    private fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getBookingData().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        if (data.availableTimeSlots.isNotEmpty()) {
                            baseTimeSlots = data.availableTimeSlots
                        }
                        val plan = buildDynamicDatePlan(
                            todayCal = getClinicCalendar(),
                            allSlots = baseTimeSlots,
                            monthOffset = _uiState.value.monthOffset
                        )

                        val updatedBookingData = data.copy(
                            monthTitle = plan.monthTitle,
                            availableDates = plan.dates,
                            availableTimeSlots = baseTimeSlots
                        )

                        _uiState.value = _uiState.value.copy(
                            bookingData = updatedBookingData,
                            selectedDayOfMonth = plan.selectedDayOfMonth,
                            selectedDayOfWeek = plan.selectedDayOfWeek,
                            selectedMonthTitle = plan.selectedMonthTitle,
                            selectedTimeSlot = plan.selectedTimeSlot,
                            availableTimeSlotsForSelectedDate = plan.validSlotsForSelectedDate,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        val plan = buildDynamicDatePlan(
                            todayCal = getClinicCalendar(),
                            allSlots = baseTimeSlots,
                            monthOffset = _uiState.value.monthOffset
                        )
                        val fallbackBookingData = AppointmentBookingData(
                            doctorName = "Dr Amelia Chen",
                            doctorSpecialty = "Obstetrician · Willow Maternity Clinic",
                            monthTitle = plan.monthTitle,
                            availableDates = plan.dates,
                            availableTimeSlots = baseTimeSlots,
                            defaultType = "Routine antenatal check",
                            defaultLocationNotes = "Willow Maternity Clinic · Room 3"
                        )
                        _uiState.value = _uiState.value.copy(
                            bookingData = fallbackBookingData,
                            selectedDayOfMonth = plan.selectedDayOfMonth,
                            selectedDayOfWeek = plan.selectedDayOfWeek,
                            selectedMonthTitle = plan.selectedMonthTitle,
                            selectedTimeSlot = plan.selectedTimeSlot,
                            availableTimeSlotsForSelectedDate = plan.validSlotsForSelectedDate,
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load appointments"
                        )
                    }
                )
            }
        }
    }

    private fun applyDynamicDates(monthOffset: Int) {
        val state = _uiState.value
        val plan = buildDynamicDatePlan(
            todayCal = getClinicCalendar(),
            allSlots = baseTimeSlots,
            monthOffset = monthOffset
        )

        val updatedBookingData = state.bookingData?.copy(
            monthTitle = plan.monthTitle,
            availableDates = plan.dates
        )

        _uiState.value = state.copy(
            bookingData = updatedBookingData,
            selectedDayOfMonth = plan.selectedDayOfMonth,
            selectedDayOfWeek = plan.selectedDayOfWeek,
            selectedMonthTitle = plan.selectedMonthTitle,
            selectedTimeSlot = plan.selectedTimeSlot,
            availableTimeSlotsForSelectedDate = plan.validSlotsForSelectedDate
        )
    }

    private data class DynamicDatePlan(
        val dates: List<CalendarDateItem>,
        val monthTitle: String,
        val selectedDayOfMonth: Int,
        val selectedDayOfWeek: String,
        val selectedMonthTitle: String,
        val selectedTimeSlot: String,
        val validSlotsForSelectedDate: List<String>
    )

    private fun buildDynamicDatePlan(
        todayCal: Calendar,
        allSlots: List<String>,
        monthOffset: Int
    ): DynamicDatePlan {
        val startCal = (todayCal.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (monthOffset > 0) {
            startCal.add(Calendar.MONTH, monthOffset)
            startCal.set(Calendar.DAY_OF_MONTH, 1)
        }

        val dates = mutableListOf<CalendarDateItem>()
        val calList = mutableListOf<Calendar>()

        for (i in 0 until 5) {
            val c = (startCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, i) }
            val dow = SimpleDateFormat("EEE", Locale.US).format(c.time)
            val dom = c.get(Calendar.DAY_OF_MONTH)
            dates.add(CalendarDateItem(dayOfWeek = dow, dayOfMonth = dom, isSelected = false))
            calList.add(c)
        }

        val primaryMonthTitle = SimpleDateFormat("MMMM yyyy", Locale.US).format(calList.first().time)

        val todayDom = todayCal.get(Calendar.DAY_OF_MONTH)
        val todayMonthYear = SimpleDateFormat("MMMM yyyy", Locale.US).format(todayCal.time)

        var selectedIndex = 0
        if (monthOffset == 0) {
            val todaySlots = getValidTimeSlotsForDay(todayDom, todayMonthYear, allSlots)
            if (todaySlots.isEmpty() && dates.size > 1) {
                // If today has no available slots, select the next available date on or after today (tomorrow)
                selectedIndex = 1
            } else {
                selectedIndex = 0
            }
        } else {
            selectedIndex = 0
        }

        val selectedCal = calList[selectedIndex]
        val selectedDateItem = dates[selectedIndex]
        val finalDates = dates.mapIndexed { idx, item ->
            item.copy(isSelected = idx == selectedIndex)
        }

        val selectedMonth = SimpleDateFormat("MMMM yyyy", Locale.US).format(selectedCal.time)
        val validSlots = getValidTimeSlotsForDay(selectedDateItem.dayOfMonth, selectedMonth, allSlots)
        val defaultSlot = validSlots.firstOrNull() ?: allSlots.firstOrNull() ?: "10:30"

        return DynamicDatePlan(
            dates = finalDates,
            monthTitle = primaryMonthTitle,
            selectedDayOfMonth = selectedDateItem.dayOfMonth,
            selectedDayOfWeek = selectedDateItem.dayOfWeek,
            selectedMonthTitle = selectedMonth,
            selectedTimeSlot = defaultSlot,
            validSlotsForSelectedDate = validSlots
        )
    }

    private fun getValidTimeSlotsForDay(
        dayOfMonth: Int?,
        monthTitle: String,
        allSlots: List<String>
    ): List<String> {
        if (dayOfMonth == null) return allSlots
        val now = getClinicCalendar()
        val currentDay = now.get(Calendar.DAY_OF_MONTH)
        val currentMonthYear = SimpleDateFormat("MMMM yyyy", Locale.US).format(now.time)

        // Only filter past hours when looking at TODAY in the current month & year
        if (dayOfMonth == currentDay && monthTitle.equals(currentMonthYear, ignoreCase = true)) {
            val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
            val futureSlots = allSlots.filter { slot ->
                val parts = slot.split(":")
                if (parts.size == 2) {
                    val slotMinutes = (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
                    slotMinutes > currentMinutes
                } else true
            }
            return futureSlots
        }
        return allSlots
    }

    private fun confirmBooking() {
        val state = _uiState.value
        val doctor = state.bookingData?.doctorName ?: "Dr Amelia Chen"
        val todayCal = getClinicCalendar()
        val day = state.selectedDayOfMonth ?: todayCal.get(Calendar.DAY_OF_MONTH)
        val monthTitle = state.selectedMonthTitle
            ?: state.bookingData?.monthTitle
            ?: SimpleDateFormat("MMMM yyyy", Locale.US).format(todayCal.time)
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
