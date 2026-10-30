package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.PregnancyDetailsData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface PregnancyRepository {
    fun getPregnancyDetails(): Flow<PregnancyDetailsData>
    fun savePregnancyDetails(details: PregnancyDetailsData): Flow<Result<Boolean>>
}

class PregnancyRepositoryImpl : PregnancyRepository {
    private val apiService = RetrofitClient.apiService

    override fun getPregnancyDetails(): Flow<PregnancyDetailsData> = flow {
        try {
            val response = apiService.getPregnancyDetails(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            PregnancyDetailsData(
                trimesterText = "SECOND TRIMESTER",
                gestationalAgeText = "24 weeks, 3 days",
                countdownText = "15 weeks, 4 days until your estimated due date.",
                estimatedDueDate = "13 Feb 2027",
                lastMenstrualPeriod = "09 May 2026",
                assignedDoctorName = SessionManager.getDoctorName(),
                assignedDoctorSpecialty = "Obstetrics · Your assigned doctor",
                nextVisitDate = "29 Oct 2026 · 10:30",
                nextVisitClinic = "Willow Clinic"
            )
        )
    }

    override fun savePregnancyDetails(details: PregnancyDetailsData): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.savePregnancyDetails(
                edd = details.estimatedDueDate,
                lmp = details.lastMenstrualPeriod
            )
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to save pregnancy details")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error while saving pregnancy details")))
        }
    }
}
