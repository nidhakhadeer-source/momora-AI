package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.PatientAccountItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ManagePatientsRepository {
    fun getPatientAccounts(): Flow<List<PatientAccountItem>>
    fun togglePatientAccess(patientId: String): Flow<Result<Boolean>>
}

class ManagePatientsRepositoryImpl : ManagePatientsRepository {
    override fun getPatientAccounts(): Flow<List<PatientAccountItem>> = flow {
        emit(
            listOf(
                PatientAccountItem(
                    id = "1",
                    name = "Maya Patel",
                    patientIdText = "P-1042",
                    assignedDoctorText = "Assigned doctor: Dr Amelia Chen",
                    emailText = "maya.patel@example.com",
                    isActive = true,
                    isHighlighted = true
                ),
                PatientAccountItem(
                    id = "2",
                    name = "Laila Ahmed",
                    patientIdText = "P-1030",
                    assignedDoctorText = "Assigned doctor: Dr Amelia Chen",
                    isActive = true
                ),
                PatientAccountItem(
                    id = "3",
                    name = "Emma Wilson",
                    patientIdText = "P-1093",
                    assignedDoctorText = "Assigned doctor: Dr Amelia Chen",
                    isActive = true
                ),
                PatientAccountItem(
                    id = "4",
                    name = "Isla Martin",
                    patientIdText = "P-0986",
                    assignedDoctorText = "Account access disabled",
                    isActive = false
                )
            )
        )
    }

    override fun togglePatientAccess(patientId: String): Flow<Result<Boolean>> = flow {
        delay(500)
        emit(Result.success(true))
    }
}
