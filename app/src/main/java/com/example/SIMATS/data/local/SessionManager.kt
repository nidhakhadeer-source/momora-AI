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
    private const val KEY_PHONE = "key_user_phone"
    private const val KEY_GENDER = "key_user_gender"
    private const val KEY_DOB = "key_user_dob"
    private const val KEY_BLOOD_GROUP = "key_user_blood_group"
    private const val KEY_ADDRESS = "key_user_address"
    private const val KEY_PHOTO_URL = "key_user_photo_url"

    private var sharedPreferences: SharedPreferences? = null

    // In-memory fallback if preferences not yet initialized
    private var memToken: String? = null
    private var memUserId: Int = 5 // Defaults to Maya Patel if initial test
    private var memName: String = "Maya Patel"
    private var memEmail: String = "maya.patel@example.com"
    private var memRole: UserRole = UserRole.PATIENT
    private var memPatientCode: String = "P-13042"
    private var memDoctorName: String = "Dr Amelia Chen"
    private var memPhone: String? = null
    private var memGender: String = "Female"
    private var memDob: String = "14 May 1997"
    private var memBloodGroup: String = "O positive"
    private var memAddress: String? = null
    private var memPhotoUrl: String? = null

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

    fun getPhone(): String? {
        return sharedPreferences?.getString(KEY_PHONE, memPhone) ?: memPhone
    }

    fun getGender(): String {
        return sharedPreferences?.getString(KEY_GENDER, memGender) ?: memGender
    }

    fun getDateOfBirth(): String {
        return sharedPreferences?.getString(KEY_DOB, memDob) ?: memDob
    }

    fun getBloodGroup(): String {
        return sharedPreferences?.getString(KEY_BLOOD_GROUP, memBloodGroup) ?: memBloodGroup
    }

    fun getAddress(): String? {
        return sharedPreferences?.getString(KEY_ADDRESS, memAddress) ?: memAddress
    }

    fun getPhotoUrl(): String? {
        return sharedPreferences?.getString(KEY_PHOTO_URL, memPhotoUrl) ?: memPhotoUrl
    }

    fun setPhotoUrl(url: String?) {
        memPhotoUrl = url
        sharedPreferences?.edit()?.apply {
            putString(KEY_PHOTO_URL, url)
            apply()
        }
    }

    fun saveUserProfileDetails(
        phone: String? = null,
        gender: String? = null,
        address: String? = null,
        photoUrl: String? = null,
        dob: String? = null,
        bloodGroup: String? = null
    ) {
        phone?.let { memPhone = it }
        gender?.let { memGender = it }
        address?.let { memAddress = it }
        photoUrl?.let { memPhotoUrl = it }
        dob?.let { memDob = it }
        bloodGroup?.let { memBloodGroup = it }

        sharedPreferences?.edit()?.apply {
            phone?.let { putString(KEY_PHONE, it) }
            gender?.let { putString(KEY_GENDER, it) }
            address?.let { putString(KEY_ADDRESS, it) }
            photoUrl?.let { putString(KEY_PHOTO_URL, it) }
            dob?.let { putString(KEY_DOB, it) }
            bloodGroup?.let { putString(KEY_BLOOD_GROUP, it) }
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null || sharedPreferences?.contains(KEY_USER_ID) == true
    }

    fun clearSession() {
        memToken = null
        memUserId = 0
        memName = ""
        memEmail = ""
        memPhone = null
        memGender = "Female"
        memDob = "14 May 1997"
        memBloodGroup = "O positive"
        memAddress = null
        memPhotoUrl = null
        memRole = UserRole.PATIENT
        sharedPreferences?.edit()?.clear()?.apply()
    }
}
