package com.example.SIMATS.ui.admindashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.repository.AdminRepository
import com.example.SIMATS.data.repository.AdminRepositoryImpl
import com.example.SIMATS.data.repository.NotificationRepository
import com.example.SIMATS.data.repository.NotificationRepositoryImpl
import com.example.SIMATS.data.repository.NotificationStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminDashboardViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl(),
    private val notificationRepository: NotificationRepository = NotificationRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AdminDashboardUiState(
            adminName = SessionManager.getUserName(),
            adminPhotoUrl = SessionManager.getPhotoUrl()
        )
    )
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
        loadNotifications()
    }

    fun onEvent(event: AdminDashboardUiEvent) {
        when (event) {
            is AdminDashboardUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            AdminDashboardUiEvent.OnManageDoctorsClicked -> {}
            AdminDashboardUiEvent.OnManagePatientsClicked -> {}
            AdminDashboardUiEvent.OnAddDoctorClicked -> {}
            AdminDashboardUiEvent.OnNotificationClicked -> {
                _uiState.value = _uiState.value.copy(showNotificationsDialog = true)
                markNotificationsRead()
            }
            AdminDashboardUiEvent.OnDismissNotificationsDialog -> {
                _uiState.value = _uiState.value.copy(showNotificationsDialog = false)
            }
            AdminDashboardUiEvent.OnRetryClicked -> {
                loadDashboard()
                loadNotifications()
            }
        }
    }

    fun loadDashboard() {
        val currentPhoto = SessionManager.getPhotoUrl()
        val currentName = SessionManager.getUserName()
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            adminPhotoUrl = currentPhoto,
            adminName = currentName
        )
        viewModelScope.launch {
            repository.getAdminDashboardData().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            dashboardData = data,
                            adminPhotoUrl = SessionManager.getPhotoUrl(),
                            adminName = SessionManager.getUserName()
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load admin dashboard"
                        )
                    }
                )
            }
        }
    }

    private fun loadNotifications() {
        _uiState.value = _uiState.value.copy(isLoadingNotifications = true)
        viewModelScope.launch {
            notificationRepository.getNotifications().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        val unread = list.count { !it.isRead }
                        NotificationStateManager.setUnreadCount(unread)
                        _uiState.value = _uiState.value.copy(
                            isLoadingNotifications = false,
                            notificationsList = list,
                            unreadNotificationCount = unread
                        )
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(isLoadingNotifications = false)
                    }
                )
            }
        }
    }

    private fun markNotificationsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead().collect { result ->
                if (result.isSuccess) {
                    NotificationStateManager.markAllRead()
                    _uiState.value = _uiState.value.copy(
                        unreadNotificationCount = 0,
                        notificationsList = _uiState.value.notificationsList.map { it.copy(isRead = true, isNew = false) }
                    )
                }
            }
        }
    }

    class Factory(
        private val repository: AdminRepository,
        private val notificationRepository: NotificationRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminDashboardViewModel(repository, notificationRepository) as T
        }
    }
}
