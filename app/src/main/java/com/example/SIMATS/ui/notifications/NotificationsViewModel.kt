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
            is NotificationsUiEvent.OnNotificationItemClicked -> {}
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

    private fun loadNotifications() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getNotifications().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        val unread = list.count { !it.isRead }
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
        viewModelScope.launch {
            repository.markAllAsRead().collect { result ->
                result.onSuccess {
                    val updated = _uiState.value.notifications.map {
                        it.copy(isRead = true, isNew = false)
                    }
                    _uiState.value = _uiState.value.copy(notifications = updated, unreadCount = 0)
                }
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
