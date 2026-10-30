package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorAdviceData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DoctorAdviceRepository {
    fun getAdviceData(patientId: String): Flow<DoctorAdviceData>
    fun sendAdvice(subject: String, adviceText: String, saveCopy: Boolean): Flow<Result<Boolean>>
}

class DoctorAdviceRepositoryImpl : DoctorAdviceRepository {
    private val apiService = RetrofitClient.apiService

    override fun getAdviceData(patientId: String): Flow<DoctorAdviceData> = flow {
        try {
            val response = apiService.getDoctorAdviceData(patientId)
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            DoctorAdviceData(
                doctorName = SessionManager.getUserName().ifBlank { "Dr Amelia Chen" },
                patientName = "Maya Patel",
                patientInitials = "MP",
                gestationalAge = "24w 3d",
                dueDateText = "Due 13 Feb 2027",
                patientIdText = "P-13042",
                patientMessageTimeText = "PATIENT MESSAGE · 29 OCT, 08:35",
                patientMessageQuote = "\"I've had mild heartburn after lunch. What can I try?\"",
                defaultSubject = "Heartburn: gentle steps & follow-up",
                defaultAdviceText = "Hi Maya, try smaller meals and stay upright after eating. Let's review this at your upcoming visit. Contact the clinic sooner if symptoms persist or worsen.\n\nFor severe chest or abdominal pain, seek urgent maternity care or local emergency services."
            )
        )
    }

    override fun sendAdvice(
        subject: String,
        adviceText: String,
        saveCopy: Boolean
    ): Flow<Result<Boolean>> = flow {
        try {
            val fields = mapOf(
                "subject" to subject,
                "adviceText" to adviceText,
                "saveCopy" to if (saveCopy) "1" else "0",
                "patientId" to "5"
            )
            val response = apiService.sendDoctorAdvice(fields)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to send advice")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error while sending advice")))
        }
    }
}
