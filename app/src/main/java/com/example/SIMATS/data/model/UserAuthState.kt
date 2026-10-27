package com.example.SIMATS.data.model

sealed interface UserAuthState {
    data object Unauthenticated : UserAuthState
    data object Authenticated : UserAuthState
    data object OnboardingCompleted : UserAuthState
}
