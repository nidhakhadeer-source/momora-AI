package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.MedicationReminderData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface MedicationRepository {
    fun getMedicationReminders(): Flow<Result<MedicationReminderData>>
    fun markAsTaken(medicationId: Int): Flow<Result<Boolean>>
}

class MedicationRepositoryImpl : MedicationRepository {
    private val apiService = RetrofitClient.apiService

    override fun getMedicationReminders(): Flow<Result<MedicationReminderData>> = flow {
        try {
            val response = apiService.getMedicationReminders(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load medication reminders")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network error loading medication reminders")))
            }
        }
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
