package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.SymptomEntryData
import com.example.SIMATS.data.model.SymptomSeverity
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SymptomRepository {
    fun getSymptomEntryData(): Flow<SymptomEntryData>
    fun saveSymptom(data: SymptomEntryData): Flow<Result<Boolean>>
}

class SymptomRepositoryImpl : SymptomRepository {
    private val apiService = RetrofitClient.apiService

    override fun getSymptomEntryData(): Flow<SymptomEntryData> = flow {
        try {
            val response = apiService.getSymptomData(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            SymptomEntryData(
                symptomName = "Heartburn after meals",
                severity = SymptomSeverity.MILD,
                startedText = "29 Oct, 13:00",
                durationText = "About 20 min",
                aiGuidanceText = "Smaller meals and sitting upright after eating may help. Speak with your maternity team if it persists, worsens, or worries you.",
                availableSymptoms = listOf(
                    "Heartburn after meals",
                    "Morning sickness / Nausea",
                    "Lower back tension",
                    "Mild fatigue",
                    "Swollen ankles"
                )
            )
        )
    }

    override fun saveSymptom(data: SymptomEntryData): Flow<Result<Boolean>> = flow {
        try {
            val fields = mapOf(
                "symptomName" to data.symptomName,
                "severity" to data.severity.name,
                "startedText" to data.startedText,
                "durationText" to data.durationText
            )
            val response = apiService.saveSymptom(fields)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to log symptom")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error while logging symptom")))
        }
    }
}
