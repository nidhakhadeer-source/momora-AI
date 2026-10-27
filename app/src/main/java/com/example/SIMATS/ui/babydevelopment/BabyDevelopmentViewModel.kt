package com.example.SIMATS.ui.babydevelopment

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BabyDevelopmentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BabyDevelopmentUiState())
    val uiState: StateFlow<BabyDevelopmentUiState> = _uiState.asStateFlow()

    fun onEvent(event: BabyDevelopmentUiEvent) {
        when (event) {
            BabyDevelopmentUiEvent.OnToggleBookmark -> {
                _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
            }
            is BabyDevelopmentUiEvent.OnWonderItemClicked -> {
                // Action for inspecting wonder item detail
            }
            is BabyDevelopmentUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            BabyDevelopmentUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            BabyDevelopmentUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
