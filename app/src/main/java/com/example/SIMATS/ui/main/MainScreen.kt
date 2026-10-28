package com.example.SIMATS.ui.main

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
import com.example.SIMATS.data.model.DoctorDashboardData
import com.example.SIMATS.data.model.DoctorNextAppointment
import com.example.SIMATS.data.model.DoctorReviewItem
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraIconBtnBg
import com.example.SIMATS.ui.theme.MomoraLavenderCircleBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    MainScreenContent(
        modifier = modifier,
        uiState = uiState,
        onNavTabSelected = { index -> viewModel.onEvent(MainUiEvent.OnNavTabSelected(index)) },
        onReviewPatientClick = { id -> viewModel.onEvent(MainUiEvent.OnReviewPatientClicked(id)) },
        onViewAssignedPatientsClick = { viewModel.onEvent(MainUiEvent.OnViewAssignedPatientsClicked) },
        onScheduleClick = { viewModel.onEvent(MainUiEvent.OnScheduleClicked) },
        onNextAppointmentClick = { viewModel.onEvent(MainUiEvent.OnNextAppointmentClicked) },
        onNotificationClick = { viewModel.onEvent(MainUiEvent.OnNotificationClicked) }
    )
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    uiState: MainUiState = MainUiState(
        dashboardData = DoctorDashboardData(
            doctorName = "Dr Chen",
            clinicName = "Willow Maternity Clinic",
            dateText = "Tuesday, 27 Oct 2026",
            assignedPatientsCount = 18,
            visitsTodayCount = 6,
            upcomingVisitsCount = 4,
            completedVisitsCount = 2,
            toReviewCount = 2,
            reviewItems = listOf(
                DoctorReviewItem(
                    id = "1",
                    patientName = "Laila Ahmed",
                    gestationalAge = "30w 2d",
                    reportType = "New ultrasound report"
                ),
                DoctorReviewItem(
                    id = "2",
                    patientName = "Sofia Reyes",
                    gestationalAge = "18w 5d",
                    reportType = "New lab report"
                )
            ),
            nextAppointment = DoctorNextAppointment(
                patientName = "Nora Williams",
                timeText = "Today, 11:30",
                visitType = "Routine antenatal",
                mode = "Video",
                futureNote = "Maya Patel's next visit · 29 Oct at 10:30"
            )
        )
    ),
    onNavTabSelected: (Int) -> Unit = {},
    onReviewPatientClick: (String) -> Unit = {},
    onViewAssignedPatientsClick: () -> Unit = {},
    onScheduleClick: () -> Unit = {},
    onNextAppointmentClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.dashboardData

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
                    .clip(CircleShape)
                    .background(MomoraIconBtnBg),
                contentAlignment = Alignment.Center
            ) {
                TopBarHeartIcon(
                    color = MomoraPurplePrimary,
                    modifier = Modifier.size(18.dp)
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

        // 2. Scrollable Doctor Dashboard Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Good morning, ${data?.doctorName ?: "Dr Chen"}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${data?.dateText ?: "Tuesday, 27 Oct 2026"} · ${data?.clinicName ?: "Willow Maternity Clinic"}",
                fontSize = 12.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Assigned Patients Banner Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MomoraMintBadgeBg)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "${data?.assignedPatientsCount ?: 18} assigned patients",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2C28)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Pre-connected, consent-based care overview.",
                        fontSize = 12.sp,
                        color = Color(0xFF43524E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visits & Reviews Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Visits Today Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MomoraLavenderCircleBg)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Visits today",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MomoraPurplePrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${data?.visitsTodayCount ?: 6}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${data?.upcomingVisitsCount ?: 4} upcoming · ${data?.completedVisitsCount ?: 2} completed",
                            fontSize = 10.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }

                // To Review Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MomoraPinkCircleBg)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "To review",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MomoraBodyMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${data?.toReviewCount ?: 2}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "routine report updates",
                            fontSize = 10.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "Needs your review" Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Needs your review",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )

                Text(
                    text = "View patients",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MomoraPurplePrimary,
                    modifier = Modifier.clickable { onViewAssignedPatientsClick() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Review Items Card List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                data?.reviewItems?.forEachIndexed { index, item ->
                    DoctorReviewRow(
                        item = item,
                        onClick = { onReviewPatientClick(item.id) }
                    )
                    if (index < (data.reviewItems.size - 1)) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "Next appointment" Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next appointment",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )

                Text(
                    text = "Schedule",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MomoraPurplePrimary,
                    modifier = Modifier.clickable { onScheduleClick() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Next Appointment Card
            val nextAppt = data?.nextAppointment
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .clickable { onNextAppointmentClick() }
                    .padding(14.dp)
            ) {
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
                            VideoIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = nextAppt?.patientName ?: "Nora Williams",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurpleDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${nextAppt?.timeText ?: "Today, 11:30"} · ${nextAppt?.visitType ?: "Routine antenatal"} · ${nextAppt?.mode ?: "Video"}",
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
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = nextAppt?.futureNote ?: "Maya Patel's next visit · 29 Oct at 10:30",
                fontSize = 11.5.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(18.dp))

            // View assigned patients Primary Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { onViewAssignedPatientsClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View assigned patients",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "👥",
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Clinical decisions remain with the treating care team.",
                fontSize = 11.sp,
                color = MomoraFooterText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Doctor Bottom Navigation Bar (4 Doctor Tabs)
        DoctorBottomNavBar(
            selectedIndex = uiState.selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

@Composable
private fun DoctorReviewRow(
    item: DoctorReviewItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
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
                DocumentIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = item.patientName,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.gestationalAge} · ${item.reportType}",
                    fontSize = 11.5.sp,
                    color = MomoraBodyMuted
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFF4ECF8))
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Text(
                text = "Review",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurplePrimary
            )
        }
    }
}

/**
 * Doctor Bottom Navigation Bar with 4 Tabs (Home, Patients, Schedule, Advice).
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

/**
 * Canvas vector drawings.
 */
@Composable
private fun DocumentIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val docPath = Path().apply {
            moveTo(w * 0.25f, h * 0.18f)
            lineTo(w * 0.60f, h * 0.18f)
            lineTo(w * 0.75f, h * 0.33f)
            lineTo(w * 0.75f, h * 0.82f)
            lineTo(w * 0.25f, h * 0.82f)
            close()
        }
        drawPath(docPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = color, start = Offset(w * 0.38f, h * 0.45f), end = Offset(w * 0.62f, h * 0.45f), strokeWidth = strokeWidth)
        drawLine(color = color, start = Offset(w * 0.38f, h * 0.60f), end = Offset(w * 0.62f, h * 0.60f), strokeWidth = strokeWidth)
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
private fun TopBarHeartIcon(color: Color, modifier: Modifier = Modifier) {
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

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun MainScreenPreview() {
    MainScreenContent()
}
