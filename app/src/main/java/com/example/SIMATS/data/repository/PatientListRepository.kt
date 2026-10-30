package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AssignedPatientItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface PatientListRepository {
    fun getAssignedPatients(): Flow<List<AssignedPatientItem>>
}

class PatientListRepositoryImpl : PatientListRepository {
    private val apiService = RetrofitClient.apiService

    override fun getAssignedPatients(): Flow<List<AssignedPatientItem>> = flow {
        try {
            val response = apiService.getAssignedPatients(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            listOf(
                AssignedPatientItem(
                    id = "5",
                    patientIdText = "P-13042",
                    name = "Maya Patel",
                    gestationalAge = "24w 3d",
                    dueDateText = "Due 13 Feb 2027",
                    latestVitalsNote = "Latest BP 112/72 mmHg · Updated today",
                    needsReview = false,
                    isHighlighted = true
                ),
                AssignedPatientItem(
                    id = "101",
                    patientIdText = "P-1098",
                    name = "Laila Ahmed",
                    gestationalAge = "30w 2d",
                    needsReview = true,
                    reviewType = "New ultrasound report"
                ),
                AssignedPatientItem(
                    id = "102",
                    patientIdText = "P-1011",
                    name = "Sofia Reyes",
                    gestationalAge = "18w 5d",
                    needsReview = true,
                    reviewType = "New lab report"
                ),
                AssignedPatientItem(
                    id = "103",
                    patientIdText = "P-1027",
                    name = "Nora Williams",
                    gestationalAge = "38w 0d",
                    visitNote = "Visit today",
                    needsReview = false
                ),
                AssignedPatientItem(
                    id = "104",
                    patientIdText = "P-1093",
                    name = "Emma Wilson",
                    gestationalAge = "12w 1d",
                    needsReview = false
                )
            )
        )
    }
}
