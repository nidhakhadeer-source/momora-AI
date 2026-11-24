package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.LocalNotificationHelper
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.NotificationItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

object NotificationStateManager {
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    fun setUnreadCount(count: Int) {
        _unreadCount.value = count
    }

    fun markAllRead() {
        _unreadCount.value = 0
    }
}

interface NotificationRepository {
    fun getNotifications(): Flow<Result<List<NotificationItem>>>
    fun markAllAsRead(): Flow<Result<Boolean>>
    fun markAsRead(notificationId: Int): Flow<Result<Boolean>>
}

class NotificationRepositoryImpl : NotificationRepository {
    private val apiService = RetrofitClient.apiService

    override fun getNotifications(): Flow<Result<List<NotificationItem>>> = flow {
        try {
            val response = apiService.getNotifications(SessionManager.getUserId())
            if (response.success && response.data != null) {
                val list = response.data
                val unread = list.count { !it.isRead }
                NotificationStateManager.setUnreadCount(unread)
                LocalNotificationHelper.postAdminAlertIfNeeded(list)
                emit(Result.success(list))
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
            val userId = SessionManager.getUserId()
            val response = apiService.markAllNotificationsRead(userId)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to mark notifications read")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error marking notifications read")))
        }
    }

    override fun markAsRead(notificationId: Int): Flow<Result<Boolean>> = flow {
        try {
            val userId = SessionManager.getUserId()
            val response = apiService.markNotificationRead(userId, notificationId)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to mark notification read")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error marking notification read")))
        }
    }
}
