package com.example.SIMATS.ui.screens.splash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SplashViewModel : ViewModel() {
    
    // In a real app, you would use this to check authentication state, 
    // load initial data, and then navigate to the next screen.
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onSplashComplete() {
        _isLoading.value = false
    }
}
