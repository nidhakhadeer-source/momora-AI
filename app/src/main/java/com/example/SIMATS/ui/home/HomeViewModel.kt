package com.example.SIMATS.ui.home

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
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
                if (event.tabIndex == 1) {
                    _uiState.update { it.copy(navigateToRoute = Screen.Timeline.route) }
                } else {
                    _uiState.update { it.copy(selectedTab = event.tabIndex) }
                }
            }
            HomeUiEvent.OnStartCheckInClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.DailyCheckIn.route) }
            }
            HomeUiEvent.OnViewTrackersClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Timeline.route) }
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
