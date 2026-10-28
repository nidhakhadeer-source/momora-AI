package com.example.SIMATS.ui.doctorschedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.DoctorScheduleRepository
import com.example.SIMATS.data.repository.DoctorScheduleRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DoctorScheduleViewModel(
    private val repository: DoctorScheduleRepository = DoctorScheduleRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorScheduleUiState())
    val uiState: StateFlow<DoctorScheduleUiState> = _uiState.asStateFlow()

    init {
        loadSchedule()
    }

    fun onEvent(event: DoctorScheduleUiEvent) {
        when (event) {
            is DoctorScheduleUiEvent.OnDateTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedDateTabIndex = event.index)
            }
            is DoctorScheduleUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            DoctorScheduleUiEvent.OnRescheduleClicked -> {}
            DoctorScheduleUiEvent.OnViewRecordClicked -> {}
            DoctorScheduleUiEvent.OnManageAvailabilityClicked -> {}
            DoctorScheduleUiEvent.OnCalendarClicked -> {}
            DoctorScheduleUiEvent.OnBackClicked -> {}
            DoctorScheduleUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            repository.getScheduleData().collect { data ->
                _uiState.value = _uiState.value.copy(scheduleData = data)
            }
        }
    }

    class Factory(private val repository: DoctorScheduleRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DoctorScheduleViewModel(repository) as T
        }
    }
}
