package com.example.SIMATS.ui.doctoravailability

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.DoctorAvailabilitySlot
import com.example.SIMATS.data.repository.DoctorAvailabilityRepository
import com.example.SIMATS.data.repository.DoctorAvailabilityRepositoryImpl
import com.example.SIMATS.ui.components.formatDoctorTime
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
        DoctorAvailabilityUiState(
            weeklyHours = emptyList(),
            slotMinutes = 30,
            isLoading = true
        )
    )
    val uiState: StateFlow<DoctorAvailabilityUiState> = _uiState.asStateFlow()

    init {
        loadAvailability()
    }

    fun onEvent(event: DoctorAvailabilityUiEvent) {
        when (event) {
            DoctorAvailabilityUiEvent.OnScreenOpened -> {
                loadAvailability()
            }
            is DoctorAvailabilityUiEvent.OnDayToggled -> {
                val updated = _uiState.value.weeklyHours.map { slot ->
                    if (slot.weekday.equals(event.weekday, ignoreCase = true)) {
                        slot.copy(isActive = event.isActive)
                    } else {
                        slot
                    }
                }
                _uiState.value = _uiState.value.copy(
                    weeklyHours = updated,
                    errorMessage = null,
                    saveSuccessMessage = null
                )
            }
            is DoctorAvailabilityUiEvent.OnTimeChanged -> {
                val updated = _uiState.value.weeklyHours.map { slot ->
                    if (slot.weekday.equals(event.weekday, ignoreCase = true)) {
                        slot.copy(startTime = event.startTime, endTime = event.endTime)
                    } else {
                        slot
                    }
                }
                _uiState.value = _uiState.value.copy(
                    weeklyHours = updated,
                    errorMessage = null,
                    saveSuccessMessage = null
                )
            }
            is DoctorAvailabilityUiEvent.OnSlotMinutesChanged -> {
                val updated = _uiState.value.weeklyHours.map { it.copy(slotMinutes = event.minutes) }
                _uiState.value = _uiState.value.copy(
                    slotMinutes = event.minutes,
                    weeklyHours = updated,
                    errorMessage = null,
                    saveSuccessMessage = null
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

    fun loadAvailability(preserveSuccessMessage: Boolean = false) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            saveSuccessMessage = if (preserveSuccessMessage) _uiState.value.saveSuccessMessage else null
        )
        viewModelScope.launch {
            repository.getAvailability().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        val remoteSlots = data.weeklyHours.ifEmpty { data.availability }
                        val defaultSlotMin = remoteSlots.firstOrNull()?.slotMinutes?.takeIf { it in listOf(15, 20, 30, 60) } ?: 30

                        // Merge remote slots into all 7 weekdays
                        val merged = allWeekdays.map { dayName ->
                            val match = remoteSlots.find { it.weekday.equals(dayName, ignoreCase = true) }
                            if (match != null) {
                                match.copy(
                                    startTime = match.startTime.formatDoctorTime().ifBlank { "09:00" },
                                    endTime = match.endTime.formatDoctorTime().ifBlank { "17:00" },
                                    slotMinutes = match.slotMinutes.takeIf { it in listOf(15, 20, 30, 60) } ?: defaultSlotMin
                                )
                            } else {
                                DoctorAvailabilitySlot(
                                    weekday = dayName,
                                    startTime = "09:00",
                                    endTime = "17:00",
                                    slotMinutes = defaultSlotMin,
                                    isActive = dayName !in listOf("Saturday", "Sunday")
                                )
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            weeklyHours = merged,
                            slotMinutes = defaultSlotMin,
                            doctorName = data.doctorName,
                            isLoading = false,
                            errorMessage = null,
                            saveSuccessMessage = if (preserveSuccessMessage) _uiState.value.saveSuccessMessage else null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load clinic availability",
                            saveSuccessMessage = if (preserveSuccessMessage) _uiState.value.saveSuccessMessage else null
                        )
                    }
                )
            }
        }
    }

    private fun parseTimeToMinutes(timeStr: String): Int? {
        val clean = timeStr.trim().formatDoctorTime()
        val parts = clean.split(":")
        if (parts.size >= 2) {
            val h = parts[0].toIntOrNull() ?: return null
            val m = parts[1].toIntOrNull() ?: return null
            if (h in 0..23 && m in 0..59) {
                return h * 60 + m
            }
        }
        return null
    }

    private fun saveAvailability() {
        val currentSlots = _uiState.value.weeklyHours
        val currentSlotMinutes = _uiState.value.slotMinutes

        // 1. Validate that end time is after start time before saving
        for (slot in currentSlots) {
            val startMin = parseTimeToMinutes(slot.startTime)
            val endMin = parseTimeToMinutes(slot.endTime)

            if (slot.isActive || (startMin != null && endMin != null && endMin <= startMin)) {
                if (startMin == null || endMin == null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "${slot.weekday}: Please enter valid start and end times."
                    )
                    return
                }

                if (endMin <= startMin) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "${slot.weekday}: End time must be after start time."
                    )
                    return
                }
            }
        }

        // 2. Prepare payload with normalized times and selected slot duration
        val payloadSlots = currentSlots.map { slot ->
            val sMin = parseTimeToMinutes(slot.startTime) ?: 540
            val eMin = parseTimeToMinutes(slot.endTime) ?: 1020
            val safeSlot = if (eMin <= sMin) {
                slot.copy(startTime = "09:00", endTime = "17:00")
            } else {
                slot
            }
            safeSlot.copy(
                startTime = safeSlot.startTime.formatDoctorTime(),
                endTime = safeSlot.endTime.formatDoctorTime(),
                slotMinutes = currentSlotMinutes
            )
        }

        _uiState.value = _uiState.value.copy(
            isSaving = true,
            errorMessage = null,
            saveSuccessMessage = null
        )

        viewModelScope.launch {
            repository.saveAvailability(payloadSlots).collect { result ->
                result.fold(
                    onSuccess = { msg ->
                        val successText = if (!msg.isNullOrBlank()) msg else "Availability saved successfully"
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            saveSuccessMessage = successText,
                            errorMessage = null
                        )
                        // Reload the saved values to confirm persistence
                        loadAvailability(preserveSuccessMessage = true)
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
