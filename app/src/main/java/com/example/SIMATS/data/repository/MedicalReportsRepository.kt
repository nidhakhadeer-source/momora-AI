package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.MedicalReportItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream

interface MedicalReportsRepository {
    fun getReports(): Flow<Result<List<MedicalReportItem>>>
    fun uploadReport(fileName: String): Flow<Result<Boolean>>
    fun uploadMedicalReport(
        filePart: MultipartBody.Part,
        title: RequestBody,
        category: RequestBody,
        description: RequestBody? = null
    ): Flow<Result<Boolean>>
    fun downloadReportFile(reportId: String, destinationFile: File): Flow<Result<File>>
    fun downloadReportFromUrl(fileUrl: String, destinationFile: File): Flow<Result<File>>
}

class MedicalReportsRepositoryImpl : MedicalReportsRepository {
    private val apiService = RetrofitClient.apiService

    override fun getReports(): Flow<Result<List<MedicalReportItem>>> = flow {
        try {
            val response = apiService.getReports(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load medical reports")))
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

    override fun uploadMedicalReport(
        filePart: MultipartBody.Part,
        title: RequestBody,
        category: RequestBody,
        description: RequestBody?
    ): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.uploadMedicalReport(filePart, title, category, description)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to upload medical report")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network error uploading report")))
            }
        }
    }

    override fun downloadReportFile(reportId: String, destinationFile: File): Flow<Result<File>> = flow {
        try {
            val response = apiService.downloadReport(reportId)
            if (!response.isSuccessful || response.body() == null) {
                val errorBodyStr = response.errorBody()?.string() ?: ""
                val msg = try {
                    val jsonObj = JSONObject(errorBodyStr)
                    jsonObj.optString("message", "Failed to download medical report")
                } catch (_: Exception) {
                    "Failed to download medical report (${response.code()})"
                }
                emit(Result.failure(Exception(msg)))
                return@flow
            }

            val body = response.body()!!
            destinationFile.parentFile?.mkdirs()

            body.byteStream().use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            emit(Result.success(destinationFile))
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network error downloading report")))
            }
        }
    }

    override fun downloadReportFromUrl(fileUrl: String, destinationFile: File): Flow<Result<File>> = flow {
        try {
            val response = apiService.downloadReportFromUrl(fileUrl)
            if (!response.isSuccessful || response.body() == null) {
                val errorBodyStr = response.errorBody()?.string() ?: ""
                val msg = try {
                    val jsonObj = JSONObject(errorBodyStr)
                    jsonObj.optString("message", "Failed to download medical report")
                } catch (_: Exception) {
                    "Failed to download medical report (${response.code()})"
                }
                emit(Result.failure(Exception(msg)))
                return@flow
            }

            val body = response.body()!!
            destinationFile.parentFile?.mkdirs()

            body.byteStream().use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            emit(Result.success(destinationFile))
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error downloading report")))
        }
    }
}
