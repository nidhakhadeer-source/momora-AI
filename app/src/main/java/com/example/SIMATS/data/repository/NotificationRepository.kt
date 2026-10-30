package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.NotificationItem
import com.example.SIMATS.data.model.NotificationType
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface NotificationRepository {
    fun getNotifications(): Flow<List<NotificationItem>>
    fun markAllAsRead(): Flow<Result<Boolean>>
}

class NotificationRepositoryImpl : NotificationRepository {
    private val apiService = RetrofitClient.apiService

    override fun getNotifications(): Flow<List<NotificationItem>> = flow {
        try {
            val response = apiService.getNotifications(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            listOf(
                NotificationItem(
                    id = "1",
                    title = "Your appointment is confirmed",
                    subtitle = "${SessionManager.getDoctorName()} · 29 Oct 2026, 10:30",
                    detailText = "09:10 · Willow Maternity Clinic · In person",
                    isNew = true,
                    isRead = false,
                    type = NotificationType.APPOINTMENT
                ),
                NotificationItem(
                    id = "2",
                    title = "Tonight's vitamin D reminder",
                    subtitle = "400 IU · 1 tablet at 20:00",
                    detailText = "08:30 · Reminder scheduled",
                    isNew = true,
                    isRead = false,
                    type = NotificationType.REMINDER
                ),
                NotificationItem(
                    id = "3",
                    title = "Your report was reviewed",
                    subtitle = "Dr Chen reviewed your blood count.",
                    detailText = "08:25 · View care-team notes in Medical Reports",
                    isNew = true,
                    isRead = false,
                    type = NotificationType.REPORT_REVIEW
                ),
                NotificationItem(
                    id = "4",
                    title = "A new week to discover",
                    subtitle = "Yesterday · Explore today's week 24 development.",
                    detailText = "",
                    isNew = false,
                    isRead = true,
                    type = NotificationType.WEEKLY_DISCOVERY
                )
            )
        )
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
