package com.example.SIMATS.ui.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraDotActive
import com.example.SIMATS.ui.theme.MomoraDotInactive
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraIconBtnBg
import com.example.SIMATS.ui.theme.MomoraLavenderCircleBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    modifier: Modifier = Modifier,
    onNavigateNext: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.Success) {
            delay(1500)
            onNavigateNext()
        }
    }

    SplashScreenContent(
        modifier = modifier,
        onFavoriteClick = { viewModel.onEvent(SplashUiEvent.OnFavoriteClicked) },
        onHelpClick = { viewModel.onEvent(SplashUiEvent.OnHelpClicked) },
        onNextClick = { onNavigateNext() }
    )
}

@Composable
fun SplashScreenContent(
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable { onNextClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Top Left Heart Icon Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MomoraIconBtnBg)
                    .clickable { onFavoriteClick() },
                contentAlignment = Alignment.Center
            ) {
                TopBarHeartIcon(
                    color = MomoraPurplePrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Top Right Help Icon Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MomoraIconBtnBg)
                    .clickable { onHelpClick() },
                contentAlignment = Alignment.Center
            ) {
                TopBarHelpIcon(
                    color = MomoraPurplePrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 2. Central Graphic & Brand Text Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Central Circular Hero Illustration
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                // Outer Light Pink Circle
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .background(MomoraPinkCircleBg)
                )

                // Inner Soft Lavender Circle
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(MomoraLavenderCircleBg)
                )

                // Hands Joining Heart Line Art Illustration
                HandsHeartIllustration(
                    color = MomoraPurplePrimary,
                    modifier = Modifier.size(120.dp)
                )

                // Top-Right Mint Badge with Heart Icon
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-22).dp, y = 22.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MomoraMintBadgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    TopBarHeartIcon(
                        color = MomoraMintBadgeText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // App Title
            Text(
                text = "momora AI",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Main Subtitle
            Text(
                text = "A little care. Every day.",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleText
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Body Description
            Text(
                text = "Personalized pregnancy monitoring and gentle\nguidance, together.",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MomoraBodyMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Badge Pill Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MomoraMintBadgeBg)
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "YOUR JOURNEY, SUPPORTED",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraMintBadgeText,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // 3. Bottom Section (Dots, Footer, Home Bar)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            // Carousel Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(MomoraDotActive)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MomoraDotInactive)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MomoraDotInactive)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Text
            Text(
                text = "Private by design. Made with compassion.",
                fontSize = 11.5.sp,
                color = MomoraFooterText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Navigation Bar Line Indicator
            Box(
                modifier = Modifier
                    .width(134.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF333333))
            )
        }
    }
}

/**
 * Custom Canvas drawing for the central hand-holding heart line art illustration.
 */
@Composable
private fun HandsHeartIllustration(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 3.5.dp.toPx()

        val heartPath = Path().apply {
            moveTo(w * 0.50f, h * 0.78f)
            cubicTo(
                w * 0.32f, h * 0.70f,
                w * 0.16f, h * 0.52f,
                w * 0.20f, h * 0.35f
            )
            cubicTo(
                w * 0.24f, h * 0.20f,
                w * 0.44f, h * 0.20f,
                w * 0.50f, h * 0.34f
            )
            cubicTo(
                w * 0.56f, h * 0.20f,
                w * 0.76f, h * 0.20f,
                w * 0.80f, h * 0.35f
            )
            cubicTo(
                w * 0.84f, h * 0.52f,
                w * 0.68f, h * 0.70f,
                w * 0.50f, h * 0.78f
            )
        }

        drawPath(
            path = heartPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val leftHandDetail = Path().apply {
            moveTo(w * 0.30f, h * 0.52f)
            cubicTo(
                w * 0.40f, h * 0.45f,
                w * 0.45f, h * 0.55f,
                w * 0.50f, h * 0.58f
            )
        }
        drawPath(
            path = leftHandDetail,
            color = color,
            style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val rightHandDetail = Path().apply {
            moveTo(w * 0.70f, h * 0.52f)
            cubicTo(
                w * 0.60f, h * 0.45f,
                w * 0.55f, h * 0.55f,
                w * 0.50f, h * 0.58f
            )
        }
        drawPath(
            path = rightHandDetail,
            color = color,
            style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
private fun TopBarHeartIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val heartPath = Path().apply {
            moveTo(w * 0.50f, h * 0.82f)
            cubicTo(
                w * 0.20f, h * 0.62f,
                w * 0.05f, h * 0.42f,
                w * 0.15f, h * 0.24f
            )
            cubicTo(
                w * 0.25f, h * 0.10f,
                w * 0.45f, h * 0.14f,
                w * 0.50f, h * 0.28f
            )
            cubicTo(
                w * 0.55f, h * 0.14f,
                w * 0.75f, h * 0.10f,
                w * 0.85f, h * 0.24f
            )
            cubicTo(
                w * 0.95f, h * 0.42f,
                w * 0.80f, h * 0.62f,
                w * 0.50f, h * 0.82f
            )
        }

        drawPath(
            path = heartPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
private fun TopBarHelpIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = size.minDimension * 0.45f,
            style = Stroke(width = strokeWidth)
        )

        val qPath = Path().apply {
            moveTo(w * 0.38f, h * 0.38f)
            cubicTo(
                w * 0.38f, h * 0.28f,
                w * 0.62f, h * 0.28f,
                w * 0.62f, h * 0.42f
            )
            cubicTo(
                w * 0.62f, h * 0.52f,
                w * 0.50f, h * 0.52f,
                w * 0.50f, h * 0.62f
            )
        }
        drawPath(
            path = qPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawCircle(
            color = color,
            radius = strokeWidth * 0.8f,
            center = Offset(w * 0.50f, h * 0.72f)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun SplashScreenPreview() {
    SplashScreenContent()
}
