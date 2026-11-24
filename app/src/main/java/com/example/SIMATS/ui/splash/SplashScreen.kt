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
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.UserAuthState
import com.example.SIMATS.ui.theme.InterFontFamily
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraIconBtnBg
import com.example.SIMATS.ui.theme.MomoraLavenderCircleBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    modifier: Modifier = Modifier,
    onNavigateNext: (UserAuthState) -> Unit = {},
    onUseDifferentAccount: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(SplashUiEvent.OnCheckAuthState)
    }

    // Never navigate automatically. Wait for user tap.
    val hasValidSession = if (uiState is SplashUiState.Success) {
        (uiState as SplashUiState.Success).authState is UserAuthState.Authenticated
    } else {
        SessionManager.isLoggedIn()
    }

    SplashScreenContent(
        modifier = modifier,
        hasValidSession = hasValidSession,
        onFavoriteClick = { viewModel.onEvent(SplashUiEvent.OnFavoriteClicked) },
        onContinueClick = {
            val state = (uiState as? SplashUiState.Success)?.authState
            val targetState = if (state is UserAuthState.Authenticated) {
                state
            } else {
                UserAuthState.Authenticated(SessionManager.getUserRole())
            }
            onNavigateNext(targetState)
        },
        onGetStartedClick = {
            onNavigateNext(UserAuthState.Unauthenticated)
        },
        onUseDifferentAccountClick = {
            onUseDifferentAccount()
        }
    )
}

@Composable
fun SplashScreenContent(
    modifier: Modifier = Modifier,
    hasValidSession: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onGetStartedClick: () -> Unit = {},
    onUseDifferentAccountClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Bar: Left Heart Icon, Help "?" Icon Removed
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

            // Balanced spacing placeholder (top right '?' help icon removed)
            Spacer(modifier = Modifier.size(40.dp))
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

                // Hands Joining Heart Line Art Illustration (Exact original from git commit)
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
                fontFamily = InterFontFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Main Subtitle
            Text(
                text = "A little care. Every day.",
                fontFamily = InterFontFamily,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleText
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Body Description
            Text(
                text = "Personalized pregnancy monitoring and gentle\nguidance, together.",
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MomoraBodyMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Badge Pill Tag: "YOUR JOURNEY, SUPPORTED"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MomoraMintBadgeBg)
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "YOUR JOURNEY, SUPPORTED",
                    fontFamily = InterFontFamily,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraMintBadgeText,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // 3. Bottom Section (Page dots removed, No automatic navigation, Buttons wait for tap)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            if (hasValidSession) {
                // Continue Button for valid saved session
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(52.dp)
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

                // Small text link to switch accounts
                Text(
                    text = "Use a different account",
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = MomoraPurplePrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onUseDifferentAccountClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            } else {
                // Get Started Button when no saved session exists
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(52.dp)
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
            }

            Spacer(modifier = Modifier.height(16.dp))

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
 * Preserved exactly as in the original git commit.
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

/**
 * Top bar / badge Heart outline icon canvas drawing.
 * Preserved exactly as in the original git commit.
 */
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

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun SplashScreenPreview() {
    SplashScreenContent()
}
