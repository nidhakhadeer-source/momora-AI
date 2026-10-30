package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.MedicationItem
import com.example.SIMATS.data.model.MedicationReminderData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface MedicationRepository {
    fun getMedicationReminders(): Flow<MedicationReminderData>
    fun markAsTaken(medicationId: Int): Flow<Result<Boolean>>
}

class MedicationRepositoryImpl : MedicationRepository {
    private val apiService = RetrofitClient.apiService

    override fun getMedicationReminders(): Flow<MedicationReminderData> = flow {
        try {
            val response = apiService.getMedicationReminders(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            MedicationReminderData(
                dateText = "Thursday, 29 Oct 2026",
                progressStatusText = "1 OF 2 TAKEN · EVENING REMINDER SET",
                medications = listOf(
                    MedicationItem(
                        id = 1,
                        name = "Prenatal vitamin",
                        dosageAndTime = "1 tablet · Daily at 08:00 · With food",
                        isTaken = true,
                        takenTimeNote = "Marked taken today at 08:05"
                    ),
                    MedicationItem(
                        id = 2,
                        name = "Vitamin D · 400 IU",
                        dosageAndTime = "1 tablet · Daily at 20:00",
                        doctorPrescriptionNote = "As prescribed by Dr. Amelia Chen.",
                        isTaken = false,
                        dueLaterText = "Due later"
                    )
                )
            )
        )
    }

    override fun markAsTaken(medicationId: Int): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.markMedicationTaken(medicationId)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to update medication")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error while updating medication")))
        }
    }
}
