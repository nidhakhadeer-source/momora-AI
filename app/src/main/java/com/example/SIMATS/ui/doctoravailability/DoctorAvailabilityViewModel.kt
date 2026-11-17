package com.example.SIMATS.ui.doctoravailability

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.DoctorAvailabilitySlot
import com.example.SIMATS.data.repository.DoctorAvailabilityRepository
import com.example.SIMATS.data.repository.DoctorAvailabilityRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DoctorAvailabilityViewModel(
    private val repository: DoctorAvailabilityRepository = DoctorAvailabilityRepositoryImpl()
) : ViewModel() {

    private val allWeekdays = listOf(
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    )

    private val _uiState = MutableStateFlow(
        DoctorAvailabilityUiState(weeklyHours = buildDefaultSlots(30))
    )
    val uiState: StateFlow<DoctorAvailabilityUiState> = _uiState.asStateFlow()

    init {
        loadAvailability()
    }

    fun onEvent(event: DoctorAvailabilityUiEvent) {
        when (event) {
            is DoctorAvailabilityUiEvent.OnDayToggled -> {
                val updated = _uiState.value.weeklyHours.map { slot ->
                    if (slot.weekday.equals(event.weekday, ignoreCase = true)) {
                        slot.copy(isActive = event.isActive)
                    } else {
                        slot
                    }
                }
                _uiState.value = _uiState.value.copy(weeklyHours = updated)
            }
            is DoctorAvailabilityUiEvent.OnTimeChanged -> {
                val updated = _uiState.value.weeklyHours.map { slot ->
                    if (slot.weekday.equals(event.weekday, ignoreCase = true)) {
                        slot.copy(startTime = event.startTime, endTime = event.endTime)
                    } else {
                        slot
                    }
                }
                _uiState.value = _uiState.value.copy(weeklyHours = updated)
            }
            is DoctorAvailabilityUiEvent.OnSlotMinutesChanged -> {
                val updated = _uiState.value.weeklyHours.map { it.copy(slotMinutes = event.minutes) }
                _uiState.value = _uiState.value.copy(
                    slotMinutes = event.minutes,
                    weeklyHours = updated
                )
            }
            DoctorAvailabilityUiEvent.OnSaveClicked -> {
                saveAvailability()
            }
            DoctorAvailabilityUiEvent.OnRetryClicked -> {
                loadAvailability()
            }
            DoctorAvailabilityUiEvent.OnBackClicked -> {}
            DoctorAvailabilityUiEvent.OnNotificationClicked -> {}
            DoctorAvailabilityUiEvent.OnClearMessages -> {
                _uiState.value = _uiState.value.copy(
                    saveSuccessMessage = null,
                    errorMessage = null
                )
            }
        }
    }

    private fun loadAvailability() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getAvailability().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        val remoteSlots = data.weeklyHours.ifEmpty { data.availability }
                        val defaultSlotMin = remoteSlots.firstOrNull()?.slotMinutes ?: 30

                        // Merge remote slots into all 7 weekdays
                        val merged = allWeekdays.map { dayName ->
                            val match = remoteSlots.find { it.weekday.equals(dayName, ignoreCase = true) }
                            match ?: DoctorAvailabilitySlot(
                                weekday = dayName,
                                startTime = "09:00",
                                endTime = "17:00",
                                slotMinutes = defaultSlotMin,
                                isActive = dayName !in listOf("Saturday", "Sunday")
                            )
                        }

                        _uiState.value = _uiState.value.copy(
                            weeklyHours = merged,
                            slotMinutes = defaultSlotMin,
                            doctorName = data.doctorName,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load clinic availability"
                        )
                    }
                )
            }
        }
    }

    private fun saveAvailability() {
        val currentSlots = _uiState.value.weeklyHours
        // Validate active days
        for (slot in currentSlots) {
            if (slot.isActive) {
                if (slot.startTime >= slot.endTime) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "${slot.weekday}: Start time (${slot.startTime}) must be earlier than end time (${slot.endTime})."
                    )
                    return
                }
            }
        }

        _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, saveSuccessMessage = null)
        viewModelScope.launch {
            repository.saveAvailability(currentSlots).collect { result ->
                result.fold(
                    onSuccess = { msg ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            saveSuccessMessage = msg
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            errorMessage = err.message ?: "Failed to save availability"
                        )
                    }
                )
            }
        }
    }

    private fun buildDefaultSlots(slotMinutes: Int): List<DoctorAvailabilitySlot> {
        return allWeekdays.map { day ->
            DoctorAvailabilitySlot(
                weekday = day,
                startTime = "09:00",
                endTime = "17:00",
                slotMinutes = slotMinutes,
                isActive = day !in listOf("Saturday", "Sunday")
            )
        }
    }

    class Factory(private val repository: DoctorAvailabilityRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DoctorAvailabilityViewModel(repository) as T
        }
    }
}
