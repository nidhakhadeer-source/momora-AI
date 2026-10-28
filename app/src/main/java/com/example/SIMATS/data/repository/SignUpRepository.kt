package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.PatientRegistrationData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SignUpRepository {
    fun registerPatient(data: PatientRegistrationData): Flow<Result<Boolean>>
}

class SignUpRepositoryImpl : SignUpRepository {
    override fun registerPatient(data: PatientRegistrationData): Flow<Result<Boolean>> = flow {
        delay(1000)
        if (data.fullName.isNotBlank() && data.email.isNotBlank() && data.password.isNotBlank() && data.isTermsAgreed) {
            emit(Result.success(true))
        } else {
            emit(Result.failure(IllegalArgumentException("Please fill out all required fields and accept terms.")))
        }
    }
}
