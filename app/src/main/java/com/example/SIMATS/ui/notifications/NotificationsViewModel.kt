package com.example.SIMATS.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.NotificationRepository
import com.example.SIMATS.data.repository.NotificationRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val repository: NotificationRepository = NotificationRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun onEvent(event: NotificationsUiEvent) {
        when (event) {
            is NotificationsUiEvent.OnFilterTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedFilterTab = event.index)
            }
            NotificationsUiEvent.OnMarkAllReadClicked -> {
                markAllRead()
            }
            is NotificationsUiEvent.OnNotificationItemClicked -> {
                markNotificationAsReadById(event.id)
            }
            is NotificationsUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            NotificationsUiEvent.OnPrivacySettingsClicked -> {}
            NotificationsUiEvent.OnRetryClicked -> {
                loadNotifications()
            }
            NotificationsUiEvent.OnBackClicked -> {}
        }
    }

    fun markNotificationAsReadById(id: String) {
        val currentList = _uiState.value.notifications
        val target = currentList.firstOrNull { it.id == id } ?: return
        if (!target.isRead || target.isNew) {
            val updated = currentList.map {
                if (it.id == id) it.copy(isRead = true, isNew = false) else it
            }
            val unread = updated.count { !it.isRead || it.isNew }
            _uiState.value = _uiState.value.copy(
                notifications = updated,
                unreadCount = unread
            )
            val notifIdInt = id.toIntOrNull()
            if (notifIdInt != null) {
                viewModelScope.launch {
                    repository.markAsRead(notifIdInt).collect { /* background sync */ }
                }
            }
        }
    }

    private fun loadNotifications() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getNotifications().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        val unread = list.count { !it.isRead || it.isNew }
                        _uiState.value = _uiState.value.copy(
                            notifications = list,
                            unreadCount = unread,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load notifications"
                        )
                    }
                )
            }
        }
    }

    private fun markAllRead() {
        if (_uiState.value.isMarkingAllRead || _uiState.value.unreadCount == 0) return

        val previousNotifications = _uiState.value.notifications
        val previousUnreadCount = _uiState.value.unreadCount

        val updated = previousNotifications.map {
            it.copy(isRead = true, isNew = false)
        }
        _uiState.value = _uiState.value.copy(
            isMarkingAllRead = true,
            notifications = updated,
            unreadCount = 0
        )

        viewModelScope.launch {
            repository.markAllAsRead().collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isMarkingAllRead = false)
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isMarkingAllRead = false,
                            notifications = previousNotifications,
                            unreadCount = previousUnreadCount,
                            errorMessage = err.message ?: "Failed to mark all as read"
                        )
                    }
                )
            }
        }
    }

    class Factory(private val repository: NotificationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NotificationsViewModel(repository) as T
        }
    }
}
