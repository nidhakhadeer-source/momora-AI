package com.example.SIMATS.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import com.example.SIMATS.data.model.OnboardingIconType
import com.example.SIMATS.ui.theme.InterFontFamily
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraDotActive
import com.example.SIMATS.ui.theme.MomoraDotInactive
import com.example.SIMATS.ui.theme.MomoraIconBtnBg
import com.example.SIMATS.ui.theme.MomoraLavenderCircleBg
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {},
    onGetStartedComplete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    OnboardingScreenContent(
        modifier = modifier,
        uiState = uiState,
        onFavoriteClick = { viewModel.onEvent(OnboardingUiEvent.OnFavoriteClicked) },
        onHelpClick = { viewModel.onEvent(OnboardingUiEvent.OnHelpClicked) },
        onBackClick = { viewModel.onEvent(OnboardingUiEvent.OnBackClicked) },
        onContinueClick = {
            viewModel.onEvent(OnboardingUiEvent.OnGetStartedClicked)
            onGetStartedComplete()
        },
        onGetStartedClick = {
            viewModel.onEvent(OnboardingUiEvent.OnGetStartedClicked)
            onGetStartedComplete()
        },
        onLoginClick = {
            viewModel.onEvent(OnboardingUiEvent.OnLoginClicked)
            onNavigateToLogin()
        }
    )
}

@Composable
fun OnboardingScreenContent(
    modifier: Modifier = Modifier,
    uiState: OnboardingUiState = OnboardingUiState(),
    onFavoriteClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onGetStartedClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    OnboardingWelcomeStep(
        modifier = modifier,
        onFavoriteClick = onFavoriteClick,
        onHelpClick = onHelpClick,
        onContinueClick = {
            onContinueClick()
        },
        onLoginClick = onLoginClick
    )
}

/**
 * Step 1: Onboarding Welcome Screen Content (02 Onboarding — Welcome)
 */
@Composable
private fun OnboardingWelcomeStep(
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            // Balanced spacing placeholder ('?' help icon removed)
            Spacer(modifier = Modifier.size(40.dp))
        }

        // Scrollable Body Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 175.dp, height = 210.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(MomoraPinkCircleBg)
                        .border(1.dp, Color(0xFFF3E5ED), RoundedCornerShape(percent = 50))
                )

                OnboardingHeroIllustration(
                    modifier = Modifier.size(width = 220.dp, height = 220.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFF4ECF8))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "A COMPANION FOR YOUR PREGNANCY",
                    fontFamily = InterFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurplePrimary,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Welcome to Momora AI",
                fontFamily = InterFontFamily,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Feel informed, connected and cared for, from your first questions to your baby's arrival.",
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MomoraBodyMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureCard(
                    iconType = OnboardingIconType.HEART,
                    title = "Care that knows you",
                    subtitle = "Your journey, your preferences."
                )

                FeatureCard(
                    iconType = OnboardingIconType.SHIELD,
                    title = "Your health, your choice",
                    subtitle = "You control who sees your information."
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Controls (Page dots removed)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { onContinueClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continue",
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "➔",
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    color = MomoraBodyMuted
                )
                Text(
                    text = "Log in",
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurplePrimary,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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
 * Row Item for Feature List.
 */
@Composable
private fun RowFeatureItem(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MomoraLavenderCircleBg.copy(alpha = 0.5f))
            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE4F0)),
                contentAlignment = Alignment.Center
            ) {
                iconContent()
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = MomoraBodyMuted
                )
            }
        }
    }
}

/**
 * Feature Card for Step 1.
 */
