package com.example.SIMATS.ui.doctoravailability

import android.app.TimePickerDialog
import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.DoctorAvailabilitySlot
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import java.util.Locale

@Composable
fun DoctorAvailabilityScreen(
    viewModel: DoctorAvailabilityViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.saveSuccessMessage) {
        uiState.saveSuccessMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(DoctorAvailabilityUiEvent.OnClearMessages)
        }
    }

    DoctorAvailabilityScreenContent(
        modifier = modifier,
        uiState = uiState,
        onDayToggled = { day, active ->
            viewModel.onEvent(DoctorAvailabilityUiEvent.OnDayToggled(day, active))
        },
        onTimeChanged = { day, start, end ->
            viewModel.onEvent(DoctorAvailabilityUiEvent.OnTimeChanged(day, start, end))
        },
        onSlotMinutesChanged = { minutes ->
            viewModel.onEvent(DoctorAvailabilityUiEvent.OnSlotMinutesChanged(minutes))
        },
        onSaveClick = {
            viewModel.onEvent(DoctorAvailabilityUiEvent.OnSaveClicked)
        },
        onRetryClick = {
            viewModel.onEvent(DoctorAvailabilityUiEvent.OnRetryClicked)
        },
        onBackClick = {
            viewModel.onEvent(DoctorAvailabilityUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = {
            viewModel.onEvent(DoctorAvailabilityUiEvent.OnNotificationClicked)
            onNotificationClick()
        }
    )
}

@Composable
fun DoctorAvailabilityScreenContent(
    modifier: Modifier = Modifier,
    uiState: DoctorAvailabilityUiState = DoctorAvailabilityUiState(),
    onDayToggled: (String, Boolean) -> Unit = { _, _ -> },
    onTimeChanged: (String, String, String) -> Unit = { _, _, _ -> },
    onSlotMinutesChanged: (Int) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // 1. Top Bar
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

            Text(
                text = "Clinic Availability",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                BellIcon(
                    color = MomoraPurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. Main Content
        if (uiState.isLoading && uiState.weeklyHours.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MomoraPurplePrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading clinic availability...",
                        fontSize = 14.sp,
                        color = MomoraBodyMuted
                    )
                }
            }
        } else if (uiState.errorMessage != null && uiState.weeklyHours.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.errorMessage,
                        fontSize = 14.sp,
                        color = Color(0xFFA94442),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MomoraPurplePrimary)
                            .clickable { onRetryClick() }
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Retry",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Weekly Schedule",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Configure weekly consultation hours and appointment slot duration.",
                    fontSize = 13.sp,
                    color = MomoraBodyMuted,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Slot Length Selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Appointment Slot Duration",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Standard duration for each patient appointment booking.",
                            fontSize = 11.5.sp,
                            color = MomoraBodyMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val durations = listOf(15, 20, 30, 45, 60)
                            durations.forEach { mins ->
                                val isSelected = uiState.slotMinutes == mins
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) MomoraPurplePrimary else Color(0xFFF4ECF8))
                                        .clickable { onSlotMinutesChanged(mins) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        fontSize = 12.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MomoraPurpleDark
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Inline error banner if any
                if (uiState.errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFDF2F2))
                            .border(1.dp, Color(0xFFF2DEDE), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFFA94442)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = "Weekly Hours",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Weekly Schedule Cards
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.weeklyHours.forEach { slot ->
                        AvailabilityDayCard(
                            slot = slot,
                            onToggle = { active -> onDayToggled(slot.weekday, active) },
                            onPickStartTime = {
                                val parts = slot.startTime.split(":")
                                val initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 9
                                val initialMin = parts.getOrNull(1)?.toIntOrNull() ?: 0

                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val newStart = String.format(Locale.US, "%02d:%02d", hour, minute)
                                        onTimeChanged(slot.weekday, newStart, slot.endTime)
                                    },
                                    initialHour,
                                    initialMin,
                                    true
                                ).show()
                            },
                            onPickEndTime = {
                                val parts = slot.endTime.split(":")
                                val initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 17
                                val initialMin = parts.getOrNull(1)?.toIntOrNull() ?: 0

                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val newEnd = String.format(Locale.US, "%02d:%02d", hour, minute)
                                        onTimeChanged(slot.weekday, slot.startTime, newEnd)
                                    },
                                    initialHour,
                                    initialMin,
                                    true
                                ).show()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Availability Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (uiState.isSaving) Color(0xFF8C7F92) else MomoraPurplePrimary)
                        .clickable(enabled = !uiState.isSaving) { onSaveClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isSaving) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Saving...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else {
                        Text(
                            text = "Save Availability",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AvailabilityDayCard(
    slot: DoctorAvailabilitySlot,
    onToggle: (Boolean) -> Unit,
    onPickStartTime: () -> Unit,
    onPickEndTime: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                1.dp,
                if (slot.isActive) Color(0xFFEFE8F4) else Color(0xFFF0F0F0),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = slot.weekday,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (slot.isActive) MomoraPurpleDark else MomoraBodyMuted
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (slot.isActive) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MomoraMintBadgeBg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Open",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraMintBadgeText
                            )
                        }
                    }
                }

                Switch(
                    checked = slot.isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MomoraPurplePrimary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFDDD8E2)
                    )
                )
            }

            if (slot.isActive) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Start Time Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF4ECF8))
                            .clickable { onPickStartTime() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = slot.startTime,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                    }

                    Text(
                        text = "to",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MomoraBodyMuted
                    )

                    // End Time Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF4ECF8))
                            .clickable { onPickEndTime() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = slot.endTime,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Closed / Off duty",
                    fontSize = 12.sp,
                    color = MomoraBodyMuted
                )
            }
        }
    }
}

@Composable
private fun BellIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val bellPath = Path().apply {
            moveTo(w * 0.25f, h * 0.65f)
            lineTo(w * 0.75f, h * 0.65f)
            cubicTo(w * 0.70f, h * 0.40f, w * 0.65f, h * 0.25f, w * 0.50f, h * 0.25f)
            cubicTo(w * 0.35f, h * 0.25f, w * 0.30f, h * 0.40f, w * 0.25f, h * 0.65f)
            close()
        }
        drawPath(bellPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawCircle(color = color, radius = strokeWidth * 1.2f, center = Offset(w * 0.50f, h * 0.78f))
    }
}
