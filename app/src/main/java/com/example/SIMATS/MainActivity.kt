package com.example.SIMATS

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.service.MomoraFirebaseMessagingService
import com.example.SIMATS.navigation.AppNavHost
import com.example.SIMATS.navigation.NotificationNavigationHolder
import com.example.SIMATS.ui.theme.MomoraAITheme
import com.example.SIMATS.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // Notification permission result handled
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(this)
        ThemeManager.init(this)
        checkNotificationPermission()
        handleNotificationIntent(intent)
        enableEdgeToEdge()
        setContent {
            val isDark = ThemeManager.isDarkTheme()
            MomoraAITheme(darkTheme = isDark) {
                AppNavHost()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val targetScreen = intent?.getStringExtra(MomoraFirebaseMessagingService.EXTRA_TARGET_SCREEN)
        if (!targetScreen.isNullOrBlank()) {
            NotificationNavigationHolder.postTargetScreen(targetScreen)
        }
    }
}
