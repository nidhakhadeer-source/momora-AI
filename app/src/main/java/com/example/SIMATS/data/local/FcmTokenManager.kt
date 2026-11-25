package com.example.SIMATS.data.local

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import com.example.SIMATS.data.remote.RetrofitClient
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FcmTokenManager {
    private const val TAG = "MomoraFCM"
    private const val PREF_FCM = "momora_fcm_pref"
    private const val KEY_FCM_TOKEN = "key_fcm_token"

    @Volatile
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREF_FCM, Context.MODE_PRIVATE)
        }
        // If user already authenticated on app launch, ensure token is registered with backend
        if (SessionManager.isLoggedIn()) {
            syncTokenWithBackend()
        }
    }

    fun saveToken(token: String) {
        prefs?.edit()?.putString(KEY_FCM_TOKEN, token)?.apply()
    }

    fun getToken(): String? {
        return prefs?.getString(KEY_FCM_TOKEN, null)
    }

    fun syncTokenWithBackend() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result ?: return@addOnCompleteListener
                Log.d(TAG, "FCM Device Token: $token")
                saveToken(token)

                // If user is currently authenticated, register this token with the backend
                if (SessionManager.isLoggedIn()) {
                    registerTokenWithBackend(token)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseMessaging not initialized or unavailable", e)
        }
    }

    fun registerTokenWithBackend(token: String) {
        val authToken = SessionManager.getToken()
        if (authToken.isNullOrBlank()) return

        val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}".trim()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.registerDeviceToken(
                    fcmToken = token,
                    deviceType = "android",
                    deviceModel = deviceModel
                )
                if (response.success) {
                    Log.d(TAG, "FCM Token registered with backend successfully for user ${SessionManager.getUserId()}")
                } else {
                    Log.w(TAG, "Backend token registration returned error: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register FCM token with backend: ${e.message}")
            }
        }
    }

    fun unregisterToken() {
        val currentToken = getToken() ?: return
        val authToken = SessionManager.getToken()

        CoroutineScope(Dispatchers.IO).launch {
            if (!authToken.isNullOrBlank()) {
                try {
                    val response = RetrofitClient.apiService.unregisterDeviceToken(fcmToken = currentToken)
                    Log.d(TAG, "Backend token unregistration response: ${response.message}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to unregister FCM token from backend: ${e.message}")
                }
            }

            try {
                FirebaseMessaging.getInstance().deleteToken()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete FCM instance token locally: ${e.message}")
            }

            prefs?.edit()?.remove(KEY_FCM_TOKEN)?.apply()
        }
    }
}
