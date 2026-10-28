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
        onContinueClick = { viewModel.onEvent(OnboardingUiEvent.OnContinueClicked) },
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
    if (uiState.currentPage == 0) {
        OnboardingWelcomeStep(
            modifier = modifier,
            onFavoriteClick = onFavoriteClick,
            onHelpClick = onHelpClick,
            onContinueClick = onContinueClick,
            onLoginClick = onLoginClick
        )
    } else {
        OnboardingInsightsStep(
            modifier = modifier,
            onBackClick = onBackClick,
            onHelpClick = onHelpClick,
            onGetStartedClick = onGetStartedClick,
            onLoginClick = onLoginClick
        )
    }
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
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurplePrimary,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Welcome to Momora AI",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Feel informed, connected and cared for, from your first questions to your baby's arrival.",
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

        // Bottom Controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
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

            Spacer(modifier = Modifier.height(14.dp))

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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "➔",
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
                    fontSize = 12.5.sp,
                    color = MomoraBodyMuted
                )
                Text(
                    text = "Log in",
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
 * Step 2: Onboarding Pregnancy Insights & Guidance (03 Onboarding — Insights)
 */
@Composable
private fun OnboardingInsightsStep(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onGetStartedClick: () -> Unit = {},
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
        // Top Bar (Back Button + Help Button)
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
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = MomoraPurpleText
                )
            }

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

        // Scrollable Body Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Hero Card: Sleeping Baby Artwork Container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFAF5EE))
            ) {
                InsightsHeroIllustration(
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Headline
            Text(
                text = "Small insights.\nMeaningful reassurance.",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 31.sp,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle
            Text(
                text = "Follow your pregnancy with thoughtful tools and a connected care team.",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Feature Item 1: Understand your wellbeing
            RowFeatureItem(
                title = "Understand your wellbeing",
                subtitle = "Track vitals, symptoms and weekly growth.",
                iconContent = { PulseIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Feature Item 2: Ask, learn and prepare
            RowFeatureItem(
                title = "Ask, learn and prepare",
                subtitle = "AI education alongside your doctor's care.",
                iconContent = { SparklesIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Medical Disclaimer Pill Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF4ECF8))
                    .padding(horizontal = 14.dp, vertical = 11.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEBE0F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        ShieldIcon(
                            color = MomoraPurplePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "AI guidance is educational, not a diagnosis. It does not replace your maternity team.",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Page Indicator Dots (2nd Dot Selected)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MomoraDotInactive)
                )
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 7.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MomoraDotActive)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MomoraDotInactive)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary "Get started" Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { onGetStartedClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Get started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "➔",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Login Link
            Text(
                text = "Log in to an existing account.",
                fontSize = 12.5.sp,
                color = MomoraBodyMuted,
                modifier = Modifier.clickable { onLoginClick() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Home Bar Indicator
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MomoraBodyMuted
                )
            }
        }

        Text(
            text = "›",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF8C7F92)
        )
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
 * Canvas drawing for Step 2 Sleeping Baby in Womb Illustration.
 */
@Composable
private fun InsightsHeroIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer floral wreath circle
        val wreathPath = Path().apply {
            addOval(Rect(w * 0.15f, h * 0.15f, w * 0.85f, h * 0.85f))
        }
        drawPath(
            path = wreathPath,
            color = Color(0xFF8EA99D).copy(alpha = 0.3f),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Delicate leaves on wreath
        val leaf1 = Path().apply {
            moveTo(w * 0.20f, h * 0.30f)
            cubicTo(w * 0.12f, h * 0.25f, w * 0.15f, h * 0.18f, w * 0.24f, h * 0.22f)
        }
        drawPath(leaf1, color = Color(0xFF8EA99D), style = Stroke(width = 1.8.dp.toPx()))

        val leaf2 = Path().apply {
            moveTo(w * 0.80f, h * 0.30f)
            cubicTo(w * 0.88f, h * 0.25f, w * 0.85f, h * 0.18f, w * 0.76f, h * 0.22f)
        }
        drawPath(leaf2, color = Color(0xFF8EA99D), style = Stroke(width = 1.8.dp.toPx()))

        // Inner soft pink womb circle
        drawCircle(
            color = Color(0xFFF9E7EC),
            radius = w * 0.32f,
            center = Offset(w * 0.50f, h * 0.50f)
        )

        // Sleeping Baby Illustration
        drawCircle(
            color = Color(0xFFF2D1C2),
            radius = w * 0.11f,
            center = Offset(w * 0.44f, h * 0.38f)
        )

        val eyePath = Path().apply {
            moveTo(w * 0.40f, h * 0.38f)
            cubicTo(w * 0.42f, h * 0.41f, w * 0.45f, h * 0.41f, w * 0.47f, h * 0.38f)
        }
        drawPath(eyePath, color = Color(0xFF6E4F76), style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round))

        val bodyPath = Path().apply {
            moveTo(w * 0.44f, h * 0.48f)
            cubicTo(w * 0.65f, h * 0.45f, w * 0.68f, h * 0.65f, w * 0.52f, h * 0.68f)
            cubicTo(w * 0.42f, h * 0.68f, w * 0.38f, h * 0.60f, w * 0.44f, h * 0.55f)
        }
        drawPath(bodyPath, color = Color(0xFFF2D1C2))

        val armPath = Path().apply {
            moveTo(w * 0.46f, h * 0.48f)
            cubicTo(w * 0.52f, h * 0.50f, w * 0.52f, h * 0.56f, w * 0.48f, h * 0.56f)
        }
        drawPath(armPath, color = Color(0xFFE5B9A6), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    }
}

/**
 * Canvas drawing for Heartbeat / ECG Pulse wave icon.
 */
@Composable
private fun PulseIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val pulsePath = Path().apply {
            moveTo(w * 0.10f, h * 0.50f)
            lineTo(w * 0.30f, h * 0.50f)
            lineTo(w * 0.40f, h * 0.25f)
            lineTo(w * 0.55f, h * 0.75f)
            lineTo(w * 0.70f, h * 0.40f)
            lineTo(w * 0.80f, h * 0.50f)
            lineTo(w * 0.90f, h * 0.50f)
        }

        drawPath(
            path = pulsePath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
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
