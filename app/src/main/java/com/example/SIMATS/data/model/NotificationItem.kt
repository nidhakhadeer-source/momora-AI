package com.example.SIMATS.data.model

import com.google.gson.annotations.SerializedName

data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    @SerializedName(value = "detailText", alternate = ["detail_text"])
    val detailText: String = "",
    @SerializedName(value = "isNew", alternate = ["is_new"])
    val isNew: Boolean = false,
    @SerializedName(value = "isRead", alternate = ["is_read"])
    val isRead: Boolean = false,
    val type: NotificationType = NotificationType.REMINDER,
    @SerializedName("created_at")
    val createdAt: String? = null
)

enum class NotificationType {
    APPOINTMENT,
    REMINDER,
    REPORT_REVIEW,
    WEEKLY_DISCOVERY
}
