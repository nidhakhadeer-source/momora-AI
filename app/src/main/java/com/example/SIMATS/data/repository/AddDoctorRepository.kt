package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.AddDoctorData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AddDoctorRepository {
    fun getDefaultDoctorFormData(): Flow<AddDoctorData>
    fun createDoctorAccount(
        name: String,
        email: String,
        specialty: String,
        licenseId: String,
        clinic: String,
        sendActivationEmail: Boolean
    ): Flow<Result<Boolean>>
}

class AddDoctorRepositoryImpl : AddDoctorRepository {
    override fun getDefaultDoctorFormData(): Flow<AddDoctorData> = flow {
        emit(
            AddDoctorData(
                defaultName = "Dr Olivia Bennett",
                defaultEmail = "olivia.bennett@willow.example",
                defaultSpecialty = "Obstetrics",
                defaultLicenseId = "OBS-28482",
                defaultClinic = "Willow Maternity Clinic",
                availableClinics = listOf(
                    "Willow Maternity Clinic",
                    "SIMATS Women's Care",
                    "City Central Maternity Center"
                )
            )
        )
    }

    override fun createDoctorAccount(
        name: String,
        email: String,
        specialty: String,
        licenseId: String,
        clinic: String,
        sendActivationEmail: Boolean
    ): Flow<Result<Boolean>> = flow {
        delay(1000)
        emit(Result.success(true))
    }
}
