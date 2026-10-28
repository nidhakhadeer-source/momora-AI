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
                _uiState.value = _uiState.value.copy(isInPerson = event.isInPerson)
            }
            is AppointmentsCalendarUiEvent.OnDateSelected -> {
                _uiState.value = _uiState.value.copy(selectedDayOfMonth = event.dayOfMonth)
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
            AppointmentsCalendarUiEvent.OnNextMonthClicked -> {}
            AppointmentsCalendarUiEvent.OnBackClicked -> {}
            AppointmentsCalendarUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getBookingData().collect { data ->
                _uiState.value = _uiState.value.copy(bookingData = data)
            }
        }
    }

    private fun confirmBooking() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        val doctor = _uiState.value.bookingData?.doctorName ?: "Dr Amelia Chen"
        val dateText = "${_uiState.value.selectedDayOfMonth} Oct 2026"
        val timeText = _uiState.value.selectedTimeSlot

        viewModelScope.launch {
            repository.confirmAppointment(doctor, dateText, timeText, _uiState.value.isInPerson)
                .collect { result ->
                    result.onSuccess {
                        _uiState.value = _uiState.value.copy(isLoading = false, isConfirmedSuccess = true)
                    }.onFailure { err ->
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
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
