package com.example.SIMATS.ui.goals

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraSubheaderPurple
import com.example.SIMATS.ui.theme.MomoraTealPrimary
import com.example.SIMATS.ui.theme.MomoraTextBody
import com.example.SIMATS.ui.theme.MomoraTextHeadline
import com.example.SIMATS.ui.theme.MomoraTextMuted

@Composable
fun GoalsPreferencesScreen(
    viewModel: GoalsPreferencesViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute, uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.onEvent(GoalsPreferencesUiEvent.OnNavigationHandled)
        } else {
            uiState.navigateToRoute?.let { route ->
                onNavigateTo(route)
                viewModel.onEvent(GoalsPreferencesUiEvent.OnNavigationHandled)
            }
        }
    }

    GoalsPreferencesScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoalsPreferencesScreenContent(
    uiState: GoalsPreferencesUiState,
    onEvent: (GoalsPreferencesUiEvent) -> Unit
) {
    var isFoodDropdownExpanded by remember { mutableStateOf(false) }
    var isUnitDropdownExpanded by remember { mutableStateOf(false) }

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
                    onClick = { onEvent(GoalsPreferencesUiEvent.OnBackClicked) },
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

            // Step Indicator: GETTING TO KNOW YOU - 5 OF 5
            Text(
                text = uiState.stepText,
                style = MaterialTheme.typography.labelMedium,
                color = MomoraSubheaderPurple,
                letterSpacing = 1.1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Headline: Support, your way
            Text(
                text = "Support, your way",
                style = MaterialTheme.typography.headlineLarge,
                color = MomoraTextHeadline,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Sub-caption
            Text(
                text = "Pick what matters to you. We'll keep it gentle.",
                style = MaterialTheme.typography.bodyLarge,
                color = MomoraTextBody
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar (5 OF 5 - 100%)
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

            // Question: I'd like help with...
            Text(
                text = "I'd like help with...",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Topic Selection Pills FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.allTopicOptions.forEach { topic ->
                    val isSelected = uiState.selectedTopics.contains(topic)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) MomoraTealPrimary else Color.White)
                            .clickable { onEvent(GoalsPreferencesUiEvent.OnTopicToggled(topic)) }
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = topic,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else MomoraTextHeadline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card: Notification Preferences
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(vertical = 12.dp, horizontal = 20.dp)
            ) {
                Column {
                    // Item 1: A morning check-in
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "A morning check-in",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraTextHeadline
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "A quiet nudge at 8:00 AM",
                                fontSize = 12.sp,
                                color = MomoraTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Switch(
                            checked = uiState.isMorningCheckInEnabled,
                            onCheckedChange = { onEvent(GoalsPreferencesUiEvent.OnToggleMorningCheckIn(it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MomoraTealPrimary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFE2E4E3),
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF2F4F3))

                    // Item 2: Evening reflection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Evening reflection",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraTextHeadline
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Wind down at 8:30 PM",
                                fontSize = 12.sp,
                                color = MomoraTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Switch(
                            checked = uiState.isEveningReflectionEnabled,
                            onCheckedChange = { onEvent(GoalsPreferencesUiEvent.OnToggleEveningReflection(it)) },
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Field 1: Food preference
            Text(
                text = "Food preference",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.foodPreference,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isFoodDropdownExpanded = true },
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
                            painter = painterResource(id = R.drawable.ic_leaf),
                            contentDescription = "Food Preference",
                            tint = MomoraTealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true
                )

                DropdownMenu(
                    expanded = isFoodDropdownExpanded,
                    onDismissRequest = { isFoodDropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.White)
                ) {
                    uiState.foodPreferenceOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option, color = MomoraTextHeadline) },
                            onClick = {
                                onEvent(GoalsPreferencesUiEvent.OnFoodPreferenceChanged(option))
                                isFoodDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Field 2: Measurement units
            Text(
                text = "Measurement units",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.measurementUnits,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isUnitDropdownExpanded = true },
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
                        IconButton(onClick = { isUnitDropdownExpanded = !isUnitDropdownExpanded }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_down),
                                contentDescription = "Select Measurement Units",
                                tint = MomoraTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    singleLine = true
                )

                DropdownMenu(
                    expanded = isUnitDropdownExpanded,
                    onDismissRequest = { isUnitDropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.White)
                ) {
                    uiState.measurementUnitOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option, color = MomoraTextHeadline) },
                            onClick = {
                                onEvent(GoalsPreferencesUiEvent.OnMeasurementUnitsChanged(option))
                                isUnitDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Helper text
            Text(
                text = "Notification permission will be requested by Android. You can use the app without reminders.",
                style = MaterialTheme.typography.bodySmall,
                color = MomoraTextMuted,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Button: Meet my Momora
            Button(
                onClick = { onEvent(GoalsPreferencesUiEvent.OnMeetMomoraClicked) },
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
                        painter = painterResource(id = R.drawable.ic_sparkles),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Meet my Momora",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GoalsPreferencesScreenPreview() {
    MomoraAITheme {
        GoalsPreferencesScreenContent(
            uiState = GoalsPreferencesUiState(),
            onEvent = {}
        )
    }
}
