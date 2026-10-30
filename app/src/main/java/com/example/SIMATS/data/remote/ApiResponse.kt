package com.example.SIMATS.data.remote

data class BaseResponse(
    val success: Boolean,
    val message: String? = null
)

data class DataResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T
)

data class LoginResponse(
    val success: Boolean,
    val message: String? = null,
    val token: String? = null,
    val expires_at: String? = null,
    val user: LoginUserData? = null
)

data class LoginUserData(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val patient_code: String? = null,
    val doctor_name: String? = null,
    val specialty: String? = null,
    val clinic: String? = null
)

data class SignUpResponse(
    val success: Boolean,
    val message: String? = null,
    val token: String? = null,
    val expires_at: String? = null,
    val user: SignUpUserData? = null
)

data class SignUpUserData(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val patient_code: String? = null
)

