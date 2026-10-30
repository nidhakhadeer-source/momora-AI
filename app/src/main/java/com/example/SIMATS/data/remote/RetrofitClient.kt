package com.example.SIMATS.data.remote

import com.example.SIMATS.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    /**
     * Centralized Base URL Configuration:
     * - Android Emulator: "http://10.0.2.2/momora-AI/"
     * - Physical Device: "http://<YOUR_COMPUTER_LOCAL_IP>/momora-AI/" (e.g. "http://192.168.1.5/momora-AI/")
     */
    const val EMULATOR_BASE_URL = "https://rental-starry-charm.ngrok-free.dev/momora-AI/"
    
    // Dynamic Base URL holder (can be updated at runtime for physical device LAN testing)
    private var currentBaseUrl = EMULATOR_BASE_URL

    fun updateBaseUrl(newUrl: String) {
        val sanitized = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        currentBaseUrl = sanitized
        retrofitInstance = createRetrofit(currentBaseUrl)
    }

    fun getBaseUrl(): String = currentBaseUrl

    // Auth & Logging Interceptor
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()

        // Attach bearer token if present
        SessionManager.getToken()?.let { token ->
            builder.addHeader("Authorization", "Bearer $token")
        }

        builder.addHeader("Accept", "application/json")
        chain.proceed(builder.build())
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private var retrofitInstance: Retrofit = createRetrofit(currentBaseUrl)

    val retrofit: Retrofit
        get() = retrofitInstance

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
