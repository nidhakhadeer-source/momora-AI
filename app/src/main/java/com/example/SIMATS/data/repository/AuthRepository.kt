package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AuthRepository {
    fun login(email: String, password: String, role: UserRole): Flow<Result<Boolean>>
}

class AuthRepositoryImpl : AuthRepository {
    override fun login(email: String, password: String, role: UserRole): Flow<Result<Boolean>> = flow {
        delay(1000)
        if (email.isNotBlank() && password.isNotBlank()) {
            emit(Result.success(true))
        } else {
            emit(Result.failure(IllegalArgumentException("Please enter valid credentials")))
        }
    }
}
