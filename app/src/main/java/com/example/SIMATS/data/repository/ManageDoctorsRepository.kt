package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.DoctorAccountItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ManageDoctorsRepository {
    fun getDoctors(): Flow<List<DoctorAccountItem>>
    fun toggleDoctorStatus(doctorId: String): Flow<Result<Boolean>>
}

class ManageDoctorsRepositoryImpl : ManageDoctorsRepository {
    private val apiService = RetrofitClient.apiService

    override fun getDoctors(): Flow<List<DoctorAccountItem>> = flow {
        try {
            val response = apiService.getManageDoctors()
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            listOf(
                DoctorAccountItem(
                    id = "3",
                    name = "Dr Amelia Chen",
                    specialtyAndPatientsText = "Obstetrics · 18 assigned patients",
                    emailAndLicenseText = "amelia.chen@willow.example · OBS-19382",
                    isActive = true,
                    isHighlighted = true
                ),
                DoctorAccountItem(
                    id = "201",
                    name = "Dr Daniel Okafor",
                    specialtyAndPatientsText = "Obstetrics · 14 assigned patients",
                    emailAndLicenseText = "daniel.okafor@willow.example · OBS-20491",
                    isActive = true
                ),
                DoctorAccountItem(
                    id = "202",
                    name = "Dr Priya Shah",
                    specialtyAndPatientsText = "Maternal medicine · Access disabled",
                    emailAndLicenseText = "priya.shah@willow.example · OBS-11029",
                    isActive = false
                )
            )
        )
    }

    override fun toggleDoctorStatus(doctorId: String): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.toggleDoctorStatus(doctorId)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to toggle status")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error toggling doctor status")))
        }
    }
}
