package com.example.SIMATS.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.ui.splash.SplashScreen
import com.example.SIMATS.ui.splash.SplashViewModel

@Composable
fun AppNavHost() {
    val splashViewModel: SplashViewModel = viewModel()
    SplashScreen(viewModel = splashViewModel)
}
