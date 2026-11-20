package com.example.SIMATS.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MomoraPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1E7F5),
    onPrimaryContainer = MomoraPurpleDark,
    secondary = MomoraPurpleDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFAF0F3),
    onSecondaryContainer = MomoraPurpleText,
    background = Color(0xFFFFFFFF),
    onBackground = MomoraPurpleText,
    surface = Color(0xFFFFFFFF),
    onSurface = MomoraPurpleText,
    surfaceVariant = Color(0xFFF7F4F9),
    onSurfaceVariant = MomoraBodyMuted,
    outline = Color(0xFFEADBEE),
    outlineVariant = Color(0xFFF0EBF2)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD4B3DC),
    onPrimary = Color(0xFF2E1935),
    primaryContainer = Color(0xFF4A3451),
    onPrimaryContainer = Color(0xFFF1E7F5),
    secondary = Color(0xFFC7B6CE),
    onSecondary = Color(0xFF261D2B),
    secondaryContainer = Color(0xFF382F3D),
    onSecondaryContainer = Color(0xFFEDE7EF),
    background = Color(0xFF141216),
    onBackground = Color(0xFFEDE7EF),
    surface = Color(0xFF1E1A22),
    onSurface = Color(0xFFEDE7EF),
    surfaceVariant = Color(0xFF2A2430),
    onSurfaceVariant = Color(0xFFB5ACB9),
    outline = Color(0xFF4B4051),
    outlineVariant = Color(0xFF382F3D)
)

@Composable
fun MomoraAITheme(
    darkTheme: Boolean = ThemeManager.isDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
