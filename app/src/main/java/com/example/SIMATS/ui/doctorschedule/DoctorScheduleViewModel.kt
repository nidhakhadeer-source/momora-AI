package com.example.SIMATS.ui.doctorschedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.DoctorScheduleRepository
import com.example.SIMATS.data.repository.DoctorScheduleRepositoryImpl
import com.example.SIMATS.navigation.SelectedPatientHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DoctorScheduleViewModel(
    private val repository: DoctorScheduleRepository = DoctorScheduleRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorScheduleUiState())
    val uiState: StateFlow<DoctorScheduleUiState> = _uiState.asStateFlow()

    init {
        loadSchedule(null)
    }

    fun onEvent(event: DoctorScheduleUiEvent) {
        when (event) {
            is DoctorScheduleUiEvent.OnDateTabSelected -> {
                val tabs = _uiState.value.scheduleData?.dateTabs ?: emptyList()
                if (event.index in tabs.indices) {
                    val tabText = tabs[event.index]
                    val currentDate = _uiState.value.selectedDate ?: _uiState.value.scheduleData?.selectedDate
                    val resolvedDate = convertTabToDate(tabText, currentDate)
                    _uiState.value = _uiState.value.copy(
                        selectedDateTabIndex = event.index,
                        selectedDate = resolvedDate
                    )
                    loadSchedule(resolvedDate)
                } else {
                    _uiState.value = _uiState.value.copy(selectedDateTabIndex = event.index)
                }
            }
            is DoctorScheduleUiEvent.OnDateSelected -> {
                _uiState.value = _uiState.value.copy(selectedDate = event.date)
                loadSchedule(event.date)
            }
            is DoctorScheduleUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            is DoctorScheduleUiEvent.OnPatientClicked -> {
                SelectedPatientHolder.selectedPatientId = event.patientId
                SelectedPatientHolder.selectedPatientName = event.patientName
            }
            is DoctorScheduleUiEvent.OnRescheduleAppointment -> {
                rescheduleAppointment(event.appointmentId, event.newDate, event.newTime)
            }
            DoctorScheduleUiEvent.OnManageAvailabilityClicked -> {}
            DoctorScheduleUiEvent.OnCalendarClicked -> {}
            DoctorScheduleUiEvent.OnRetryClicked -> {
                loadSchedule(_uiState.value.selectedDate)
            }
            DoctorScheduleUiEvent.OnBackClicked -> {}
            DoctorScheduleUiEvent.OnNotificationClicked -> {}
            DoctorScheduleUiEvent.OnClearRescheduleMessage -> {
                _uiState.value = _uiState.value.copy(rescheduleSuccessMessage = null)
            }
        }
    }

    fun loadSchedule(date: String?) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getScheduleData(date).collect { result ->
                result.fold(
                    onSuccess = { data ->
                        val activeDate = data.selectedDate ?: date ?: _uiState.value.selectedDate
                        val matchingIdx = findMatchingTabIndex(data.dateTabs, activeDate)
                        _uiState.value = _uiState.value.copy(
                            scheduleData = data,
                            selectedDate = activeDate,
                            selectedDateTabIndex = if (matchingIdx >= 0) matchingIdx else 0,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load schedule"
                        )
                    }
                )
            }
        }
    }

    fun rescheduleAppointment(appointmentId: Int, newDate: String, newTime: String) {
        _uiState.value = _uiState.value.copy(isRescheduling = true, rescheduleSuccessMessage = null, errorMessage = null)
        viewModelScope.launch {
            repository.rescheduleAppointment(appointmentId, newDate, newTime).collect { result ->
                result.fold(
                    onSuccess = { message ->
                        _uiState.value = _uiState.value.copy(
                            isRescheduling = false,
                            rescheduleSuccessMessage = message
                        )
                        // Refresh schedule for current selected date so changes reflect immediately
                        loadSchedule(_uiState.value.selectedDate)
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isRescheduling = false,
                            errorMessage = err.message ?: "Failed to reschedule appointment"
                        )
                    }
                )
            }
        }
    }

    private fun findMatchingTabIndex(tabs: List<String>, dateStr: String?): Int {
        if (dateStr.isNullOrBlank() || tabs.isEmpty()) return 0
        return try {
            val sdfIn = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val d = sdfIn.parse(dateStr) ?: return 0
            val sdfTab = SimpleDateFormat("dd MMM", Locale.ENGLISH)
            val targetTab = sdfTab.format(d)
            val idx = tabs.indexOfFirst {
                it.equals(targetTab, ignoreCase = true) ||
                it.trim().replace("0", "").equals(targetTab.trim().replace("0", ""), ignoreCase = true)
            }
            if (idx >= 0) idx else 0
        } catch (e: Exception) {
            0
        }
    }

    private fun convertTabToDate(tabText: String, currentSelectedDate: String?): String {
        return try {
            val year = if (!currentSelectedDate.isNullOrBlank() && currentSelectedDate.length >= 4) {
                currentSelectedDate.substring(0, 4)
            } else {
                Calendar.getInstance().get(Calendar.YEAR).toString()
            }
            val sdfIn = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
            val date = sdfIn.parse("${tabText.trim()} $year")
            if (date != null) {
                val sdfOut = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                sdfOut.format(date)
            } else {
                currentSelectedDate ?: ""
            }
        } catch (e: Exception) {
            currentSelectedDate ?: ""
        }
    }

    class Factory(private val repository: DoctorScheduleRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DoctorScheduleViewModel(repository) as T
        }
    }
}
