package com.example.SIMATS.ui.notifications

import com.example.SIMATS.data.model.NotificationItem

data class NotificationsUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val selectedFilterTab: Int = 0, // 0 = All, 1 = Unread
    val unreadCount: Int = 3,
    val selectedNavIndex: Int = 3, // Care tab active
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
