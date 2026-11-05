package com.example.SIMATS.data.model

sealed interface UserAuthState {
    data object Unauthenticated : UserAuthState
    data class Authenticated(val role: UserRole = UserRole.PATIENT) : UserAuthState
    data object OnboardingCompleted : UserAuthState
}
