package com.example.SIMATS.ui.hydration

sealed interface HydrationLogUiEvent {
    data class OnQuickAddAmountSelected(val amountMl: Int) : HydrationLogUiEvent
    data object OnAddWaterClicked : HydrationLogUiEvent
    data object OnEditGoalClicked : HydrationLogUiEvent
    data class OnToggleReminders(val enabled: Boolean) : HydrationLogUiEvent
    data class OnTabSelected(val tabIndex: Int) : HydrationLogUiEvent
    data object OnBackClicked : HydrationLogUiEvent
    data object OnNavigationHandled : HydrationLogUiEvent
}