@Composable
private fun FeatureCard(
    iconType: OnboardingIconType,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    RowFeatureItem(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        iconContent = {
            if (iconType == OnboardingIconType.HEART) {
                TopBarHeartIcon(
                    color = MomoraPurplePrimary,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                ShieldIcon(
                    color = MomoraPurplePrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    )
}





/**
 * Canvas drawing for Sparkles / AI learning icon.
 */
@Composable
private fun SparklesIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val starPath = Path().apply {
            moveTo(w * 0.50f, h * 0.15f)
            cubicTo(w * 0.50f, h * 0.35f, w * 0.65f, h * 0.50f, w * 0.85f, h * 0.50f)
            cubicTo(w * 0.65f, h * 0.50f, w * 0.50f, h * 0.65f, w * 0.50f, h * 0.85f)
            cubicTo(w * 0.50f, h * 0.65f, w * 0.35f, h * 0.50f, w * 0.15f, h * 0.50f)
            cubicTo(w * 0.35f, h * 0.50f, w * 0.50f, h * 0.35f, w * 0.50f, h * 0.15f)
            close()
        }

        drawPath(
            path = starPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
private fun OnboardingHeroIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val leafPathLeft = Path().apply {
            moveTo(w * 0.25f, h * 0.40f)
            cubicTo(w * 0.15f, h * 0.35f, w * 0.12f, h * 0.25f, w * 0.20f, h * 0.20f)
            cubicTo(w * 0.28f, h * 0.25f, w * 0.28f, h * 0.35f, w * 0.25f, h * 0.40f)
        }
        drawPath(leafPathLeft, color = Color(0xFF8EA99D).copy(alpha = 0.7f))

        val leafPathRight = Path().apply {
            moveTo(w * 0.75f, h * 0.45f)
            cubicTo(w * 0.85f, h * 0.40f, w * 0.88f, h * 0.30f, w * 0.80f, h * 0.25f)
            cubicTo(w * 0.72f, h * 0.30f, w * 0.72f, h * 0.40f, w * 0.75f, h * 0.45f)
        }
        drawPath(leafPathRight, color = Color(0xFF8EA99D).copy(alpha = 0.7f))

        drawCircle(
            color = Color(0xFF382923),
            radius = w * 0.08f,
            center = Offset(w * 0.48f, h * 0.28f)
        )

        drawCircle(
            color = Color(0xFFC68B6E),
            radius = w * 0.06f,
            center = Offset(w * 0.50f, h * 0.29f)
        )

        val dressPath = Path().apply {
            moveTo(w * 0.45f, h * 0.35f)
            cubicTo(w * 0.40f, h * 0.45f, w * 0.32f, h * 0.60f, w * 0.35f, h * 0.75f)
            lineTo(w * 0.65f, h * 0.75f)
            cubicTo(w * 0.68f, h * 0.60f, w * 0.60f, h * 0.45f, w * 0.55f, h * 0.35f)
            close()
        }
        drawPath(dressPath, color = Color(0xFFBD9EAE))

        val bellyCurve = Path().apply {
            moveTo(w * 0.42f, h * 0.50f)
            cubicTo(w * 0.38f, h * 0.58f, w * 0.45f, h * 0.66f, w * 0.55f, h * 0.64f)
        }
        drawPath(
            bellyCurve,
            color = Color(0xFF5A3D63),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        val benchPath = Path().apply {
            moveTo(w * 0.28f, h * 0.74f)
            lineTo(w * 0.72f, h * 0.74f)
            lineTo(w * 0.70f, h * 0.78f)
            lineTo(w * 0.30f, h * 0.78f)
            close()
        }
        drawPath(benchPath, color = Color(0xFFEDE2D2))
    }
}

@Composable
private fun ShieldIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val shieldPath = Path().apply {
            moveTo(w * 0.50f, h * 0.12f)
            lineTo(w * 0.85f, h * 0.28f)
            cubicTo(w * 0.85f, h * 0.60f, w * 0.65f, h * 0.85f, w * 0.50f, h * 0.90f)
            cubicTo(w * 0.35f, h * 0.85f, w * 0.15f, h * 0.60f, w * 0.15f, h * 0.28f)
            close()
        }

        drawPath(
            path = shieldPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val checkPath = Path().apply {
            moveTo(w * 0.38f, h * 0.50f)
            lineTo(w * 0.48f, h * 0.60f)
            lineTo(w * 0.64f, h * 0.40f)
        }
        drawPath(
            path = checkPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
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
fun OnboardingStep1Preview() {
    OnboardingScreenContent(uiState = OnboardingUiState(currentPage = 0))
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun OnboardingStep2Preview() {
    OnboardingScreenContent(uiState = OnboardingUiState(currentPage = 1))
}
