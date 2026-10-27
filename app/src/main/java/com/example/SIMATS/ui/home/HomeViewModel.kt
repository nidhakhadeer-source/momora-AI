package com.example.SIMATS.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            HomeUiEvent.OnStartCheckInClicked -> {
                // Action for daily check-in
            }
            HomeUiEvent.OnViewTrackersClicked -> {
                _uiState.update { it.copy(selectedTab = 1) }
            }
            HomeUiEvent.OnPrenatalVitaminClicked -> {
                // Action for prenatal vitamin
            }
            HomeUiEvent.OnAntenatalVisitClicked -> {
                // Action for antenatal visit
            }
            HomeUiEvent.OnOpenDailyPlanClicked -> {
                // Action for daily plan
            }
            HomeUiEvent.OnSavedClicked -> {
                // Action for saved items
            }
            HomeUiEvent.OnNotificationsClicked -> {
                // Action for notifications
            }
            HomeUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null) }
            }
        }
    }
}
