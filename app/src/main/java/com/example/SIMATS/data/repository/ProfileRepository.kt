package com.example.SIMATS.data.repository

import android.content.Context
import android.net.Uri
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.PatientProfileData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

interface ProfileRepository {
    fun getPatientProfile(): Flow<Result<PatientProfileData>>
    fun savePatientProfile(profile: PatientProfileData): Flow<Result<PatientProfileData>>
    fun uploadProfilePhoto(context: Context, uri: Uri): Flow<Result<String>>
}

class ProfileRepositoryImpl : ProfileRepository {
    private val apiService = RetrofitClient.apiService

    override fun getPatientProfile(): Flow<Result<PatientProfileData>> = flow {
        val userId = SessionManager.getUserId()
        try {
            val response = apiService.getProfile(userId)
            if (response.success && response.data != null) {
                val data = response.data

                // Resolve full photo URL
                val rawPhoto = if (!data.photoUrl.isNullOrBlank()) data.photoUrl else data.profilePhoto
                val resolvedPhoto = resolveFullPhotoUrl(rawPhoto) ?: SessionManager.getPhotoUrl()

                // Fall back to local session values if backend data is empty
                val resolvedPhone = if (!data.phone.isNullOrBlank()) data.phone else SessionManager.getPhone()
                val resolvedGender = if (!data.gender.isNullOrBlank()) data.gender else SessionManager.getGender()
                val resolvedAddress = if (!data.address.isNullOrBlank()) data.address else SessionManager.getAddress()
                val resolvedDob = if (data.dateOfBirth.isNotBlank()) data.dateOfBirth else SessionManager.getDateOfBirth()
                val resolvedBloodGroup = if (data.bloodGroup.isNotBlank()) data.bloodGroup else SessionManager.getBloodGroup()

                val merged = data.copy(
                    avatarInitials = if (data.avatarInitials.isNotBlank()) data.avatarInitials else generateInitials(data.fullName.ifBlank { SessionManager.getUserName() }),
                    patientName = data.patientName ?: data.fullName.ifBlank { SessionManager.getUserName() },
                    patientId = data.patientId ?: SessionManager.getPatientCode(),
                    fullName = data.fullName.ifBlank { SessionManager.getUserName() },
                    email = data.email.ifBlank { SessionManager.getUserEmail() },
                    phone = resolvedPhone,
                    dateOfBirth = resolvedDob,
                    gender = resolvedGender,
                    bloodGroup = resolvedBloodGroup,
                    address = resolvedAddress,
                    photoUrl = resolvedPhoto,
                    profilePhoto = resolvedPhoto
                )

                // Sync locally to SessionManager
                SessionManager.saveUserProfileDetails(
                    phone = resolvedPhone,
                    gender = resolvedGender,
                    address = resolvedAddress,
                    photoUrl = resolvedPhoto,
                    dob = resolvedDob,
                    bloodGroup = resolvedBloodGroup
                )

                emit(Result.success(merged))
                return@flow
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load profile")))
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

    override fun savePatientProfile(profile: PatientProfileData): Flow<Result<PatientProfileData>> = flow {
        val userId = SessionManager.getUserId()
        try {
            val fields = mutableMapOf(
                "user_id" to userId.toString(),
                "fullName" to profile.fullName,
                "email" to profile.email,
                "dateOfBirth" to profile.dateOfBirth,
                "bloodGroup" to profile.bloodGroup,
                "isCareTeamSharingOn" to if (profile.isCareTeamSharingOn) "1" else "0"
            )
            profile.phone?.let { if (it.isNotBlank()) fields["phone"] = it }
            profile.gender?.let { if (it.isNotBlank()) fields["gender"] = it }
            profile.address?.let { if (it.isNotBlank()) fields["address"] = it }

            val response = apiService.updateProfile(fields)
            if (response.success) {
                // Update local session
                SessionManager.saveUserProfileDetails(
                    phone = profile.phone,
                    gender = profile.gender,
                    address = profile.address,
                    photoUrl = profile.photoUrl,
                    dob = profile.dateOfBirth,
                    bloodGroup = profile.bloodGroup
                )
                emit(Result.success(profile))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to save profile")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error while saving profile")))
        }
    }

    override fun uploadProfilePhoto(context: Context, uri: Uri): Flow<Result<String>> = flow {
        val userId = SessionManager.getUserId()
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
                ?: throw Exception("Unable to open selected image")
            val bytes = inputStream.use { it.readBytes() }

            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val extension = when {
                mimeType.contains("png", ignoreCase = true) -> "png"
                mimeType.contains("webp", ignoreCase = true) -> "webp"
                else -> "jpg"
            }
            val filename = "profile_photo_${userId}_${System.currentTimeMillis()}.$extension"

            val textMediaType = MediaType.parse("text/plain")
            val imageMediaType = MediaType.parse(mimeType)
            val userIdPart = RequestBody.create(textMediaType, userId.toString())
            val photoRequestBody = RequestBody.create(imageMediaType, bytes)
            val photoPart = MultipartBody.Part.createFormData("photo", filename, photoRequestBody)

            val response = apiService.uploadProfilePhoto(userIdPart, photoPart)
            if (response.success && response.data != null) {
                val rawPhoto = response.data.photo_url ?: response.data.profile_photo ?: ""
                val resolvedUrl = resolveFullPhotoUrl(rawPhoto) ?: rawPhoto
                SessionManager.setPhotoUrl(resolvedUrl)
                emit(Result.success(resolvedUrl))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to upload photo")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Error uploading photo")))
        }
    }

    private fun resolveFullPhotoUrl(rawUrl: String?): String? {
        if (rawUrl.isNullOrBlank()) return null
        if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
            // Remap localhost to RetrofitClient.BASE_URL if client uses remote domain (e.g. ngrok)
            if (rawUrl.contains("localhost") && !RetrofitClient.BASE_URL.contains("localhost")) {
                val relativePath = rawUrl.substringAfter("/momora-AI/").removePrefix("/")
                return RetrofitClient.BASE_URL.trimEnd('/') + "/" + relativePath
            }
            return rawUrl
        }
        return RetrofitClient.BASE_URL.trimEnd('/') + "/" + rawUrl.removePrefix("/")
    }

    private fun generateInitials(name: String): String {
        val words = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val initials = words.take(2).map { it.first().uppercaseChar() }.joinToString("")
        return initials.ifBlank { "PT" }
    }
}
