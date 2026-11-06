package com.example.SIMATS.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.SIMATS.R

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold)
)

val InterFontFamily = Inter

private val defaultTypography = Typography()

val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = Inter),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = Inter),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = Inter),
    headlineLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.2).sp,
        color = MomoraTextHeadline
    ),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = Inter),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = Inter),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = Inter),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = Inter),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = Inter),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp,
        color = MomoraTextBody
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = MomoraTextBody
    ),
    bodySmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        color = MomoraTextMuted
    ),
    labelLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.2.sp,
        color = MomoraSubheaderPurple
    ),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = Inter)
)
