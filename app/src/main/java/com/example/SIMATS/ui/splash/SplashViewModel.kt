package com.example.SIMATS.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.SplashRepository
import com.example.SIMATS.data.repository.SplashRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val repository: SplashRepository = SplashRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        loadSplashData()
    }

    fun onEvent(event: SplashUiEvent) {
        when (event) {
            SplashUiEvent.OnFavoriteClicked -> {
                // Handle favorite action
            }
            SplashUiEvent.OnHelpClicked -> {
                // Handle help action
            }
            SplashUiEvent.OnGetStartedClicked -> {
                // Navigate to next screen
            }
        }
    }

    private fun loadSplashData() {
        viewModelScope.launch {
            repository.checkAuthState().collect { authState ->
                _uiState.value = SplashUiState.Success(authState)
            }
        }
    }

    class Factory(private val repository: SplashRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SplashViewModel(repository) as T
        }
    }
}
