package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.MedicalReportItem
import com.example.SIMATS.data.model.ReportCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface MedicalReportsRepository {
    fun getReports(): Flow<List<MedicalReportItem>>
    fun uploadReport(fileName: String): Flow<Result<Boolean>>
}

class MedicalReportsRepositoryImpl : MedicalReportsRepository {
    override fun getReports(): Flow<List<MedicalReportItem>> = flow {
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
        delay(1000)
        emit(Result.success(true))
    }
}
