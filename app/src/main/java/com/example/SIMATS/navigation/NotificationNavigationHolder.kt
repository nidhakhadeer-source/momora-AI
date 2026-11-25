package com.example.SIMATS.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationNavigationHolder {
    private val _pendingRoute = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val pendingRoute: SharedFlow<String> = _pendingRoute.asSharedFlow()

    fun postTargetScreen(screen: String?) {
        if (!screen.isNullOrBlank()) {
            _pendingRoute.tryEmit(screen)
        }
    }
}
