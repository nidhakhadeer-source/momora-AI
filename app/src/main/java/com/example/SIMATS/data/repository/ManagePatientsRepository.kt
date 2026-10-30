package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.PatientAccountItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ManagePatientsRepository {
    fun getPatientAccounts(): Flow<List<PatientAccountItem>>
    fun togglePatientAccess(patientId: String): Flow<Result<Boolean>>
}

class ManagePatientsRepositoryImpl : ManagePatientsRepository {
    private val apiService = RetrofitClient.apiService

    override fun getPatientAccounts(): Flow<List<PatientAccountItem>> = flow {
        try {
            val response = apiService.getManagePatients()
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            listOf(
                PatientAccountItem(
                    id = "5",
                    name = "Maya Patel",
                    patientIdText = "P-1042",
                    assignedDoctorText = "Assigned doctor: Dr Amelia Chen",
                    emailText = "maya.patel@example.com",
                    isActive = true,
                    isHighlighted = true
                ),
                PatientAccountItem(
                    id = "301",
                    name = "Laila Ahmed",
                    patientIdText = "P-1030",
                    assignedDoctorText = "Assigned doctor: Dr Amelia Chen",
                    emailText = "laila.ahmed@example.com",
                    isActive = true
                ),
                PatientAccountItem(
                    id = "302",
                    name = "Emma Wilson",
                    patientIdText = "P-1093",
                    assignedDoctorText = "Assigned doctor: Dr Amelia Chen",
                    emailText = "emma.wilson@example.com",
                    isActive = true
                ),
                PatientAccountItem(
                    id = "303",
                    name = "Isla Martin",
                    patientIdText = "P-0986",
                    assignedDoctorText = "Account access disabled",
                    emailText = "isla.martin@example.com",
                    isActive = false
                )
            )
        )
    }

    override fun togglePatientAccess(patientId: String): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.togglePatientAccess(patientId)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to update access")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error updating patient access")))
        }
    }
}
