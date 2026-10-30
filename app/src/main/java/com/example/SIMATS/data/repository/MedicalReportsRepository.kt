package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.MedicalReportItem
import com.example.SIMATS.data.model.ReportCategory
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface MedicalReportsRepository {
    fun getReports(): Flow<List<MedicalReportItem>>
    fun uploadReport(fileName: String): Flow<Result<Boolean>>
}

class MedicalReportsRepositoryImpl : MedicalReportsRepository {
    private val apiService = RetrofitClient.apiService

    override fun getReports(): Flow<List<MedicalReportItem>> = flow {
        try {
            val response = apiService.getReports(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            listOf(
                MedicalReportItem(
                    id = "1",
                    title = "Anatomy ultrasound",
                    dateAndSizeText = "02 Oct 2026 · PDF · 2.4 MB",
                    category = ReportCategory.ULTRASOUND,
                    fileType = "PDF"
                ),
                MedicalReportItem(
                    id = "2",
                    title = "Complete blood count",
                    dateAndSizeText = "28 Oct 2026 · PDF · 640 KB",
                    category = ReportCategory.LAB_RESULTS,
                    fileType = "PDF"
                ),
                MedicalReportItem(
                    id = "3",
                    title = "Antenatal visit summary",
                    dateAndSizeText = "04 Oct 2026 · PDF · 420 KB",
                    category = ReportCategory.ALL,
                    fileType = "PDF"
                )
            )
        )
    }

    override fun uploadReport(fileName: String): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.uploadReport(fileName)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to upload report")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error uploading report")))
        }
    }
}
