package com.example.SIMATS.ui.profile

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileSetupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    fun onEvent(event: ProfileSetupUiEvent) {
        when (event) {
            is ProfileSetupUiEvent.OnPreferredNameChanged -> {
                _uiState.update { it.copy(preferredName = event.name) }
            }
            is ProfileSetupUiEvent.OnDateOfBirthChanged -> {
                _uiState.update { it.copy(dateOfBirth = event.dob) }
            }
            is ProfileSetupUiEvent.OnFirstPregnancyChanged -> {
                _uiState.update { it.copy(isFirstPregnancy = event.isFirst) }
            }
            is ProfileSetupUiEvent.OnPregnancyTypeChanged -> {
                _uiState.update { it.copy(pregnancyType = event.type) }
            }
            is ProfileSetupUiEvent.OnCareTeamChanged -> {
                _uiState.update { it.copy(careTeam = event.careTeam) }
            }
            ProfileSetupUiEvent.OnContinueClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.DueDate.route) }
            }
            ProfileSetupUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            ProfileSetupUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
