package com.example.SIMATS.data.model

data class NotificationItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val detailText: String,
    val isNew: Boolean,
    val isRead: Boolean,
    val type: NotificationType
)

enum class NotificationType {
    APPOINTMENT,
    REMINDER,
    REPORT_REVIEW,
    WEEKLY_DISCOVERY
}
