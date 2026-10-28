package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.PatientMedicalHistoryData
import com.example.SIMATS.data.model.PatientMedicationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface PatientHistoryRepository {
    fun getPatientHistory(patientId: String): Flow<PatientMedicalHistoryData>
}

class PatientHistoryRepositoryImpl : PatientHistoryRepository {
    override fun getPatientHistory(patientId: String): Flow<PatientMedicalHistoryData> = flow {
        emit(
            PatientMedicalHistoryData(
                patientName = "Maya Patel",
                patientInitials = "MP",
                gestationalAge = "24w 3d",
                dueDateText = "Due 13 Feb 2027",
                patientIdText = "P-13042",
                clinicalBackgroundText = "Age 29 · O positive · First pregnancy (G1 P0). No known drug allergies or chronic conditions reported.",
                patientConfirmedDateText = "Patient confirmed · 05 Oct 2026",
                medications = listOf(
                    PatientMedicationItem(
                        id = "1",
                        name = "Prenatal vitamin",
                        dosageDetails = "1 tablet daily · 08:00 · Taken today",
                        isTakenToday = true
                    ),
                    PatientMedicationItem(
                        id = "2",
                        name = "Vitamin D · 400 IU",
                        dosageDetails = "1 tablet daily · 20:00",
                        isTakenToday = false
                    )
                ),
                recentCareRecordTitle = "Complete blood count",
                recentCareRecordDetails = "28 Oct 2026 · Reviewed 27 Oct 2026"
            )
        )
    }
}
