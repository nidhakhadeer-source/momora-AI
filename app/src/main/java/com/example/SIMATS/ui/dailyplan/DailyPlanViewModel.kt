package com.example.SIMATS.ui.dailyplan

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DailyPlanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DailyPlanUiState())
    val uiState: StateFlow<DailyPlanUiState> = _uiState.asStateFlow()

    fun onEvent(event: DailyPlanUiEvent) {
        when (event) {
            is DailyPlanUiEvent.OnMomentActionClicked -> {
                _uiState.update { state ->
                    val updatedMoments = state.momentsList.map { item ->
                        if (item.id == event.momentId) {
                            item.copy(isCompleted = !item.isCompleted, actionText = if (!item.isCompleted) "Done" else "Start")
                        } else item
                    }
                    val completedCount = updatedMoments.count { it.isCompleted }
                    state.copy(
                        momentsList = updatedMoments,
                        progress = completedCount / 4f,
                        progressFooterText = "$completedCount of 4 moments complete · Go at your own pace"
                    )
                }
            }
            DailyPlanUiEvent.OnAdjustPlanClicked -> {
                // Action for adjusting today's plan
            }
            is DailyPlanUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            DailyPlanUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            DailyPlanUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
