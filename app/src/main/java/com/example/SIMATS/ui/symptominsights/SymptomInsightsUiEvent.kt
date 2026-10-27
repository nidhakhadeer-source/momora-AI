package com.example.SIMATS.ui.symptominsights

sealed interface SymptomInsightsUiEvent {
    data class OnTipItemClicked(val tip: GentleTipItem) : SymptomInsightsUiEvent
    data object OnAskFollowUpClicked : SymptomInsightsUiEvent
    data object OnAddQuestionsClicked : SymptomInsightsUiEvent
    data class OnTabSelected(val tabIndex: Int) : SymptomInsightsUiEvent
    data object OnBackClicked : SymptomInsightsUiEvent
    data object OnNavigationHandled : SymptomInsightsUiEvent
}
