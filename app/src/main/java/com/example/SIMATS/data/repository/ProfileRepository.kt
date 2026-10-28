package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.PatientProfileData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ProfileRepository {
    fun getPatientProfile(): Flow<PatientProfileData>
    fun savePatientProfile(profile: PatientProfileData): Flow<Result<Boolean>>
}

class ProfileRepositoryImpl : ProfileRepository {
    override fun getPatientProfile(): Flow<PatientProfileData> = flow {
        emit(
            PatientProfileData(
                avatarInitials = "MP",
                patientName = "Maya Patel",
                patientId = "P-13042",
                fullName = "Maya Patel",
                email = "maya.patel@example.com",
                dateOfBirth = "14 May 1997",
                bloodGroup = "O positive",
                isCareTeamSharingOn = true,
                careTeamDoctorName = "Dr Amelia Chen",
                reminderSummary = "Medication and appointment alerts"
            )
        )
    }

    override fun savePatientProfile(profile: PatientProfileData): Flow<Result<Boolean>> = flow {
        delay(800)
        emit(Result.success(true))
    }
}
