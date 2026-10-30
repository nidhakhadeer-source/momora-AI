package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.UserRole
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AuthRepository {
    fun login(email: String, password: String, role: UserRole): Flow<Result<Boolean>>
}

class AuthRepositoryImpl : AuthRepository {
    private val apiService = RetrofitClient.apiService

    override fun login(email: String, password: String, role: UserRole): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.login(
                email = email.trim(),
                password = password,
                role = role.name.lowercase()
            )

            if (response.success && response.user != null) {
                // Save session in SessionManager
                SessionManager.saveSession(
                    token = response.token,
                    userId = response.user.id,
                    name = response.user.name,
                    email = response.user.email,
                    role = response.user.role,
                    patientCode = response.user.patient_code,
                    doctorName = response.user.doctor_name
                )
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Invalid email or password")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network connection error. Is backend running?")))
        }
    }
}
