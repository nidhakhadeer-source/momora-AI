package com.example.SIMATS.ui.doctorschedule

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
import androidx.compose.ui.geometry.Size
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
import com.example.SIMATS.data.model.DoctorCompactAppointment
import com.example.SIMATS.data.model.DoctorMainAppointment
import com.example.SIMATS.data.model.DoctorScheduleData
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun DoctorScheduleScreen(
    viewModel: DoctorScheduleViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onViewRecordClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    DoctorScheduleScreenContent(
        modifier = modifier,
        uiState = uiState,
        onDateTabSelected = { index -> viewModel.onEvent(DoctorScheduleUiEvent.OnDateTabSelected(index)) },
        onRescheduleClick = { viewModel.onEvent(DoctorScheduleUiEvent.OnRescheduleClicked) },
        onViewRecordClick = {
            viewModel.onEvent(DoctorScheduleUiEvent.OnViewRecordClicked)
            onViewRecordClick()
        },
        onManageAvailabilityClick = { viewModel.onEvent(DoctorScheduleUiEvent.OnManageAvailabilityClicked) },
        onCalendarClick = { viewModel.onEvent(DoctorScheduleUiEvent.OnCalendarClicked) },
        onBackClick = {
            viewModel.onEvent(DoctorScheduleUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(DoctorScheduleUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(DoctorScheduleUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun DoctorScheduleScreenContent(
    modifier: Modifier = Modifier,
    uiState: DoctorScheduleUiState = DoctorScheduleUiState(
        scheduleData = DoctorScheduleData(
            doctorName = "Dr Amelia Chen",
            dateText = "Thursday, 29 Oct 2026",
            dateTabs = listOf("27 Oct", "28 Oct", "29 Oct"),
            mainAppointment = DoctorMainAppointment(
                timeRange = "10:30–11:00",
                statusText = "Confirmed",
                patientName = "Maya Patel",
                patientSubtitle = "24w 3d on 27 Oct · Patient P-13042",
                patientIdText = "P-13042",
                notesText = "Routine antenatal check · In person, Willow Maternity Clinic · Room 3."
            ),
            otherAppointments = listOf(
                DoctorCompactAppointment(
                    id = "1",
                    patientName = "Laila Ahmed",
                    timeText = "09:00",
                    visitTypeAndMode = "Routine antenatal · In person",
                    statusText = "Confirmed",
                    isVideo = false
                ),
                DoctorCompactAppointment(
                    id = "2",
                    patientName = "Emma Wilson",
                    timeText = "14:00",
                    visitTypeAndMode = "Follow-up · Video visit",
                    statusText = "Confirmed",
                    isVideo = true
                )
            ),
            visitsSummaryNote = "Today, 27 Oct · 6 visits; 2 completed, 4 upcoming. Next: Nora Williams at 11:30."
        )
    ),
    onDateTabSelected: (Int) -> Unit = {},
    onRescheduleClick: () -> Unit = {},
    onViewRecordClick: () -> Unit = {},
    onManageAvailabilityClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.scheduleData

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
                text = "Your appointment book",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${data?.doctorName ?: "Dr Amelia Chen"} · Clinic local time",
                fontSize = 13.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Date Tabs (27 Oct, 28 Oct, 29 Oct)
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
                    val tabs = data?.dateTabs ?: listOf("27 Oct", "28 Oct", "29 Oct")
                    tabs.forEachIndexed { index, label ->
                        val isSelected = uiState.selectedDateTabIndex == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { onDateTabSelected(index) },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Date Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data?.dateText ?: "Thursday, 29 Oct 2026",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )

                Text(
                    text = "Calendar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MomoraPurplePrimary,
                    modifier = Modifier.clickable { onCalendarClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Highlighted Appointment Card (Maya Patel)
            val mainAppt = data?.mainAppointment
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF4ECF8))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mainAppt?.timeRange ?: "10:30–11:00",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MomoraMintBadgeBg)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = mainAppt?.statusText ?: "Confirmed",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraMintBadgeText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEDE4F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                UserNavIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = mainAppt?.patientName ?: "Maya Patel",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraPurpleDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = mainAppt?.patientSubtitle ?: "24w 3d on 27 Oct · Patient P-13042",
                                    fontSize = 11.5.sp,
                                    color = MomoraBodyMuted
                                )
                            }
                        }

                        Text(
                            text = "›",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF8C7F92)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = mainAppt?.notesText ?: "Routine antenatal check · In person, Willow Maternity Clinic · Room 3.",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = Color(0xFF4A3F50)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(50))
                                .clickable { onRescheduleClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Reschedule",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurpleDark
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MomoraPurplePrimary)
                                .clickable { onViewRecordClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "View record",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Other Compact Appointments List
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                data?.otherAppointments?.forEach { item ->
                    DoctorCompactAppointmentCard(item = item)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visits Summary Caption
            Text(
                text = data?.visitsSummaryNote ?: "Today, 27 Oct · 6 visits; 2 completed, 4 upcoming. Next: Nora Williams at 11:30.",
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Manage clinic availability Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(50))
                    .clickable { onManageAvailabilityClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Manage clinic availability",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurplePrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Doctor Bottom Navigation Bar
        DoctorBottomNavBar(
            selectedIndex = uiState.selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

@Composable
private fun DoctorCompactAppointmentCard(
    item: DoctorCompactAppointment
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val iconBg = if (item.isVideo) Color(0xFFEDE4F0) else MomoraMintBadgeBg
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isVideo) {
                        VideoIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                    } else {
                        ClinicIcon(color = MomoraMintBadgeText, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "${item.patientName} · ${item.timeText}",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.visitTypeAndMode,
                        fontSize = 11.5.sp,
                        color = MomoraBodyMuted
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (item.isVideo) Color(0xFFF4ECF8) else MomoraMintBadgeBg)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isVideo) MomoraPurplePrimary else MomoraMintBadgeText
                )
            }
        }
    }
}

/**
 * Doctor Bottom Navigation Bar.
 */
@Composable
private fun DoctorBottomNavBar(
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
                label = "Patients",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { PeopleNavIcon(color = if (selectedIndex == 1) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Schedule",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { CalendarNavIcon(color = if (selectedIndex == 2) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Advice",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { ChatNavIcon(color = if (selectedIndex == 3) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
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
private fun ClinicIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRect(color = color, style = Stroke(width = strokeWidth))
        drawLine(color = color, start = Offset(w * 0.50f, h * 0.30f), end = Offset(w * 0.50f, h * 0.70f), strokeWidth = strokeWidth)
        drawLine(color = color, start = Offset(w * 0.30f, h * 0.50f), end = Offset(w * 0.70f, h * 0.50f), strokeWidth = strokeWidth)
    }
}

@Composable
private fun VideoIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRect(
            color = color,
            topLeft = Offset(w * 0.15f, h * 0.28f),
            size = Size(w * 0.50f, h * 0.44f),
            style = Stroke(width = strokeWidth)
        )

        val trianglePath = Path().apply {
            moveTo(w * 0.65f, h * 0.40f)
            lineTo(w * 0.85f, h * 0.28f)
            lineTo(w * 0.85f, h * 0.72f)
            lineTo(w * 0.65f, h * 0.60f)
            close()
        }
        drawPath(trianglePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
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
private fun PeopleNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(color = color, radius = w * 0.18f, center = Offset(w * 0.50f, h * 0.32f), style = Stroke(width = strokeWidth))
        val bodyPath = Path().apply {
            moveTo(w * 0.20f, h * 0.80f)
            cubicTo(w * 0.30f, h * 0.58f, w * 0.70f, h * 0.58f, w * 0.80f, h * 0.80f)
        }
        drawPath(bodyPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
private fun CalendarNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRect(color = color, style = Stroke(width = strokeWidth))
        drawLine(color = color, start = Offset(0f, h * 0.35f), end = Offset(w, h * 0.35f), strokeWidth = strokeWidth)
    }
}

@Composable
private fun ChatNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val chatPath = Path().apply {
            moveTo(w * 0.18f, h * 0.25f)
            lineTo(w * 0.82f, h * 0.25f)
            lineTo(w * 0.82f, h * 0.65f)
            lineTo(w * 0.45f, h * 0.65f)
            lineTo(w * 0.25f, h * 0.82f)
            lineTo(w * 0.25f, h * 0.65f)
            lineTo(w * 0.18f, h * 0.65f)
            close()
        }
        drawPath(chatPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
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

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun DoctorScheduleScreenPreview() {
    DoctorScheduleScreenContent()
}
