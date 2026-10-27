package com.example.SIMATS.ui.hydration

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HydrationLogViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HydrationLogUiState())
    val uiState: StateFlow<HydrationLogUiState> = _uiState.asStateFlow()

    fun onEvent(event: HydrationLogUiEvent) {
        when (event) {
            is HydrationLogUiEvent.OnQuickAddAmountSelected -> {
                _uiState.update { it.copy(selectedAddAmountMl = event.amountMl) }
            }
            HydrationLogUiEvent.OnAddWaterClicked -> {
                _uiState.update { state ->
                    val addedLiters = state.selectedAddAmountMl / 1000f
                    val newTotalLiters = state.currentLoggedLiters + addedLiters
                    val newProgress = (newTotalLiters / state.goalLiters).coerceAtMost(1.0f)
                    val remainingMl = ((state.goalLiters - newTotalLiters) * 1000).toInt().coerceAtLeast(0)
                    val newSip = SipEntryItem(
                        id = System.currentTimeMillis().toString(),
                        timeText = "Just now · Water",
                        amountText = "${state.selectedAddAmountMl} mL"
                    )
                    state.copy(
                        currentLoggedLiters = newTotalLiters,
                        progress = newProgress,
                        progressFooterText = if (remainingMl > 0) "$remainingMl mL to your personal goal · Edit goal" else "Goal completed! · Edit goal",
                        sipsList = listOf(newSip) + state.sipsList,
                        navigateToRoute = Screen.NutritionPlan.route
                    )
                }
            }
            HydrationLogUiEvent.OnEditGoalClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.NutritionPlan.route) }
            }
            is HydrationLogUiEvent.OnToggleReminders -> {
                _uiState.update { it.copy(isRemindersEnabled = event.enabled) }
            }
            is HydrationLogUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            HydrationLogUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            HydrationLogUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
