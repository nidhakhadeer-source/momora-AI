package com.example.SIMATS.ui.healthhistory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.R
import com.example.SIMATS.ui.theme.MomoraAITheme
import com.example.SIMATS.ui.theme.MomoraMintLight
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraSubheaderPurple
import com.example.SIMATS.ui.theme.MomoraTealPrimary
import com.example.SIMATS.ui.theme.MomoraTextBody
import com.example.SIMATS.ui.theme.MomoraTextHeadline
import com.example.SIMATS.ui.theme.MomoraTextMuted

@Composable
fun HealthHistoryScreen(
    viewModel: HealthHistoryViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute, uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.onEvent(HealthHistoryUiEvent.OnNavigationHandled)
        } else {
            uiState.navigateToRoute?.let { route ->
                onNavigateTo(route)
                viewModel.onEvent(HealthHistoryUiEvent.OnNavigationHandled)
            }
        }
    }

    HealthHistoryScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HealthHistoryScreenContent(
    uiState: HealthHistoryUiState,
    onEvent: (HealthHistoryUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Bar: Back Arrow | momora AI | More Options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onEvent(HealthHistoryUiEvent.OnBackClicked) },
                    modifier = Modifier.size(28.dp)
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
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more_dots),
                        contentDescription = "More Options",
                        tint = MomoraTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Indicator: GETTING TO KNOW YOU - 4 OF 5
            Text(
                text = uiState.stepText,
                style = MaterialTheme.typography.labelMedium,
                color = MomoraSubheaderPurple,
                letterSpacing = 1.1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Headline: A fuller picture of you
            Text(
                text = "A fuller picture of you",
                style = MaterialTheme.typography.headlineLarge,
                color = MomoraTextHeadline,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Sub-caption
            Text(
                text = "This helps us suggest relevant topics, not assess or diagnose your risk.",
                style = MaterialTheme.typography.bodyLarge,
                color = MomoraTextBody
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar (4 OF 5)
            LinearProgressIndicator(
                progress = { uiState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MomoraTealPrimary,
                trackColor = Color(0xFFEAE5DC),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question: Has a clinician diagnosed any of these?
            Text(
                text = "Has a clinician diagnosed any of these?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Diagnosis Selection Pills FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.allDiagnosisOptions.forEach { option ->
                    val isSelected = uiState.selectedDiagnoses.contains(option)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) MomoraTealPrimary else Color.White)
                            .clickable { onEvent(HealthHistoryUiEvent.OnDiagnosisToggled(option)) }
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = option,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else MomoraTextHeadline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Field 1: Current medicines or supplements
            Text(
                text = "Current medicines or supplements",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.currentMedicines,
                onValueChange = { onEvent(HealthHistoryUiEvent.OnMedicinesChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = MomoraTealPrimary,
                    unfocusedBorderColor = Color(0xFFE5E7E6),
                    focusedTextColor = MomoraTextHeadline,
                    unfocusedTextColor = MomoraTextHeadline
                ),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pill),
                        contentDescription = null,
                        tint = MomoraTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Field 2: Allergies
            Text(
                text = "Allergies",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.allergies,
                onValueChange = { onEvent(HealthHistoryUiEvent.OnAllergiesChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = MomoraTealPrimary,
                    unfocusedBorderColor = Color(0xFFE5E7E6),
                    focusedTextColor = MomoraTextHeadline,
                    unfocusedTextColor = MomoraTextHeadline
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Field 3: Anything your team is monitoring?
            Text(
                text = "Anything your team is monitoring?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.teamMonitoring,
                onValueChange = { onEvent(HealthHistoryUiEvent.OnTeamMonitoringChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = MomoraTealPrimary,
                    unfocusedBorderColor = Color(0xFFE5E7E6),
                    focusedTextColor = MomoraTextHeadline,
                    unfocusedTextColor = MomoraTextHeadline
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Security Info Card (Mint Green Card)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MomoraMintLight)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shield_check),
                        contentDescription = null,
                        tint = MomoraTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Care that fits your situation",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraTealPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Your clinician sets your care plan. Momora can help you keep notes and prepare questions.",
                            fontSize = 12.sp,
                            color = MomoraTextBody,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Button: Save and continue
            Button(
                onClick = { onEvent(HealthHistoryUiEvent.OnSaveAndContinueClicked) },
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
                    text = "Save and continue",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Link: I'll add this later
            Text(
                text = "I'll add this later",
                style = MaterialTheme.typography.bodySmall,
                color = MomoraTealPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(HealthHistoryUiEvent.OnSkipLaterClicked) },
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HealthHistoryScreenPreview() {
    MomoraAITheme {
        HealthHistoryScreenContent(
            uiState = HealthHistoryUiState(),
            onEvent = {}
        )
    }
}
