package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.NotificationItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface NotificationRepository {
    fun getNotifications(): Flow<Result<List<NotificationItem>>>
    fun markAllAsRead(): Flow<Result<Boolean>>
}

class NotificationRepositoryImpl : NotificationRepository {
    private val apiService = RetrofitClient.apiService

    override fun getNotifications(): Flow<Result<List<NotificationItem>>> = flow {
        try {
            val response = apiService.getNotifications(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load notifications")))
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

    override fun markAllAsRead(): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.markAllNotificationsRead()
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to mark notifications read")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error marking notifications read")))
        }
    }
}
