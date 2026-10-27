package com.example.SIMATS.ui.profile

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
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraSubheaderPurple
import com.example.SIMATS.ui.theme.MomoraTealPrimary
import com.example.SIMATS.ui.theme.MomoraTextBody
import com.example.SIMATS.ui.theme.MomoraTextHeadline
import com.example.SIMATS.ui.theme.MomoraTextMuted

@Composable
fun ProfileSetupScreen(
    viewModel: ProfileSetupViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute, uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.onEvent(ProfileSetupUiEvent.OnNavigationHandled)
        } else {
            uiState.navigateToRoute?.let { route ->
                onNavigateTo(route)
                viewModel.onEvent(ProfileSetupUiEvent.OnNavigationHandled)
            }
        }
    }

    ProfileSetupScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun ProfileSetupScreenContent(
    uiState: ProfileSetupUiState,
    onEvent: (ProfileSetupUiEvent) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

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
                    onClick = { onEvent(ProfileSetupUiEvent.OnBackClicked) },
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

            // Step Indicator: GETTING TO KNOW YOU - 2 OF 5
            Text(
                text = uiState.stepText,
                style = MaterialTheme.typography.labelMedium,
                color = MomoraSubheaderPurple,
                letterSpacing = 1.1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Headline: Let's start with you
            Text(
                text = "Let's start with you",
                style = MaterialTheme.typography.headlineLarge,
                color = MomoraTextHeadline,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Sub-caption
            Text(
                text = "A few details help us make this space yours.",
                style = MaterialTheme.typography.bodyLarge,
                color = MomoraTextBody
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar (2 OF 5)
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

            // Field 1: Preferred name
            Text(
                text = "Preferred name",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.preferredName,
                onValueChange = { onEvent(ProfileSetupUiEvent.OnPreferredNameChanged(it)) },
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

            // Field 2: Date of birth
            Text(
                text = "Date of birth",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.dateOfBirth,
                onValueChange = { onEvent(ProfileSetupUiEvent.OnDateOfBirthChanged(it)) },
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
                        contentDescription = "Calendar",
                        tint = MomoraTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Field 3: Is this your first pregnancy?
            Text(
                text = "Is this your first pregnancy?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Option "Yes, my first"
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (uiState.isFirstPregnancy) MomoraTealPrimary else Color.White
                        )
                        .clickable { onEvent(ProfileSetupUiEvent.OnFirstPregnancyChanged(true)) }
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Yes, my first",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (uiState.isFirstPregnancy) Color.White else MomoraTextHeadline
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Option "No"
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (!uiState.isFirstPregnancy) MomoraTealPrimary else Color.White
                        )
                        .clickable { onEvent(ProfileSetupUiEvent.OnFirstPregnancyChanged(false)) }
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "No",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (!uiState.isFirstPregnancy) Color.White else MomoraTextHeadline
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Field 4: Pregnancy type
            Text(
                text = "Pregnancy type",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.pregnancyType,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDropdownExpanded = true },
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
                        IconButton(onClick = { isDropdownExpanded = !isDropdownExpanded }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_down),
                                contentDescription = "Select Pregnancy Type",
                                tint = MomoraTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    singleLine = true
                )

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.White)
                ) {
                    uiState.pregnancyTypeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option, color = MomoraTextHeadline) },
                            onClick = {
                                onEvent(ProfileSetupUiEvent.OnPregnancyTypeChanged(option))
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Field 5: Care team (optional)
            Text(
                text = "Care team (optional)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.careTeam,
                onValueChange = { onEvent(ProfileSetupUiEvent.OnCareTeamChanged(it)) },
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
                        painter = painterResource(id = R.drawable.ic_hospital),
                        contentDescription = null,
                        tint = MomoraTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Helper text
            Text(
                text = uiState.calculatedAgeText,
                style = MaterialTheme.typography.bodySmall,
                color = MomoraTextMuted
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Button: Continue to pregnancy dates
            Button(
                onClick = { onEvent(ProfileSetupUiEvent.OnContinueClicked) },
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
                    text = "Continue to pregnancy dates",
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
fun ProfileSetupScreenPreview() {
    MomoraAITheme {
        ProfileSetupScreenContent(
            uiState = ProfileSetupUiState(),
            onEvent = {}
        )
    }
}
