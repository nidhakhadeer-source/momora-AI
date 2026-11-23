package com.example.SIMATS.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
    primaryContainer = Color(0xFF3B2745),
    onPrimaryContainer = Color(0xFFF1E7F5),
    secondary = Color(0xFFC7B6CE),
    onSecondary = Color(0xFF261D2B),
    secondaryContainer = Color(0xFF2E2435),
    onSecondaryContainer = Color(0xFFEDE7EF),
    background = Color(0xFF141218),
    onBackground = Color(0xFFEDE7EF),
    surface = Color(0xFF1F1A24),
    onSurface = Color(0xFFEDE7EF),
    surfaceVariant = Color(0xFF28212F),
    onSurfaceVariant = Color(0xFFB5ACB9),
    outline = Color(0xFF3D3245),
    outlineVariant = Color(0xFF2E2536)
)

@Composable
fun MomoraAITheme(
    darkTheme: Boolean = ThemeManager.isDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
