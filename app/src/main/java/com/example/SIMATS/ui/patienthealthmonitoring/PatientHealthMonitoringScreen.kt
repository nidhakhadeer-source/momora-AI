package com.example.SIMATS.ui.patienthealthmonitoring

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.PatientHealthMonitoringData
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraLavenderCircleBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun PatientHealthMonitoringScreen(
    viewModel: PatientHealthMonitoringViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onWriteAdviceClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    PatientHealthMonitoringScreenContent(
        modifier = modifier,
        uiState = uiState,
        onWriteAdviceClick = {
            viewModel.onEvent(PatientHealthMonitoringUiEvent.OnWriteAdviceClicked)
            onWriteAdviceClick()
        },
        onBackClick = {
            viewModel.onEvent(PatientHealthMonitoringUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(PatientHealthMonitoringUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(PatientHealthMonitoringUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun PatientHealthMonitoringScreenContent(
    modifier: Modifier = Modifier,
    uiState: PatientHealthMonitoringUiState = PatientHealthMonitoringUiState(
        monitoringData = PatientHealthMonitoringData(
            patientName = "Maya Patel",
            patientInitials = "MP",
            gestationalAge = "24w 3d",
            dueDateText = "Due 13 Feb 2027",
            patientIdText = "P-13042",
            dateText = "27 Oct 2026",
            latestSystolic = 112,
            latestDiastolic = 72,
            bpReadingTime = "08:15",
            latestWeightKg = 64.2,
            weightReadingTime = "08:10",
            systolicTrends = listOf(114f, 110f, 115f, 112f, 116f, 110f, 112f),
            diastolicTrends = listOf(70f, 72f, 68f, 74f, 71f, 73f, 72f),
            additionalVitalsText = "Pulse 76 bpm · Temperature 36.7 °C. Latest symptom: mild heartburn after meals."
        )
    ),
    onWriteAdviceClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.monitoringData

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
                text = "Health monitoring",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Patient-entered readings · ${data?.dateText ?: "27 Oct 2026"}",
                fontSize = 13.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Patient Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF4ECF8))
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = data?.patientInitials ?: "MP",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurplePrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = data?.patientName ?: "Maya Patel",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurpleDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${data?.gestationalAge ?: "24w 3d"} · ${data?.dueDateText ?: "Due 13 Feb 2027"}",
                                fontSize = 11.5.sp,
                                color = MomoraBodyMuted
                            )
                        }
                    }

                    Text(
                        text = data?.patientIdText ?: "P-13042",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurplePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Vitals Overview Cards Row (BP & Weight)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Blood pressure Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MomoraMintBadgeBg)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Blood pressure",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MomoraMintBadgeText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${data?.latestSystolic ?: 112}/${data?.latestDiastolic ?: 72}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "mmHg · ${data?.bpReadingTime ?: "08:15"}",
                            fontSize = 10.5.sp,
                            color = Color(0xFF43524E)
                        )
                    }
                }

                // Weight Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MomoraLavenderCircleBg)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Weight",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MomoraPurplePrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${data?.latestWeightKg ?: 64.2}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "kg · ${data?.weightReadingTime ?: "08:10"}",
                            fontSize = 10.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Line Chart Card (Blood pressure · 7 days)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "Blood pressure · 7 days",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MomoraPurplePrimary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Systolic · mmHg",
                                fontSize = 11.sp,
                                color = MomoraPurplePrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MomoraMintBadgeText)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Diastolic",
                                fontSize = 11.sp,
                                color = MomoraMintBadgeText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Canvas Native Chart Drawing
                    HealthMonitoringChart(
                        systolicTrends = data?.systolicTrends ?: listOf(114f, 110f, 115f, 112f, 116f, 110f, 112f),
                        diastolicTrends = data?.diastolicTrends ?: listOf(70f, 72f, 68f, 74f, 71f, 73f, 72f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Vitals & Symptoms Summary Note
            Text(
                text = data?.additionalVitalsText ?: "Pulse 76 bpm · Temperature 36.7 °C. Latest symptom: mild heartburn after meals.",
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                color = Color(0xFF4A3F50)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Clinical Interpretation Disclaimer Card
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
                        text = "Home readings are self-reported. Interpret trends with clinical assessment and the patient's care plan.",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Write advice for Maya Primary Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { onWriteAdviceClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Write advice for Maya",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "💭",
                        fontSize = 16.sp
                    )
                }
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

/**
 * Native Canvas Chart for Blood Pressure Trends.
 */
@Composable
private fun HealthMonitoringChart(
    systolicTrends: List<Float>,
    diastolicTrends: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val gridColor = Color(0xFFEFE8F4)
        val strokeWidth = 2.dp.toPx()

        val yMin = 60f
        val yMax = 120f

        // Y-axis grid lines
        val ySteps = listOf(120, 100, 80, 60)
        ySteps.forEachIndexed { i, valStep ->
            val yPos = (h / (ySteps.size - 1)) * i
            drawLine(
                color = gridColor,
                start = Offset(0f, yPos),
                end = Offset(w, yPos),
                strokeWidth = 1.dp.toPx()
            )
        }

        val stepX = w / (systolicTrends.size - 1)

        // Draw Systolic Line (Purple)
        val sysPath = Path()
        systolicTrends.forEachIndexed { index, sysVal ->
            val x = index * stepX
            val normY = (sysVal - yMin) / (yMax - yMin)
            val y = h - (normY * h)
            if (index == 0) sysPath.moveTo(x, y) else sysPath.lineTo(x, y)
        }
        drawPath(
            path = sysPath,
            color = Color(0xFF6E4F76),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw Diastolic Line (Mint Green)
        val diaPath = Path()
        diastolicTrends.forEachIndexed { index, diaVal ->
            val x = index * stepX
            val normY = (diaVal - yMin) / (yMax - yMin)
            val y = h - (normY * h)
            if (index == 0) diaPath.moveTo(x, y) else diaPath.lineTo(x, y)
        }
        drawPath(
            path = diaPath,
            color = Color(0xFF3D7B59),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
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

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun PatientHealthMonitoringScreenPreview() {
    PatientHealthMonitoringScreenContent()
}
