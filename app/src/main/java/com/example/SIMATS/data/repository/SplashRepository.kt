package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.UserAuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface SplashRepository {
    fun getUserAuthState(): Flow<UserAuthState>
}

class SplashRepositoryImpl : SplashRepository {
    private val _authState = MutableStateFlow<UserAuthState>(UserAuthState.Unauthenticated)

    override fun getUserAuthState(): Flow<UserAuthState> = _authState.asStateFlow()
}
