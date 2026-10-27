package com.example.SIMATS.ui.kickcounter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.SIMATS.ui.home.BottomNavBar
import com.example.SIMATS.ui.theme.MomoraAITheme
import com.example.SIMATS.ui.theme.MomoraMintLight
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraTealPrimary
import com.example.SIMATS.ui.theme.MomoraTextBody
import com.example.SIMATS.ui.theme.MomoraTextHeadline
import com.example.SIMATS.ui.theme.MomoraTextMuted

@Composable
fun KickCounterScreen(
    viewModel: KickCounterViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute, uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.onEvent(KickCounterUiEvent.OnNavigationHandled)
        } else {
            uiState.navigateToRoute?.let { route ->
                onNavigateTo(route)
                viewModel.onEvent(KickCounterUiEvent.OnNavigationHandled)
            }
        }
    }

    KickCounterScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun KickCounterScreenContent(
    uiState: KickCounterUiState,
    onEvent: (KickCounterUiEvent) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { onEvent(KickCounterUiEvent.OnTabSelected(it)) }
            )
        },
        containerColor = MomoraScreenBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Header Bar: Back Arrow | momora AI | History
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onEvent(KickCounterUiEvent.OnBackClicked) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = "Back",
                            tint = MomoraTextHeadline,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "momora AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraTealPrimary
                    )

                    IconButton(
                        onClick = { onEvent(KickCounterUiEvent.OnHistoryClicked) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_history),
                            contentDescription = "History",
                            tint = MomoraTextHeadline,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Headline: Little movements, noticed
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MomoraTextHeadline,
                    fontSize = 26.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.dateSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MomoraTextMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Instruction Sub-caption
                Text(
                    text = uiState.instructionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MomoraTextBody,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Counter Gauge Circle
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF4F7F6))
                        .border(4.dp, MomoraTealPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "%02d".format(uiState.kickCount),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraTealPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "movements noticed",
                            fontSize = 13.sp,
                            color = MomoraTextBody,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Session Timer Display
                Text(
                    text = "Session time · ${uiState.sessionTimerText}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraTextHeadline
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Button: I felt a movement
                Button(
                    onClick = { onEvent(KickCounterUiEvent.OnFeltMovementClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MomoraTealPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_footprints),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "I felt a movement",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary Button: Finish and save session
                Button(
                    onClick = { onEvent(KickCounterUiEvent.OnFinishAndSaveSessionClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MomoraMintLight,
                        contentColor = MomoraTealPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Finish and save session",
                        style = MaterialTheme.typography.labelLarge,
                        color = MomoraTealPrimary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Warning Card (Soft Coral)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFDF0ED))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_warning),
                            contentDescription = "Warning",
                            tint = Color(0xFFC05646),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = uiState.warningTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB34131)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = uiState.warningDescription,
                                fontSize = 12.sp,
                                color = Color(0xFF804339),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer Disclaimer Note
                Text(
                    text = uiState.disclaimerText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MomoraTextMuted,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun KickCounterScreenPreview() {
    MomoraAITheme {
        KickCounterScreenContent(
            uiState = KickCounterUiState(),
            onEvent = {}
        )
    }
}
