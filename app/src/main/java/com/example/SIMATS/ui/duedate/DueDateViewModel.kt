package com.example.SIMATS.ui.duedate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.PregnancyDetailsData
import com.example.SIMATS.data.repository.PregnancyRepository
import com.example.SIMATS.data.repository.PregnancyRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class DueDateViewModel(
    private val repository: PregnancyRepository = PregnancyRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DueDateUiState())
    val uiState: StateFlow<DueDateUiState> = _uiState.asStateFlow()

    init {
        loadPregnancyDetails()
    }

    fun onEvent(event: DueDateUiEvent) {
        when (event) {
            is DueDateUiEvent.OnDueDateChanged -> {
                updateFromEdd(event.dueDate)
            }
            is DueDateUiEvent.OnLmpChanged -> {
                updateFromLmp(event.lmp)
            }
            is DueDateUiEvent.OnTrimesterTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedTrimesterView = event.trimester)
            }
            is DueDateUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            DueDateUiEvent.OnDoctorCardClicked -> {}
            DueDateUiEvent.OnNextVisitClicked -> {
                _uiState.value = _uiState.value.copy(isAppointmentDetailOpen = true)
            }
            DueDateUiEvent.OnDismissAppointmentDetail -> {
                _uiState.value = _uiState.value.copy(isAppointmentDetailOpen = false)
            }
            DueDateUiEvent.OnCancelAppointmentClicked -> {
                _uiState.value = _uiState.value.copy(
                    appointmentStatus = "Cancelled",
                    isAppointmentDetailOpen = false
                )
            }
            DueDateUiEvent.OnSaveClicked -> {
                saveDetails()
            }
            DueDateUiEvent.OnDismissSaveSuccess -> {
                _uiState.value = _uiState.value.copy(isSavedSuccess = false)
            }
            DueDateUiEvent.OnBackClicked -> {}
            DueDateUiEvent.OnNotificationClicked -> {}
            DueDateUiEvent.OnRetryClicked -> {
                loadPregnancyDetails()
            }
        }
    }

    private fun loadPregnancyDetails() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getPregnancyDetails().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        applyLoadedData(data)
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load pregnancy details"
                        )
                    }
                )
            }
        }
    }

    private fun applyLoadedData(data: PregnancyDetailsData) {
        val metrics = PregnancyDataHolder.updatePregnancyData(data)

        _uiState.value = _uiState.value.copy(
            pregnancyData = data,
            estimatedDueDate = metrics.eddFormatted,
            lastMenstrualPeriod = metrics.lmpFormatted,
            gestationalAgeText = metrics.gestationalAgeText,
            gestationalWeeks = metrics.weeks,
            gestationalDays = metrics.days,
            currentTrimester = metrics.trimester,
            currentTrimesterText = metrics.trimesterText,
            countdownText = metrics.countdownText,
            remainingDaysCount = metrics.remainingDays,
            progressFraction = metrics.progress,
            selectedTrimesterView = metrics.trimester,
            isLoading = false,
            errorMessage = null
        )
    }

    private fun updateFromEdd(newEddStr: String) {
        val parsedEdd = PregnancyCalculator.parseDate(newEddStr)
        if (parsedEdd != null) {
            val lmpDate = PregnancyCalculator.calculateLmpFromEdd(parsedEdd)
            val lmpFormatted = PregnancyCalculator.formatDate(lmpDate)
            val metrics = PregnancyCalculator.calculateMetrics(lmpStr = lmpFormatted, eddStr = newEddStr)
            PregnancyDataHolder.updateMetrics(metrics)
            _uiState.value = _uiState.value.copy(
                estimatedDueDate = newEddStr,
                lastMenstrualPeriod = lmpFormatted,
                gestationalAgeText = metrics.gestationalAgeText,
                gestationalWeeks = metrics.weeks,
                gestationalDays = metrics.days,
                currentTrimester = metrics.trimester,
                currentTrimesterText = metrics.trimesterText,
                countdownText = metrics.countdownText,
                remainingDaysCount = metrics.remainingDays,
                progressFraction = metrics.progress
            )
        } else {
            _uiState.value = _uiState.value.copy(estimatedDueDate = newEddStr)
        }
    }

    private fun updateFromLmp(newLmpStr: String) {
        val parsedLmp = PregnancyCalculator.parseDate(newLmpStr)
        if (parsedLmp != null) {
            val eddDate = PregnancyCalculator.calculateEddFromLmp(parsedLmp)
            val eddFormatted = PregnancyCalculator.formatDate(eddDate)
            val metrics = PregnancyCalculator.calculateMetrics(lmpStr = newLmpStr, eddStr = eddFormatted)
            PregnancyDataHolder.updateMetrics(metrics)
            _uiState.value = _uiState.value.copy(
                lastMenstrualPeriod = newLmpStr,
                estimatedDueDate = eddFormatted,
                gestationalAgeText = metrics.gestationalAgeText,
                gestationalWeeks = metrics.weeks,
                gestationalDays = metrics.days,
                currentTrimester = metrics.trimester,
                currentTrimesterText = metrics.trimesterText,
                countdownText = metrics.countdownText,
                remainingDaysCount = metrics.remainingDays,
                progressFraction = metrics.progress
            )
        } else {
            _uiState.value = _uiState.value.copy(lastMenstrualPeriod = newLmpStr)
        }
    }

    private fun saveDetails() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val current = _uiState.value
            val updated = current.pregnancyData?.copy(
                estimatedDueDate = current.estimatedDueDate,
                lastMenstrualPeriod = current.lastMenstrualPeriod,
                gestationalAgeText = current.gestationalAgeText,
                trimesterText = current.currentTrimesterText,
                countdownText = current.countdownText
            ) ?: PregnancyDetailsData(
                trimesterText = current.currentTrimesterText,
                gestationalAgeText = current.gestationalAgeText,
                countdownText = current.countdownText,
                estimatedDueDate = current.estimatedDueDate,
                lastMenstrualPeriod = current.lastMenstrualPeriod,
                assignedDoctorName = "Dr Amelia Chen",
                assignedDoctorSpecialty = "Obstetrics · Your assigned doctor",
                nextVisitDate = "29 Oct 2026 · 10:30",
                nextVisitClinic = "Willow Clinic"
            )

            repository.savePregnancyDetails(updated).collect { result ->
                result.fold(
                    onSuccess = {
                        PregnancyDataHolder.updatePregnancyData(updated)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSavedSuccess = true,
                            pregnancyData = updated
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to save pregnancy details"
                        )
                    }
                )
            }
        }
    }

    private fun calculateMetrics(lmpStr: String, eddStr: String): PregnancyMetrics {
        return PregnancyCalculator.calculateMetrics(lmpStr = lmpStr, eddStr = eddStr)
    }

    class Factory(private val repository: PregnancyRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DueDateViewModel(repository) as T
        }
    }
}
