package com.example.SIMATS.ui.splash

import com.example.SIMATS.data.model.UserAuthState

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data class Success(val authState: UserAuthState) : SplashUiState
}
