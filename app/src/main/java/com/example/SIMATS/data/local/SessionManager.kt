package com.example.SIMATS.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.SIMATS.data.model.UserRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SessionManager {
    private const val PREF_NAME = "momora_session_pref"
    private const val KEY_TOKEN = "key_auth_token"
    private const val KEY_USER_ID = "key_user_id"
    private const val KEY_NAME = "key_user_name"
    private const val KEY_EMAIL = "key_user_email"
    private const val KEY_ROLE = "key_user_role"
    private const val KEY_PATIENT_CODE = "key_patient_code"
    private const val KEY_DOCTOR_NAME = "key_doctor_name"
    private const val KEY_EXPIRES_AT = "key_auth_expires_at"
    private const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
    private const val KEY_PHONE = "key_user_phone"
    private const val KEY_GENDER = "key_user_gender"
    private const val KEY_DOB = "key_user_dob"
    private const val KEY_BLOOD_GROUP = "key_user_blood_group"
    private const val KEY_ADDRESS = "key_user_address"
    private const val KEY_PHOTO_URL = "key_user_photo_url"

    @Volatile
    private var sharedPreferences: SharedPreferences? = null

    // In-memory fallback if preferences not yet initialized
    private var memToken: String? = null
    private var memUserId: Int = 0
    private var memName: String = ""
    private var memEmail: String = ""
    private var memRole: UserRole = UserRole.PATIENT
    private var memPatientCode: String = ""
    private var memDoctorName: String = ""
    private var memExpiresAt: String? = null
    private var memPhone: String? = null
    private var memGender: String = "Female"
    private var memDob: String = "14 May 1997"
    private var memBloodGroup: String = "O positive"
    private var memAddress: String? = null
    private var memPhotoUrl: String? = null

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()

    fun notifySessionExpired() {
        clearSession()
        _sessionExpiredEvent.tryEmit(Unit)
    }

    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveSession(
        token: String?,
        userId: Int,
        name: String,
        email: String,
        role: String,
        patientCode: String? = null,
        doctorName: String? = null,
        expiresAt: String? = null
    ) {
        memToken = token
        memUserId = userId
        memName = name
        memEmail = email
        memExpiresAt = expiresAt
        memRole = when (role.lowercase()) {
            "doctor" -> UserRole.DOCTOR
            "admin" -> UserRole.ADMIN
            else -> UserRole.PATIENT
        }
        memPatientCode = patientCode ?: "P-$userId"
        memDoctorName = doctorName ?: "Dr Amelia Chen"

        sharedPreferences?.let { prefs ->
            prefs.edit()
                .putString(KEY_TOKEN, token)
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putString(KEY_ROLE, role)
                .putString(KEY_PATIENT_CODE, memPatientCode)
                .putString(KEY_DOCTOR_NAME, memDoctorName)
                .putString(KEY_EXPIRES_AT, expiresAt)
                .putBoolean(KEY_ONBOARDING_COMPLETED, true)
                .commit()
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

    fun getExpiresAt(): String? {
        return sharedPreferences?.getString(KEY_EXPIRES_AT, memExpiresAt) ?: memExpiresAt
    }

    fun isTokenExpired(): Boolean {
        val expiresAtStr = getExpiresAt()?.trim()
        if (expiresAtStr.isNullOrEmpty()) {
            return false
        }
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val expiryDate = sdf.parse(expiresAtStr)
            expiryDate != null && expiryDate.before(Date())
        } catch (_: Exception) {
            false
        }
    }

    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences?.getBoolean(KEY_ONBOARDING_COMPLETED, false) ?: false
    }

    fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences?.let { prefs ->
            prefs.edit()
                .putBoolean(KEY_ONBOARDING_COMPLETED, completed)
                .commit()
        }
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
        sharedPreferences?.let { prefs ->
            prefs.edit()
                .putString(KEY_PHOTO_URL, url)
                .commit()
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

        sharedPreferences?.let { prefs ->
            val editor = prefs.edit()
            phone?.let { editor.putString(KEY_PHONE, it) }
            gender?.let { editor.putString(KEY_GENDER, it) }
            address?.let { editor.putString(KEY_ADDRESS, it) }
            photoUrl?.let { editor.putString(KEY_PHOTO_URL, it) }
            dob?.let { editor.putString(KEY_DOB, it) }
            bloodGroup?.let { editor.putString(KEY_BLOOD_GROUP, it) }
            editor.commit()
        }
    }

    fun isLoggedIn(): Boolean {
        val token = getToken()
        val userId = getUserId()
        if (token.isNullOrBlank() || userId <= 0) {
            return false
        }
        if (isTokenExpired()) {
            clearSession()
            return false
        }
        return true
    }

    fun clearSession() {
        memToken = null
        memUserId = 0
        memName = ""
        memEmail = ""
        memExpiresAt = null
        memPhone = null
        memGender = "Female"
        memDob = "14 May 1997"
        memBloodGroup = "O positive"
        memAddress = null
        memPhotoUrl = null
        memRole = UserRole.PATIENT
        sharedPreferences?.let { prefs ->
            prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_USER_ID)
                .remove(KEY_NAME)
                .remove(KEY_EMAIL)
                .remove(KEY_ROLE)
                .remove(KEY_PATIENT_CODE)
                .remove(KEY_DOCTOR_NAME)
                .remove(KEY_EXPIRES_AT)
                .remove(KEY_PHONE)
                .remove(KEY_GENDER)
                .remove(KEY_DOB)
                .remove(KEY_BLOOD_GROUP)
                .remove(KEY_ADDRESS)
                .remove(KEY_PHOTO_URL)
                .commit()
        }
    }
}
