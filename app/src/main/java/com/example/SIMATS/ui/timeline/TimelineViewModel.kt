package com.example.SIMATS.ui.timeline

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TimelineViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    fun onEvent(event: TimelineUiEvent) {
        when (event) {
            is TimelineUiEvent.OnTrimesterSelected -> {
                _uiState.update { it.copy(selectedTrimester = event.trimester) }
            }
            is TimelineUiEvent.OnWeekItemClicked -> {
                // Action for selecting a week item
            }
            TimelineUiEvent.OnExploreWeekClicked -> {
                // Action for exploring current week
            }
            is TimelineUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            TimelineUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            TimelineUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
