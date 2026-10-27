package com.example.SIMATS.ui.profile

sealed interface ProfileSetupUiEvent {
    data class OnPreferredNameChanged(val name: String) : ProfileSetupUiEvent
    data class OnDateOfBirthChanged(val dob: String) : ProfileSetupUiEvent
    data class OnFirstPregnancyChanged(val isFirst: Boolean) : ProfileSetupUiEvent
    data class OnPregnancyTypeChanged(val type: String) : ProfileSetupUiEvent
    data class OnCareTeamChanged(val careTeam: String) : ProfileSetupUiEvent
    data object OnContinueClicked : ProfileSetupUiEvent
    data object OnBackClicked : ProfileSetupUiEvent
    data object OnNavigationHandled : ProfileSetupUiEvent
}
