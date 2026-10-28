package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.DoctorAccountItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ManageDoctorsRepository {
    fun getDoctors(): Flow<List<DoctorAccountItem>>
    fun toggleDoctorStatus(doctorId: String): Flow<Result<Boolean>>
}

class ManageDoctorsRepositoryImpl : ManageDoctorsRepository {
    override fun getDoctors(): Flow<List<DoctorAccountItem>> = flow {
        emit(
            listOf(
                DoctorAccountItem(
                    id = "1",
                    name = "Dr Amelia Chen",
                    specialtyAndPatientsText = "Obstetrics · 18 assigned patients",
                    emailAndLicenseText = "amelia.chen@willow.example · OBS-19382",
                    isActive = true,
                    isHighlighted = true
                ),
                DoctorAccountItem(
                    id = "2",
                    name = "Dr Daniel Okafor",
                    specialtyAndPatientsText = "Obstetrics · 14 assigned patients",
                    isActive = true
                ),
                DoctorAccountItem(
                    id = "3",
                    name = "Dr Priya Shah",
                    specialtyAndPatientsText = "Maternal medicine · Access disabled",
                    isActive = false
                )
            )
        )
    }

    override fun toggleDoctorStatus(doctorId: String): Flow<Result<Boolean>> = flow {
        delay(500)
        emit(Result.success(true))
    }
}
