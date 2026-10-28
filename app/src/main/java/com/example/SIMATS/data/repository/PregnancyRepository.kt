package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.PregnancyDetailsData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface PregnancyRepository {
    fun getPregnancyDetails(): Flow<PregnancyDetailsData>
    fun savePregnancyDetails(details: PregnancyDetailsData): Flow<Result<Boolean>>
}

class PregnancyRepositoryImpl : PregnancyRepository {
    override fun getPregnancyDetails(): Flow<PregnancyDetailsData> = flow {
        emit(
            PregnancyDetailsData(
                trimesterText = "SECOND TRIMESTER",
                gestationalAgeText = "24 weeks, 3 days",
                countdownText = "15 weeks, 4 days until your estimated due date.",
                estimatedDueDate = "13 Feb 2027",
                lastMenstrualPeriod = "09 May 2026",
                assignedDoctorName = "Dr Amelia Chen",
                assignedDoctorSpecialty = "Obstetrics · Your assigned doctor",
                nextVisitDate = "29 Oct 2026 · 10:30",
                nextVisitClinic = "Willow Clinic"
            )
        )
    }

    override fun savePregnancyDetails(details: PregnancyDetailsData): Flow<Result<Boolean>> = flow {
        delay(800)
        emit(Result.success(true))
    }
}
