package com.example.SIMATS.ui.screens.splash

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.SIMATS.ui.splash.SplashScreen
import com.example.SIMATS.ui.splash.SplashScreenContent
import com.example.SIMATS.ui.splash.SplashViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    modifier: Modifier = Modifier
) {
    SplashScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}

@Composable
fun SplashScreenContent(
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit = {},
    onHelpClick: () -> Unit = {}
) {
    SplashScreenContent(
        modifier = modifier,
        onFavoriteClick = onFavoriteClick,
        onHelpClick = onHelpClick
    )
}
