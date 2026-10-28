package com.example.SIMATS.ui.healthreport

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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
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
import com.example.SIMATS.data.model.HealthMonitoringData
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraLavenderCircleBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun HealthReportScreen(
    viewModel: HealthReportViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    HealthReportScreenContent(
        modifier = modifier,
        uiState = uiState,
        onAddReadingClick = { viewModel.onEvent(HealthReportUiEvent.OnAddReadingClicked) },
        onWeightTrackerClick = { viewModel.onEvent(HealthReportUiEvent.OnWeightTrackerClicked) },
        onBloodPressureTrackerClick = { viewModel.onEvent(HealthReportUiEvent.OnBloodPressureTrackerClicked) },
        onSymptomCheckerClick = { viewModel.onEvent(HealthReportUiEvent.OnSymptomCheckerClicked) },
        onBackClick = {
            viewModel.onEvent(HealthReportUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(HealthReportUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(HealthReportUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun HealthReportScreenContent(
    modifier: Modifier = Modifier,
    uiState: HealthReportUiState = HealthReportUiState(
        healthData = HealthMonitoringData(
            lastRecordedText = "27 Oct 2026 · Last recorded at 08:15",
            bloodPressure = "112/72",
            bloodPressureUnit = "mmHg",
            heartRate = "78",
            heartRateUnit = "beats per minute",
            weight = "64.2",
            weightDelta = "kg · +0.2 kg from last week",
            temperature = "36.7",
            temperatureUnit = "°C · Oral",
            doctorName = "Dr Amelia Chen"
        )
    ),
    onAddReadingClick: () -> Unit = {},
    onWeightTrackerClick: () -> Unit = {},
    onBloodPressureTrackerClick: () -> Unit = {},
    onSymptomCheckerClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.healthData

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
                text = "Your health, at a glance",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = data?.lastRecordedText ?: "27 Oct 2026 · Last recorded at 08:15",
                fontSize = 12.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 Vitals Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Row 1: Blood pressure & Heart rate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VitalCard(
                        title = "Blood pressure",
                        value = data?.bloodPressure ?: "112/72",
                        unit = data?.bloodPressureUnit ?: "mmHg",
                        titleColor = Color(0xFF43524E),
                        valueColor = Color(0xFF1E2C28),
                        unitColor = Color(0xFF7A8984),
                        bgColor = MomoraMintBadgeBg,
                        modifier = Modifier.weight(1f)
                    )

                    VitalCard(
                        title = "Heart rate",
                        value = data?.heartRate ?: "78",
                        unit = data?.heartRateUnit ?: "beats per minute",
                        titleColor = MomoraBodyMuted,
                        valueColor = MomoraPurpleDark,
                        unitColor = MomoraBodyMuted,
                        bgColor = MomoraPinkCircleBg,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Weight & Temperature
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VitalCard(
                        title = "Weight",
                        value = data?.weight ?: "64.2",
                        unit = data?.weightDelta ?: "kg · +0.2 kg from last week",
                        titleColor = MomoraPurplePrimary,
                        valueColor = MomoraPurpleDark,
                        unitColor = MomoraBodyMuted,
                        bgColor = MomoraLavenderCircleBg,
                        modifier = Modifier.weight(1f)
                    )

                    VitalCard(
                        title = "Temperature",
                        value = data?.temperature ?: "36.7",
                        unit = data?.temperatureUnit ?: "°C · Oral",
                        titleColor = Color(0xFF43524E),
                        valueColor = Color(0xFF1E2C28),
                        unitColor = Color(0xFF7A8984),
                        bgColor = MomoraMintBadgeBg,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // "Keep track" Section
            Text(
                text = "Keep track",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                // Item 1: Weight tracker
                TrackerRow(
                    title = "Weight tracker",
                    subtitle = "Review your pregnancy weight trend",
                    icon = { WeightScaleIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onWeightTrackerClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Item 2: Blood pressure tracker
                TrackerRow(
                    title = "Blood pressure tracker",
                    subtitle = "Readings, notes and measurement tips",
                    icon = { PulseIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onBloodPressureTrackerClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Item 3: Symptom checker
                TrackerRow(
                    title = "Symptom checker",
                    subtitle = "Log how you feel and get AI education",
                    icon = { ClipboardIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onSymptomCheckerClick
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Medical Disclaimer Card
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
                        text = "Home readings are not a diagnosis. Share changes or concerns with ${data?.doctorName ?: "Dr Amelia Chen"}.",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Add a health reading Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { onAddReadingClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add a health reading",
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Shared with your doctor under your explicit consent.",
                fontSize = 11.sp,
                color = MomoraFooterText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
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

@Composable
private fun VitalCard(
    title: String,
    value: String,
    unit: String,
    titleColor: Color,
    valueColor: Color,
    unitColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = unit,
                fontSize = 10.5.sp,
                color = unitColor
            )
        }
    }
}

@Composable
private fun TrackerRow(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
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
                icon()
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = MomoraBodyMuted
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "›",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF8C7F92)
        )
    }
}

/**
 * Bottom Navigation Bar for Health Report Screen.
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

/**
 * Custom Canvas vector drawings.
 */
@Composable
private fun WeightScaleIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val rectPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(w * 0.15f, h * 0.20f, w * 0.85f, h * 0.80f),
                    cornerRadius = CornerRadius(w * 0.10f, h * 0.10f)
                )
            )
        }
        drawPath(rectPath, color = color, style = Stroke(width = strokeWidth))

        drawCircle(color = color, radius = w * 0.15f, center = Offset(w * 0.50f, h * 0.40f), style = Stroke(width = strokeWidth))
        drawLine(color = color, start = Offset(w * 0.50f, h * 0.40f), end = Offset(w * 0.55f, h * 0.32f), strokeWidth = strokeWidth)
    }
}

@Composable
private fun PulseIcon(color: Color, modifier: Modifier = Modifier) {
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
private fun ClipboardIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val boardPath = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    rect = androidx.compose.ui.geometry.Rect(w * 0.20f, h * 0.25f, w * 0.80f, h * 0.85f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.08f, h * 0.08f)
                )
            )
        }
        drawPath(boardPath, color = color, style = Stroke(width = strokeWidth))

        // Top clip
        drawRect(
            color = color,
            topLeft = Offset(w * 0.38f, h * 0.18f),
            size = Size(w * 0.24f, h * 0.12f),
            style = Stroke(width = strokeWidth)
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
private fun TrackNavIcon(color: Color, modifier: Modifier = Modifier) {
    PulseIcon(color = color, modifier = modifier)
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
fun HealthReportScreenPreview() {
    HealthReportScreenContent()
}
