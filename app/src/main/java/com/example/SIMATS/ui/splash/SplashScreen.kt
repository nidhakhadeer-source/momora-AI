package com.example.SIMATS.ui.splash

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.R
import com.example.SIMATS.ui.theme.MomoraAITheme
import com.example.SIMATS.ui.theme.MomoraMintLight
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraTealPrimary
import com.example.SIMATS.ui.theme.MomoraTextHeadline
import com.example.SIMATS.ui.theme.MomoraTextMuted

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute) {
        uiState.navigateToRoute?.let { route ->
            onNavigateTo(route)
            viewModel.onEvent(SplashUiEvent.OnNavigationHandled)
        }
    }

    SplashScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun SplashScreenContent(
    uiState: SplashUiState,
    onEvent: (SplashUiEvent) -> Unit
) {
    Scaffold(
        containerColor = MomoraScreenBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable { onEvent(SplashUiEvent.OnGetStartedClicked) },
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Bar with Heart Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Top-Left Heart Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFCEEF3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_heart),
                            contentDescription = "Heart Icon",
                            tint = Color(0xFF887288),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Top-Right Shield Heart Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFCEEF3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shield_heart),
                            contentDescription = "Shield Heart Icon",
                            tint = Color(0xFF887288),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Central Graphic Illustration Stack
                Box(
                    modifier = Modifier.size(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Soft Pink Circle
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFCEEF3))
                    )

                    // Inner Soft Purple Circle
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFE4F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_heart_hands),
                            contentDescription = "Heart Hands Illustration",
                            tint = Color(0xFF6A4C6A),
                            modifier = Modifier.size(72.dp)
                        )
                    }

                    // Floating Small Heart Badge on Top Right
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 28.dp, end = 28.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MomoraMintLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_heart),
                            contentDescription = null,
                            tint = MomoraTealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logo & Headline Content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A4C6A),
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = uiState.headline,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraTextHeadline,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = uiState.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MomoraTextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Soft Mint Pill Badge Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MomoraMintLight)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = uiState.badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraTealPrimary,
                            letterSpacing = 1.0.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Page Indicator Dots & Footer Disclaimer
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    // 3 Page Indicator Dots
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(uiState.pageCount) { index ->
                            val isSelected = index == uiState.currentPage
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (isSelected) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFF6A4C6A) else Color(0xFFF5D8E4))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = uiState.footerText,
                        fontSize = 12.sp,
                        color = MomoraTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    MomoraAITheme {
        SplashScreenContent(
            uiState = SplashUiState(),
            onEvent = {}
        )
    }
}
