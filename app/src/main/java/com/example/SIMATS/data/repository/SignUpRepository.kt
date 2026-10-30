package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.PatientRegistrationData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SignUpRepository {
    fun registerPatient(data: PatientRegistrationData): Flow<Result<Boolean>>
}

class SignUpRepositoryImpl : SignUpRepository {
    private val apiService = RetrofitClient.apiService

    override fun registerPatient(data: PatientRegistrationData): Flow<Result<Boolean>> = flow {
        if (!data.isTermsAgreed) {
            emit(Result.failure(IllegalArgumentException("Please accept the terms and privacy notice.")))
            return@flow
        }

        try {
            val response = apiService.signup(
                fullName = data.fullName.trim(),
                email = data.email.trim(),
                password = data.password,
                dateOfBirth = data.dateOfBirth
            )

            if (response.success && response.user != null) {
                SessionManager.saveSession(
                    token = response.token,
                    userId = response.user.id,
                    name = response.user.name,
                    email = response.user.email,
                    role = response.user.role,
                    patientCode = response.user.patient_code
                )
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Registration failed")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Registration connection error")))
        }
    }
}
