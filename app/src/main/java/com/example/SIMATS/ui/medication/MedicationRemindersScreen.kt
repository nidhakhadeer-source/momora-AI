package com.example.SIMATS.ui.medication

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.SIMATS.data.model.MedicationItem
import com.example.SIMATS.data.model.MedicationReminderData
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun MedicationRemindersScreen(
    viewModel: MedicationRemindersViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    MedicationRemindersScreenContent(
        modifier = modifier,
        uiState = uiState,
        onTabSelected = { index -> viewModel.onEvent(MedicationRemindersUiEvent.OnTabSelected(index)) },
        onMarkAsTakenClick = { id -> viewModel.onEvent(MedicationRemindersUiEvent.OnMarkAsTakenClicked(id)) },
        onAddMedicationClick = { viewModel.onEvent(MedicationRemindersUiEvent.OnAddMedicationClicked) },
        onSettingsClick = { viewModel.onEvent(MedicationRemindersUiEvent.OnSettingsClicked) },
        onBackClick = {
            viewModel.onEvent(MedicationRemindersUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(MedicationRemindersUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(MedicationRemindersUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun MedicationRemindersScreenContent(
    modifier: Modifier = Modifier,
    uiState: MedicationRemindersUiState = MedicationRemindersUiState(
        data = MedicationReminderData(
            dateText = "Tuesday, 27 Oct 2026",
            progressStatusText = "1 OF 2 TAKEN · EVENING REMINDER SET",
            medications = listOf(
                MedicationItem(
                    id = 1,
                    name = "Prenatal vitamin",
                    dosageAndTime = "1 tablet · Daily at 08:00 · With food",
                    isTaken = true,
                    takenTimeNote = "Marked taken today at 08:05"
                ),
                MedicationItem(
                    id = 2,
                    name = "Vitamin D · 400 IU",
                    dosageAndTime = "1 tablet · Daily at 20:00",
                    doctorPrescriptionNote = "As prescribed by Dr. Amelia Chen.",
                    isTaken = false,
                    dueLaterText = "Due later"
                )
            )
        )
    ),
    onTabSelected: (Int) -> Unit = {},
    onMarkAsTakenClick: (Int) -> Unit = {},
    onAddMedicationClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.data

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
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

        // 2. Scrollable Body Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "A little daily routine",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = data?.dateText ?: "Tuesday, 27 Oct 2026",
                fontSize = 13.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Segmented Tab Switcher (Today, Schedule, History)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF4ECF8))
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf("Today", "Schedule", "History")
                    tabs.forEachIndexed { index, label ->
                        val isSelected = uiState.selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { onTabSelected(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MomoraPurpleDark else MomoraBodyMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Status Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MomoraMintBadgeBg)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = data?.progressStatusText ?: "1 OF 2 TAKEN · EVENING REMINDER SET",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraMintBadgeText,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "Today's schedule" Section
            Text(
                text = "Today's schedule",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Medication Cards List
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                data?.medications?.forEach { item ->
                    MedicationCard(
                        item = item,
                        onMarkAsTakenClick = { onMarkAsTakenClick(item.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add prescribed medication Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { onAddMedicationClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Add prescribed medication",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Medical Safety Disclaimer Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF4ECF8))
                    .padding(horizontal = 14.dp, vertical = 11.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEBE0F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        ShieldIcon(
                            color = MomoraPurplePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Follow your prescription. Do not start, stop or double a dose based on app reminders. Ask your pharmacist about missed doses.",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Reminder settings link
            Text(
                text = "Reminder settings · Sound & notification privacy ➔",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MomoraPurplePrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSettingsClick() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Bottom Dashboard Navigation Bar
        DashboardBottomNavBar(
            selectedIndex = uiState.selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

/**
 * Single Medication Card Composable.
 */
@Composable
private fun MedicationCard(
    item: MedicationItem,
    onMarkAsTakenClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (item.isTaken) Color.White else Color(0xFFF4ECF8))
            .border(1.dp, if (item.isTaken) Color(0xFFEFE8F4) else Color.Transparent, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (item.isTaken) MomoraMintBadgeBg else Color(0xFFEDE4F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        PillCapsuleIcon(
                            color = if (item.isTaken) MomoraMintBadgeText else MomoraPurplePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.dosageAndTime,
                            fontSize = 11.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }

                if (item.isTaken) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MomoraMintBadgeBg)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Taken",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraMintBadgeText
                        )
                    }
                } else if (!item.dueLaterText.isNullOrEmpty()) {
                    Text(
                        text = item.dueLaterText,
                        fontSize = 11.5.sp,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            if (!item.takenTimeNote.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.takenTimeNote,
                    fontSize = 11.sp,
                    color = MomoraBodyMuted,
                    modifier = Modifier.padding(start = 48.dp)
                )
            }

            if (!item.doctorPrescriptionNote.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.doctorPrescriptionNote,
                    fontSize = 11.5.sp,
                    color = MomoraBodyMuted,
                    modifier = Modifier.padding(start = 48.dp)
                )
            }

            if (!item.isTaken) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(50))
                        .clickable { onMarkAsTakenClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Mark as taken",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                }
            }
        }
    }
}

/**
 * Bottom Navigation Bar for Medication Screen.
 */
@Composable
private fun DashboardBottomNavBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color(0xFFF0E8F4), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(top = 8.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                label = "Home",
                isSelected = selectedIndex == 0,
                onClick = { onTabSelected(0) },
                icon = { HomeNavIcon(color = if (selectedIndex == 0) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Track",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { TrackNavIcon(color = if (selectedIndex == 1) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "AI",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { SparklesIcon(color = if (selectedIndex == 2) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Care",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { CareNavIcon(color = if (selectedIndex == 3) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Profile",
                isSelected = selectedIndex == 4,
                onClick = { onTabSelected(4) },
                icon = { UserNavIcon(color = if (selectedIndex == 4) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(134.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF333333))
        )
    }
}

@Composable
private fun NavTabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFFF4ECF8) else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MomoraPurplePrimary else MomoraBodyMuted
        )
    }
}

@Composable
private fun PillCapsuleIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val pillPath = Path().apply {
            moveTo(w * 0.25f, h * 0.75f)
            lineTo(w * 0.75f, h * 0.25f)
        }
        drawPath(pillPath, color = color, style = Stroke(width = strokeWidth * 2.5f, cap = StrokeCap.Round))
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

@Composable
private fun ShieldIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val shieldPath = Path().apply {
            moveTo(w * 0.50f, h * 0.12f)
            lineTo(w * 0.85f, h * 0.28f)
            cubicTo(w * 0.85f, h * 0.60f, w * 0.65f, h * 0.85f, w * 0.50f, h * 0.90f)
            cubicTo(w * 0.35f, h * 0.85f, w * 0.15f, h * 0.60f, w * 0.15f, h * 0.28f)
            close()
        }

        drawPath(shieldPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val checkPath = Path().apply {
            moveTo(w * 0.38f, h * 0.50f)
            lineTo(w * 0.48f, h * 0.60f)
            lineTo(w * 0.64f, h * 0.40f)
        }
        drawPath(checkPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun HomeNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val housePath = Path().apply {
            moveTo(w * 0.20f, h * 0.80f)
            lineTo(w * 0.20f, h * 0.45f)
            lineTo(w * 0.50f, h * 0.20f)
            lineTo(w * 0.80f, h * 0.45f)
            lineTo(w * 0.80f, h * 0.80f)
            close()
        }
        drawPath(housePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun TrackNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val pulsePath = Path().apply {
            moveTo(w * 0.10f, h * 0.50f)
            lineTo(w * 0.30f, h * 0.50f)
            lineTo(w * 0.40f, h * 0.25f)
            lineTo(w * 0.55f, h * 0.75f)
            lineTo(w * 0.70f, h * 0.40f)
            lineTo(w * 0.80f, h * 0.50f)
            lineTo(w * 0.90f, h * 0.50f)
        }

        drawPath(pulsePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun CareNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val heartPath = Path().apply {
            moveTo(w * 0.50f, h * 0.82f)
            cubicTo(w * 0.20f, h * 0.62f, w * 0.05f, h * 0.42f, w * 0.15f, h * 0.24f)
            cubicTo(w * 0.25f, h * 0.10f, w * 0.45f, h * 0.14f, w * 0.50f, h * 0.28f)
            cubicTo(w * 0.55f, h * 0.14f, w * 0.75f, h * 0.10f, w * 0.85f, h * 0.24f)
            cubicTo(w * 0.95f, h * 0.42f, w * 0.80f, h * 0.62f, w * 0.50f, h * 0.82f)
        }

        drawPath(heartPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun UserNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(color = color, radius = w * 0.20f, center = Offset(w * 0.50f, h * 0.35f), style = Stroke(width = strokeWidth))

        val bodyArc = Path().apply {
            moveTo(w * 0.20f, h * 0.80f)
            cubicTo(w * 0.30f, h * 0.60f, w * 0.70f, h * 0.60f, w * 0.80f, h * 0.80f)
        }
        drawPath(bodyArc, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
private fun SparklesIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val starPath = Path().apply {
            moveTo(w * 0.50f, h * 0.15f)
            cubicTo(w * 0.50f, h * 0.35f, w * 0.65f, h * 0.50f, w * 0.85f, h * 0.50f)
            cubicTo(w * 0.65f, h * 0.50f, w * 0.50f, h * 0.65f, w * 0.50f, h * 0.85f)
            cubicTo(w * 0.50f, h * 0.65f, w * 0.35f, h * 0.50f, w * 0.15f, h * 0.50f)
            cubicTo(w * 0.35f, h * 0.50f, w * 0.50f, h * 0.35f, w * 0.50f, h * 0.15f)
            close()
        }

        drawPath(starPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun MedicationRemindersScreenPreview() {
    MedicationRemindersScreenContent()
}
