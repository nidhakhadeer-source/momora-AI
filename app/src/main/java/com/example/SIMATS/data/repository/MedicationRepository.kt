package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.MedicationItem
import com.example.SIMATS.data.model.MedicationReminderData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface MedicationRepository {
    fun getMedicationReminders(): Flow<MedicationReminderData>
    fun markAsTaken(medicationId: Int): Flow<Result<Boolean>>
}

class MedicationRepositoryImpl : MedicationRepository {
    override fun getMedicationReminders(): Flow<MedicationReminderData> = flow {
        emit(
            MedicationReminderData(
                dateText = "Tuesday, 27 Oct 2026",
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
        delay(500)
        emit(Result.success(true))
    }
}
