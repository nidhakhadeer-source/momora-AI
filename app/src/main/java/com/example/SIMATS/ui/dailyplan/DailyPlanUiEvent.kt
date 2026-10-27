package com.example.SIMATS.ui.dailyplan

sealed interface DailyPlanUiEvent {
    data class OnMomentActionClicked(val momentId: String) : DailyPlanUiEvent
    data object OnAdjustPlanClicked : DailyPlanUiEvent
    data class OnTabSelected(val tabIndex: Int) : DailyPlanUiEvent
    data object OnBackClicked : DailyPlanUiEvent
    data object OnNavigationHandled : DailyPlanUiEvent
}
