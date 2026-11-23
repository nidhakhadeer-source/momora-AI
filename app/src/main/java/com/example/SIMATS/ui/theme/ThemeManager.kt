package com.example.SIMATS.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.SIMATS.data.local.SessionManager
import kotlinx.coroutines.flow.StateFlow

object ThemeManager {
    val themeFlow: StateFlow<String> get() = SessionManager.themeFlow

    fun init(context: Context) {
        // SessionManager.init already handles reading saved choice at app start
    }

    fun setTheme(theme: String) {
        SessionManager.setAppTheme(theme)
    }

    fun getTheme(): String = SessionManager.getAppTheme()

    @Composable
    fun isDarkTheme(): Boolean {
        val theme by SessionManager.themeFlow.collectAsState()
        val isSystemDark = isSystemInDarkTheme()
        return when (theme.uppercase()) {
            "DARK" -> true
            "LIGHT" -> false
            else -> isSystemDark
        }
    }
}
