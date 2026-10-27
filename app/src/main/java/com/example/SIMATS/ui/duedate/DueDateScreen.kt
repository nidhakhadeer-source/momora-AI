package com.example.SIMATS.ui.duedate

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.SIMATS.ui.theme.MomoraAITheme
import com.example.SIMATS.ui.theme.MomoraLavenderCard
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraSubheaderPurple
import com.example.SIMATS.ui.theme.MomoraTealPrimary
import com.example.SIMATS.ui.theme.MomoraTextBody
import com.example.SIMATS.ui.theme.MomoraTextHeadline
import com.example.SIMATS.ui.theme.MomoraTextMuted

@Composable
fun DueDateScreen(
    viewModel: DueDateViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute, uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.onEvent(DueDateUiEvent.OnNavigationHandled)
        } else {
            uiState.navigateToRoute?.let { route ->
                onNavigateTo(route)
                viewModel.onEvent(DueDateUiEvent.OnNavigationHandled)
            }
        }
    }

    DueDateScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun DueDateScreenContent(
    uiState: DueDateUiState,
    onEvent: (DueDateUiEvent) -> Unit
) {
    var isCycleDropdownExpanded by remember { mutableStateOf(false) }

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
                    onClick = { onEvent(DueDateUiEvent.OnBackClicked) },
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

            // Step Indicator: GETTING TO KNOW YOU - 3 OF 5
            Text(
                text = uiState.stepText,
                style = MaterialTheme.typography.labelMedium,
                color = MomoraSubheaderPurple,
                letterSpacing = 1.1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Headline: When will you meet?
            Text(
                text = "When will you meet?",
                style = MaterialTheme.typography.headlineLarge,
                color = MomoraTextHeadline,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Sub-caption
            Text(
                text = "Let's find your estimated due date.",
                style = MaterialTheme.typography.bodyLarge,
                color = MomoraTextBody
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar (3 OF 5)
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

            Spacer(modifier = Modifier.height(20.dp))

            // Calculation Method Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                uiState.calculationMethods.forEach { method ->
                    val isSelected = method == uiState.selectedCalculationMethod
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MomoraTealPrimary else Color.White)
                            .clickable { onEvent(DueDateUiEvent.OnCalculationMethodSelected(method)) }
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = method,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else MomoraTextHeadline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Field 1: First day of your last period
            Text(
                text = "First day of your last period",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.lastPeriodDate,
                onValueChange = { onEvent(DueDateUiEvent.OnLastPeriodDateChanged(it)) },
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
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Select Date",
                        tint = MomoraTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Field 2: Average cycle length
            Text(
                text = "Average cycle length",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.cycleLength,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCycleDropdownExpanded = true },
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
                        IconButton(onClick = { isCycleDropdownExpanded = !isCycleDropdownExpanded }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_down),
                                contentDescription = "Select Cycle Length",
                                tint = MomoraTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    singleLine = true
                )

                DropdownMenu(
                    expanded = isCycleDropdownExpanded,
                    onDismissRequest = { isCycleDropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.White)
                ) {
                    uiState.cycleLengthOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option, color = MomoraTextHeadline) },
                            onClick = {
                                onEvent(DueDateUiEvent.OnCycleLengthChanged(option))
                                isCycleDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Result Summary Card (Lavender Card)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MomoraLavenderCard)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "YOUR ESTIMATED DUE DATE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MomoraSubheaderPurple,
                        letterSpacing = 1.1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = uiState.estimatedDueDate,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraTextHeadline
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = uiState.gestationalAgeText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MomoraTextHeadline
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = uiState.disclaimerText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MomoraTextMuted,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Disclaimer text below card
            Text(
                text = uiState.todayEstimateText,
                style = MaterialTheme.typography.bodySmall,
                color = MomoraTextMuted,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Button: Use this due date
            Button(
                onClick = { onEvent(DueDateUiEvent.OnUseDueDateClicked) },
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
                    text = "Use this due date",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DueDateScreenPreview() {
    MomoraAITheme {
        DueDateScreenContent(
            uiState = DueDateUiState(),
            onEvent = {}
        )
    }
}
