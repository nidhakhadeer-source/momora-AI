package com.example.SIMATS.ui.babydevelopment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.BabyDevelopmentRepository
import com.example.SIMATS.data.repository.BabyDevelopmentRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BabyDevelopmentViewModel(
    private val repository: BabyDevelopmentRepository = BabyDevelopmentRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BabyDevelopmentUiState())
    val uiState: StateFlow<BabyDevelopmentUiState> = _uiState.asStateFlow()

    init {
        loadData(24)
    }

    fun onEvent(event: BabyDevelopmentUiEvent) {
        when (event) {
            is BabyDevelopmentUiEvent.OnWeekChanged -> {
                _uiState.value = _uiState.value.copy(currentWeek = event.week)
                loadData(event.week)
            }
            BabyDevelopmentUiEvent.OnPreviousWeekClicked -> {
                if (_uiState.value.currentWeek > 1) {
                    val prevWeek = _uiState.value.currentWeek - 1
                    _uiState.value = _uiState.value.copy(currentWeek = prevWeek)
                    loadData(prevWeek)
                }
            }
            BabyDevelopmentUiEvent.OnNextWeekClicked -> {
                if (_uiState.value.currentWeek < 40) {
                    val nextWeek = _uiState.value.currentWeek + 1
                    _uiState.value = _uiState.value.copy(currentWeek = nextWeek)
                    loadData(nextWeek)
                }
            }
            is BabyDevelopmentUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            BabyDevelopmentUiEvent.OnTimelineClicked -> {}
            BabyDevelopmentUiEvent.OnBackClicked -> {}
            BabyDevelopmentUiEvent.OnNotificationClicked -> {}
            is BabyDevelopmentUiEvent.OnHighlightClicked -> {}
        }
    }

    private fun loadData(week: Int) {
        viewModelScope.launch {
            repository.getDevelopmentData(week).collect { data ->
                _uiState.value = _uiState.value.copy(developmentData = data)
            }
        }
    }

    class Factory(private val repository: BabyDevelopmentRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BabyDevelopmentViewModel(repository) as T
        }
    }
}
