package com.example.SIMATS.ui.hydration

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
fun HydrationLogScreen(
    viewModel: HydrationLogViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute, uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.onEvent(HydrationLogUiEvent.OnNavigationHandled)
        } else {
            uiState.navigateToRoute?.let { route ->
                onNavigateTo(route)
                viewModel.onEvent(HydrationLogUiEvent.OnNavigationHandled)
            }
        }
    }

    HydrationLogScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun HydrationLogScreenContent(
    uiState: HydrationLogUiState,
    onEvent: (HydrationLogUiEvent) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { onEvent(HydrationLogUiEvent.OnTabSelected(it)) }
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
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Header Bar: Back Arrow | momora AI | More Options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onEvent(HydrationLogUiEvent.OnBackClicked) },
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
                        onClick = { },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_more_dots),
                            contentDescription = "More Options",
                            tint = MomoraTealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Headline: One sip at a time
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MomoraTextHeadline,
                    fontSize = 26.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.dateSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MomoraTextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Main Progress Card (Mint Green Card)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MomoraMintLight)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_water_glass),
                                contentDescription = null,
                                tint = MomoraTealPrimary,
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "%.2f L".format(uiState.currentLoggedLiters),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraTextHeadline
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "of your %.1f L daily goal".format(uiState.goalLiters),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MomoraTextBody
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { uiState.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MomoraTealPrimary,
                            trackColor = Color(0xFFD3E4DC),
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = uiState.progressFooterText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MomoraTealPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onEvent(HydrationLogUiEvent.OnEditGoalClicked) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Add Section
                Text(
                    text = "Quick add",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MomoraTextHeadline
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    uiState.quickAddOptionsMl.forEach { amountMl ->
                        val isSelected = amountMl == uiState.selectedAddAmountMl
                        Box(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MomoraTealPrimary else Color.White)
                                .clickable { onEvent(HydrationLogUiEvent.OnQuickAddAmountSelected(amountMl)) }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "$amountMl mL",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else MomoraTextHeadline
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Action Button: + Add 250 mL of water
                Button(
                    onClick = { onEvent(HydrationLogUiEvent.OnAddWaterClicked) },
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
                    Text(
                        text = "+ Add ${uiState.selectedAddAmountMl} mL of water",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Today's sips
                Text(
                    text = "Today's sips",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraTextHeadline
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sips History Card (White)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        uiState.sipsList.forEachIndexed { index, sip ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MomoraMintLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_water_glass),
                                            contentDescription = null,
                                            tint = MomoraTealPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = sip.timeText,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MomoraTextHeadline
                                    )
                                }

                                Text(
                                    text = sip.amountText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraTealPrimary
                                )
                            }

                            if (index < uiState.sipsList.size - 1) {
                                HorizontalDivider(color = Color(0xFFF2F4F3))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Reminders Setting Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Gentle water reminders",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraTextHeadline
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.reminderScheduleText,
                                fontSize = 12.sp,
                                color = MomoraTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Switch(
                            checked = uiState.isRemindersEnabled,
                            onCheckedChange = { onEvent(HydrationLogUiEvent.OnToggleReminders(it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MomoraTealPrimary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFE2E4E3),
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer Disclaimer Note
                Text(
                    text = uiState.disclaimerText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MomoraTextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HydrationLogScreenPreview() {
    MomoraAITheme {
        HydrationLogScreenContent(
            uiState = HydrationLogUiState(),
            onEvent = {}
        )
    }
}
