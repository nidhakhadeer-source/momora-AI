package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.UserAuthState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SplashRepository {
    fun checkAuthState(): Flow<UserAuthState>
}

class SplashRepositoryImpl : SplashRepository {
    override fun checkAuthState(): Flow<UserAuthState> = flow {
        delay(800)
        val loggedIn = SessionManager.isLoggedIn()
        val role = SessionManager.getUserRole()
        if (loggedIn) {
            emit(UserAuthState.Authenticated(role))
        } else {
            emit(UserAuthState.Unauthenticated)
        }
    }
}
