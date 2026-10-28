package com.example.SIMATS.ui.home

sealed interface HomeUiEvent {
    data class OnNavTabSelected(val index: Int) : HomeUiEvent
    data object OnNotificationClicked : HomeUiEvent
    data object OnPregnancyDetailsClicked : HomeUiEvent
    data object OnBloodPressureCardClicked : HomeUiEvent
    data object OnWeightCardClicked : HomeUiEvent
    data object OnAskAiClicked : HomeUiEvent
    data object OnBabyGrowthClicked : HomeUiEvent
    data object OnNutritionClicked : HomeUiEvent
    data object OnViewAllDailyCareClicked : HomeUiEvent
}
