package com.example.SIMATS.ui.bloodpressure

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
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
fun BloodPressureScreen(
    viewModel: BloodPressureViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute, uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.onEvent(BloodPressureUiEvent.OnNavigationHandled)
        } else {
            uiState.navigateToRoute?.let { route ->
                onNavigateTo(route)
                viewModel.onEvent(BloodPressureUiEvent.OnNavigationHandled)
            }
        }
    }

    BloodPressureScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun BloodPressureScreenContent(
    uiState: BloodPressureUiState,
    onEvent: (BloodPressureUiEvent) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { onEvent(BloodPressureUiEvent.OnTabSelected(it)) }
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
                        onClick = { onEvent(BloodPressureUiEvent.OnBackClicked) },
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

                // Headline: A clearer pressure picture
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MomoraTextHeadline,
                    fontSize = 26.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MomoraTextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Latest Reading Banner Card (Mint)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MomoraMintLight)
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = uiState.latestTag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraTealPrimary,
                            letterSpacing = 1.1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = uiState.latestReading,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraTextHeadline
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = uiState.latestDetails,
                            style = MaterialTheme.typography.bodySmall,
                            color = MomoraTextBody
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Add a reading
                Text(
                    text = "Add a reading",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraTextHeadline
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Side-by-side Fields: Systolic & Diastolic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Systolic Field
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Systolic · mmHg",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MomoraTextHeadline
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = uiState.inputSystolic,
                            onValueChange = { onEvent(BloodPressureUiEvent.OnSystolicChanged(it)) },
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
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    // Diastolic Field
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Diastolic · mmHg",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MomoraTextHeadline
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = uiState.inputDiastolic,
                            onValueChange = { onEvent(BloodPressureUiEvent.OnDiastolicChanged(it)) },
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
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Field: Pulse (optional)
                Text(
                    text = "Pulse (optional)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MomoraTextHeadline
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.inputPulse,
                    onValueChange = { onEvent(BloodPressureUiEvent.OnPulseChanged(it)) },
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

                // Primary Button: Save reading
                Button(
                    onClick = { onEvent(BloodPressureUiEvent.OnSaveReadingClicked) },
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
                        text = "Save reading",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Threshold Advice Card (Mint Green Card)
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
                                text = uiState.thresholdTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraTealPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = uiState.thresholdDescription,
                                fontSize = 12.sp,
                                color = MomoraTextBody,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Readings History Card (White)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        uiState.recentReadings.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = item.dateTimeText,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MomoraTextHeadline
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.pulseText,
                                        fontSize = 12.sp,
                                        color = MomoraTextMuted
                                    )
                                }

                                Text(
                                    text = item.readingText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraTealPrimary
                                )
                            }

                            if (index < uiState.recentReadings.size - 1) {
                                HorizontalDivider(color = Color(0xFFF2F4F3))
                            }
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
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BloodPressureScreenPreview() {
    MomoraAITheme {
        BloodPressureScreenContent(
            uiState = BloodPressureUiState(),
            onEvent = {}
        )
    }
}
