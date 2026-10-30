package com.example.SIMATS.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.SIMATS.data.model.UserRole

object SessionManager {
    private const val PREF_NAME = "momora_session_pref"
    private const val KEY_TOKEN = "key_auth_token"
    private const val KEY_USER_ID = "key_user_id"
    private const val KEY_NAME = "key_user_name"
    private const val KEY_EMAIL = "key_user_email"
    private const val KEY_ROLE = "key_user_role"
    private const val KEY_PATIENT_CODE = "key_patient_code"
    private const val KEY_DOCTOR_NAME = "key_doctor_name"

    private var sharedPreferences: SharedPreferences? = null

    // In-memory fallback if preferences not yet initialized
    private var memToken: String? = null
    private var memUserId: Int = 5 // Defaults to Maya Patel if initial test
    private var memName: String = "Maya Patel"
    private var memEmail: String = "maya.patel@example.com"
    private var memRole: UserRole = UserRole.PATIENT
    private var memPatientCode: String = "P-13042"
    private var memDoctorName: String = "Dr Amelia Chen"

    fun init(context: Context) {
        sharedPreferences = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession(
        token: String?,
        userId: Int,
        name: String,
        email: String,
        role: String,
        patientCode: String? = null,
        doctorName: String? = null
    ) {
        memToken = token
        memUserId = userId
        memName = name
        memEmail = email
        memRole = when (role.lowercase()) {
            "doctor" -> UserRole.DOCTOR
            "admin" -> UserRole.ADMIN
            else -> UserRole.PATIENT
        }
        memPatientCode = patientCode ?: "P-$userId"
        memDoctorName = doctorName ?: "Dr Amelia Chen"

        sharedPreferences?.edit()?.apply {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, userId)
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_ROLE, role)
            putString(KEY_PATIENT_CODE, memPatientCode)
            putString(KEY_DOCTOR_NAME, memDoctorName)
            apply()
        }
    }

    fun getToken(): String? {
        return sharedPreferences?.getString(KEY_TOKEN, memToken) ?: memToken
    }

    fun getUserId(): Int {
        return sharedPreferences?.getInt(KEY_USER_ID, memUserId) ?: memUserId
    }

    fun getUserName(): String {
        return sharedPreferences?.getString(KEY_NAME, memName) ?: memName
    }

    fun getUserEmail(): String {
        return sharedPreferences?.getString(KEY_EMAIL, memEmail) ?: memEmail
    }

    fun getUserRole(): UserRole {
        val roleStr = sharedPreferences?.getString(KEY_ROLE, null)
        return if (roleStr != null) {
            when (roleStr.lowercase()) {
                "doctor" -> UserRole.DOCTOR
                "admin" -> UserRole.ADMIN
                else -> UserRole.PATIENT
            }
        } else {
            memRole
        }
    }

    fun getPatientCode(): String {
        return sharedPreferences?.getString(KEY_PATIENT_CODE, memPatientCode) ?: memPatientCode
    }

    fun getDoctorName(): String {
        return sharedPreferences?.getString(KEY_DOCTOR_NAME, memDoctorName) ?: memDoctorName
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null || sharedPreferences?.contains(KEY_USER_ID) == true
    }

    fun clearSession() {
        memToken = null
        memUserId = 0
        memName = ""
        memEmail = ""
        memRole = UserRole.PATIENT
        sharedPreferences?.edit()?.clear()?.apply()
    }
}

