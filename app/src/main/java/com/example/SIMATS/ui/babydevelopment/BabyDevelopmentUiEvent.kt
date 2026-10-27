package com.example.SIMATS.ui.babydevelopment

sealed interface BabyDevelopmentUiEvent {
    data object OnToggleBookmark : BabyDevelopmentUiEvent
    data class OnWonderItemClicked(val wonder: DevelopmentWonderItem) : BabyDevelopmentUiEvent
    data class OnTabSelected(val tabIndex: Int) : BabyDevelopmentUiEvent
    data object OnBackClicked : BabyDevelopmentUiEvent
    data object OnNavigationHandled : BabyDevelopmentUiEvent
}
