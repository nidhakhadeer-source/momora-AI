package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.AddDoctorData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AddDoctorRepository {
    fun getDefaultDoctorFormData(): Flow<AddDoctorData>
    fun createDoctorAccount(
        name: String,
        email: String,
        specialty: String,
        licenseId: String,
        clinic: String,
        sendActivationEmail: Boolean
    ): Flow<Result<Boolean>>
}

class AddDoctorRepositoryImpl : AddDoctorRepository {
    private val apiService = RetrofitClient.apiService

    override fun getDefaultDoctorFormData(): Flow<AddDoctorData> = flow {
        try {
            val response = apiService.getAddDoctorFormData()
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            AddDoctorData(
                defaultName = "Dr Olivia Bennett",
                defaultEmail = "olivia.bennett@willow.example",
                defaultSpecialty = "Obstetrics",
                defaultLicenseId = "OBS-28482",
                defaultClinic = "Willow Maternity Clinic",
                availableClinics = listOf(
                    "Willow Maternity Clinic",
                    "SIMATS Women's Care",
                    "City Central Maternity Center"
                )
            )
        )
    }

    override fun createDoctorAccount(
        name: String,
        email: String,
        specialty: String,
        licenseId: String,
        clinic: String,
        sendActivationEmail: Boolean
    ): Flow<Result<Boolean>> = flow {
        try {
            val fields = mapOf(
                "name" to name,
                "email" to email,
                "specialty" to specialty,
                "licenseId" to licenseId,
                "clinic" to clinic,
                "sendActivationEmail" to if (sendActivationEmail) "1" else "0"
            )
            val response = apiService.createDoctorAccount(fields)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to create doctor account")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error while creating doctor account")))
        }
    }
}
