package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.PatientProfileData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ProfileRepository {
    fun getPatientProfile(): Flow<PatientProfileData>
    fun savePatientProfile(profile: PatientProfileData): Flow<Result<Boolean>>
}

class ProfileRepositoryImpl : ProfileRepository {
    private val apiService = RetrofitClient.apiService

    override fun getPatientProfile(): Flow<PatientProfileData> = flow {
        try {
            val response = apiService.getProfile(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        // Fallback
        emit(
            PatientProfileData(
                avatarInitials = "MP",
                patientName = SessionManager.getUserName(),
                patientId = SessionManager.getPatientCode(),
                fullName = SessionManager.getUserName(),
                email = SessionManager.getUserEmail(),
                dateOfBirth = "14 May 1997",
                bloodGroup = "O positive",
                isCareTeamSharingOn = true,
                careTeamDoctorName = SessionManager.getDoctorName(),
                reminderSummary = "Medication and appointment alerts"
            )
        )
    }

    override fun savePatientProfile(profile: PatientProfileData): Flow<Result<Boolean>> = flow {
        try {
            val fields = mapOf(
                "user_id" to SessionManager.getUserId().toString(),
                "fullName" to profile.fullName,
                "email" to profile.email,
                "dateOfBirth" to profile.dateOfBirth,
                "bloodGroup" to profile.bloodGroup,
                "isCareTeamSharingOn" to if (profile.isCareTeamSharingOn) "1" else "0"
            )
            val response = apiService.updateProfile(fields)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to save profile")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error while saving profile")))
        }
    }
}
