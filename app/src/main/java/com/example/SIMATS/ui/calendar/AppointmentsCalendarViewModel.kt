package com.example.SIMATS.ui.calendar

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppointmentsCalendarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsCalendarUiState())
    val uiState: StateFlow<AppointmentsCalendarUiState> = _uiState.asStateFlow()

    fun onEvent(event: AppointmentsCalendarUiEvent) {
        when (event) {
            is AppointmentsCalendarUiEvent.OnDaySelected -> {
                _uiState.update { state ->
                    val updatedDays = state.calendarDays.map { d ->
                        d.copy(isSelected = d.dayNumber == event.day.dayNumber && d.isCurrentMonth == event.day.isCurrentMonth)
                    }
                    val selectedText = when (event.day.dayNumber) {
                        28 -> "Wednesday, 28 October"
                        26 -> "Monday, 26 October"
                        else -> "Day ${event.day.dayNumber}, October"
                    }
                    state.copy(
                        calendarDays = updatedDays,
                        selectedDateText = selectedText,
                        appointmentCountText = if (event.day.dayNumber == 28) "1 appointment" else "0 appointments"
                    )
                }
            }
            AppointmentsCalendarUiEvent.OnPreviousMonthClicked -> {
                // Action for previous month
            }
            AppointmentsCalendarUiEvent.OnNextMonthClicked -> {
                // Action for next month
            }
            AppointmentsCalendarUiEvent.OnAddAppointmentClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Home.route) }
            }
            is AppointmentsCalendarUiEvent.OnAppointmentDetailsClicked -> {
                // Action for appointment details
            }
            is AppointmentsCalendarUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            AppointmentsCalendarUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            AppointmentsCalendarUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
