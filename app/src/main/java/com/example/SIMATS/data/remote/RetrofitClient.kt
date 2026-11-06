package com.example.SIMATS.data.remote

import com.example.SIMATS.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Single unified Base URL for all network calls
    const val BASE_URL = "https://rental-starry-charm.ngrok-free.dev/momora-AI/"
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()

        // Attach bearer token if present
        val token = SessionManager.getToken()
        if (!token.isNullOrBlank()) {
            builder.addHeader("Authorization", "Bearer $token")
        }

        builder.addHeader("Accept", "application/json")
        // Bypass ngrok free tier browser interstitial warning
        builder.addHeader("ngrok-skip-browser-warning", "true")

        val newRequest = builder.build()
        val response = chain.proceed(newRequest)

        val path = original.url().encodedPath()
        val isAuthEndpoint = path.contains("auth/login.php") || path.contains("auth/signup.php")
        val isExplicitlyAuthenticated = newRequest.header("Authorization") != null

        if (response.code() == 401 && !isAuthEndpoint && isExplicitlyAuthenticated) {
            SessionManager.notifySessionExpired()
        }
        response
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
