package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AssignedPatientItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

data class PaginatedPatients(
    val patients: List<AssignedPatientItem>,
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

interface PatientListRepository {
    fun getAssignedPatients(page: Int = 1, limit: Int = 10): Flow<Result<PaginatedPatients>>
}

class PatientListRepositoryImpl : PatientListRepository {
    private val apiService = RetrofitClient.apiService

    override fun getAssignedPatients(page: Int, limit: Int): Flow<Result<PaginatedPatients>> = flow {
        try {
            val response = apiService.getAssignedPatients(
                page = page,
                limit = limit,
                userId = SessionManager.getUserId()
            )
            if (response.success && response.data != null) {
                emit(Result.success(
                    PaginatedPatients(
                        patients = response.data,
                        page = response.page ?: page,
                        limit = response.limit ?: limit,
                        total = response.total ?: response.data.size,
                        totalPages = response.totalPages ?: 1
                    )
                ))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load assigned patients")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network connection error")))
            }
        }
    }
}
