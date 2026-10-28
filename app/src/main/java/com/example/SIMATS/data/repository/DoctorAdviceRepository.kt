package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.DoctorAdviceData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DoctorAdviceRepository {
    fun getAdviceData(patientId: String): Flow<DoctorAdviceData>
    fun sendAdvice(subject: String, adviceText: String, saveCopy: Boolean): Flow<Result<Boolean>>
}

class DoctorAdviceRepositoryImpl : DoctorAdviceRepository {
    override fun getAdviceData(patientId: String): Flow<DoctorAdviceData> = flow {
        emit(
            DoctorAdviceData(
                doctorName = "Dr Amelia Chen",
                patientName = "Maya Patel",
                patientInitials = "MP",
                gestationalAge = "24w 3d",
                dueDateText = "Due 13 Feb 2027",
                patientIdText = "P-13042",
                patientMessageTimeText = "PATIENT MESSAGE · 27 OCT, 08:35",
                patientMessageQuote = "\"I've had mild heartburn after lunch. What can I try?\"",
                defaultSubject = "Heartburn: gentle steps & follow-up",
                defaultAdviceText = "Hi Maya, try smaller meals and stay upright after eating. Let's review this at your 29 Oct visit. Contact the clinic sooner if symptoms persist or worsen.\n\nFor severe chest or abdominal pain, seek urgent maternity care or local emergency services."
            )
        )
    }

    override fun sendAdvice(
        subject: String,
        adviceText: String,
        saveCopy: Boolean
    ): Flow<Result<Boolean>> = flow {
        delay(800)
        emit(Result.success(true))
    }
}
