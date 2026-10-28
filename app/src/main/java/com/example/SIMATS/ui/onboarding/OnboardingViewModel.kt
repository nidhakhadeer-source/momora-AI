package com.example.SIMATS.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.OnboardingRepository
import com.example.SIMATS.data.repository.OnboardingRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val repository: OnboardingRepository = OnboardingRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        loadFeatures()
    }

    fun onEvent(event: OnboardingUiEvent) {
        when (event) {
            OnboardingUiEvent.OnContinueClicked -> {
                if (_uiState.value.currentPage < _uiState.value.totalPages - 1) {
                    _uiState.value = _uiState.value.copy(
                        currentPage = _uiState.value.currentPage + 1
                    )
                }
            }
            OnboardingUiEvent.OnBackClicked -> {
                if (_uiState.value.currentPage > 0) {
                    _uiState.value = _uiState.value.copy(
                        currentPage = _uiState.value.currentPage - 1
                    )
                }
            }
            OnboardingUiEvent.OnGetStartedClicked -> {
                // Handle Get Started action (complete onboarding)
            }
            OnboardingUiEvent.OnLoginClicked -> {
                // Navigate to login screen
            }
            OnboardingUiEvent.OnFavoriteClicked -> {
                // Handle favorite action
            }
            OnboardingUiEvent.OnHelpClicked -> {
                // Handle help action
            }
            is OnboardingUiEvent.OnFeatureClicked -> {
                // Handle feature selection
            }
        }
    }

    private fun loadFeatures() {
        viewModelScope.launch {
            repository.getOnboardingFeatures().collect { list ->
                _uiState.value = _uiState.value.copy(features = list)
            }
        }
    }

    class Factory(private val repository: OnboardingRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OnboardingViewModel(repository) as T
        }
    }
}
