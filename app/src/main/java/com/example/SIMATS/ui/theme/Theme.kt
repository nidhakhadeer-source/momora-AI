package com.example.SIMATS.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MomoraTealPrimary,
    onPrimary = Color.White,
    primaryContainer = MomoraMintLight,
    onPrimaryContainer = MomoraTealPrimary,
    background = MomoraScreenBackground,
    onBackground = MomoraTextHeadline,
    surface = MomoraCardBackground,
    onSurface = MomoraTextHeadline,
    secondary = MomoraSubheaderPurple,
    onSecondary = Color.White
)

private val DarkColorScheme = lightColorScheme(
    primary = MomoraTealPrimary,
    onPrimary = Color.White,
    primaryContainer = MomoraMintLight,
    onPrimaryContainer = MomoraTealPrimary,
    background = MomoraScreenBackground,
    onBackground = MomoraTextHeadline,
    surface = MomoraCardBackground,
    onSurface = MomoraTextHeadline,
    secondary = MomoraSubheaderPurple,
    onSecondary = Color.White
)

@Composable
fun MomoraAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
