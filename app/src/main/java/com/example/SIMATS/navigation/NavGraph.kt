package com.example.SIMATS.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.SIMATS.ui.splash.SplashScreen
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraTealPrimary

@Composable
fun AppNavHost() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    when (currentScreen) {
        Screen.Splash -> {
            SplashScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.Home.route -> currentScreen = Screen.Home
                        Screen.Login.route -> currentScreen = Screen.Login
                    }
                }
            )
        }
        Screen.Home -> {
            PlaceholderScreen(title = "Home Screen")
        }
        Screen.Login -> {
            PlaceholderScreen(title = "Account Login Screen")
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MomoraScreenBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MomoraTealPrimary
        )
    }
}
