package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorProfileData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface DoctorProfileRepository {
    fun getDoctorProfile(): Flow<Result<DoctorProfileData>>
    fun updateDoctorProfile(profile: DoctorProfileData): Flow<Result<DoctorProfileData>>
}

class DoctorProfileRepositoryImpl : DoctorProfileRepository {
    private val apiService = RetrofitClient.apiService

    override fun getDoctorProfile(): Flow<Result<DoctorProfileData>> = flow {
        val userId = SessionManager.getUserId()
        try {
            val response = apiService.getDoctorProfile(userId.takeIf { it > 0 })
            if (response.success && response.data != null) {
                val data = response.data
                val resolvedPhoto = resolveFullPhotoUrl(data.photoUrl)
                val merged = data.copy(photoUrl = resolvedPhoto)
                emit(Result.success(merged))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load doctor profile")))
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

    override fun updateDoctorProfile(profile: DoctorProfileData): Flow<Result<DoctorProfileData>> = flow {
        val userId = SessionManager.getUserId()
        try {
            val fields = mutableMapOf<String, String>()
            if (profile.fullName.isNotBlank()) fields["fullName"] = profile.fullName
            if (profile.specialty.isNotBlank()) fields["specialty"] = profile.specialty
            if (profile.licenseId.isNotBlank()) fields["licenseId"] = profile.licenseId
            if (profile.clinic.isNotBlank()) fields["clinic"] = profile.clinic
            profile.gender?.let { if (it.isNotBlank()) fields["gender"] = it }
            profile.phone?.let { if (it.isNotBlank()) fields["phone"] = it }
            profile.address?.let { if (it.isNotBlank()) fields["address"] = it }
            profile.qualification?.let { if (it.isNotBlank()) fields["qualification"] = it }
            profile.experienceYears?.let { fields["experienceYears"] = it.toString() }
            profile.photoUrl?.let { if (it.isNotBlank()) fields["photoUrl"] = it }

            val response = apiService.updateDoctorProfile(userId.takeIf { it > 0 }, fields)
            if (response.success && response.data != null) {
                val saved = response.data
                val resolvedPhoto = resolveFullPhotoUrl(saved.photoUrl)
                emit(Result.success(saved.copy(photoUrl = resolvedPhoto)))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to update profile")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Failed to update doctor profile")))
            }
        }
    }

    private fun resolveFullPhotoUrl(rawUrl: String?): String? {
        if (rawUrl.isNullOrBlank()) return null
        if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
            if (rawUrl.contains("localhost") && !RetrofitClient.BASE_URL.contains("localhost")) {
                val relativePath = rawUrl.substringAfter("/momora-AI/").removePrefix("/")
                return RetrofitClient.BASE_URL.trimEnd('/') + "/" + relativePath
            }
            return rawUrl
        }
        return RetrofitClient.BASE_URL.trimEnd('/') + "/" + rawUrl.removePrefix("/")
    }
}
