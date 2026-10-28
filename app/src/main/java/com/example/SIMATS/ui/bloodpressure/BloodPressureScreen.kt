package com.example.SIMATS.ui.bloodpressure

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.BloodPressureData
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun BloodPressureScreen(
    viewModel: BloodPressureViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    BloodPressureScreenContent(
        modifier = modifier,
        uiState = uiState,
        onSystolicInputChanged = { sys -> viewModel.onEvent(BloodPressureUiEvent.OnSystolicInputChanged(sys)) },
        onDiastolicInputChanged = { dia -> viewModel.onEvent(BloodPressureUiEvent.OnDiastolicInputChanged(dia)) },
        onSaveReadingClick = { viewModel.onEvent(BloodPressureUiEvent.OnSaveReadingClicked) },
        onBackClick = {
            viewModel.onEvent(BloodPressureUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(BloodPressureUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(BloodPressureUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun BloodPressureScreenContent(
    modifier: Modifier = Modifier,
    uiState: BloodPressureUiState = BloodPressureUiState(
        data = BloodPressureData(
            currentReadingText = "112 / 72 mmHg",
            timestampText = "27 Oct 2026 · 08:15 · Pulse 78 bpm",
            yAxisLabels = listOf("120", "100", "80", "60"),
            xAxisLabels = listOf("21 Oct", "23 Oct", "25 Oct", "27 Oct"),
            systolicPointsNormalized = listOf(
                0.00f to 0.28f,
                0.33f to 0.35f,
                0.66f to 0.25f,
                1.00f to 0.28f
            ),
            diastolicPointsNormalized = listOf(
                0.00f to 0.78f,
                0.33f to 0.82f,
                0.66f to 0.75f,
                1.00f to 0.78f
            )
        )
    ),
    onSystolicInputChanged: (String) -> Unit = {},
    onDiastolicInputChanged: (String) -> Unit = {},
    onSaveReadingClick: () -> Unit = {},
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
                text = "Blood pressure",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Take a quiet moment before measuring.",
                fontSize = 13.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Current Reading Banner Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MomoraMintBadgeBg)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = data?.currentReadingText ?: "112 / 72 mmHg",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2C28)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = data?.timestampText ?: "27 Oct 2026 · 08:15 · Pulse 78 bpm",
                        fontSize = 12.sp,
                        color = Color(0xFF43524E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dual Line Chart Card (Last 7 days)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Last 7 days",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(MomoraPurplePrimary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Systolic - mmHg",
                                    fontSize = 10.5.sp,
                                    color = MomoraBodyMuted
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(MomoraMintBadgeText)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Diastolic",
                                    fontSize = 10.5.sp,
                                    color = MomoraBodyMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dual Line Chart
                    DualLineChart(
                        yAxisLabels = data?.yAxisLabels ?: listOf("120", "100", "80", "60"),
                        xAxisLabels = data?.xAxisLabels ?: listOf("21 Oct", "23 Oct", "25 Oct", "27 Oct"),
                        systolicPoints = data?.systolicPointsNormalized ?: listOf(
                            0.00f to 0.28f,
                            0.33f to 0.35f,
                            0.66f to 0.25f,
                            1.00f to 0.28f
                        ),
                        diastolicPoints = data?.diastolicPointsNormalized ?: listOf(
                            0.00f to 0.78f,
                            0.33f to 0.82f,
                            0.66f to 0.75f,
                            1.00f to 0.78f
                        ),
                        systolicColor = MomoraPurplePrimary,
                        diastolicColor = MomoraMintBadgeText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inputs Row (Systolic & Diastolic)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    FormFieldLabel(text = "Systolic (mmHg)")
                    CustomInputField(
                        value = uiState.systolicInput,
                        onValueChange = onSystolicInputChanged,
                        placeholder = "e.g. 112",
                        keyboardType = KeyboardType.Number
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    FormFieldLabel(text = "Diastolic (mmHg)")
                    CustomInputField(
                        value = uiState.diastolicInput,
                        onValueChange = onDiastolicInputChanged,
                        placeholder = "e.g. 72",
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sit quietly for 5 minutes, support your arm at heart level. Use a pregnancy-validated monitor.",
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Save blood pressure Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { if (!uiState.isLoading) onSaveReadingClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.isLoading) "Saving..." else "Save blood pressure",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Warning Notice Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFF4EC))
                    .border(1.dp, Color(0xFFFDE0CE), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 11.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "⚠️",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 1.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "A reading of 140/90 mmHg or above needs prompt maternity-team advice. Severe headache, vision changes, chest pain or breathlessness need urgent care.",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = Color(0xFF8D4213),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Bottom Navigation Bar
        DashboardBottomNavBar(
            selectedIndex = uiState.selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

/**
 * Dual Line Chart Drawing.
 */
@Composable
private fun DualLineChart(
    yAxisLabels: List<String>,
    xAxisLabels: List<String>,
    systolicPoints: List<Pair<Float, Float>>,
    diastolicPoints: List<Pair<Float, Float>>,
    systolicColor: Color,
    diastolicColor: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        // Y-Axis Labels
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(120.dp)
        ) {
            yAxisLabels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color(0xFFA098A6)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                val w = size.width
                val h = size.height
                val gridColor = Color(0xFFF0E8F4)

                // Grid Lines
                val count = yAxisLabels.size
                for (i in 0 until count) {
                    val y = h * (i.toFloat() / (count - 1).coerceAtLeast(1))
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw Systolic Line (Purple)
                drawCurveLine(systolicPoints, systolicColor, w, h)

                // Draw Diastolic Line (Green)
                drawCurveLine(diastolicPoints, diastolicColor, w, h)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // X-Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                xAxisLabels.forEach { label ->
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = Color(0xFFA098A6)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawCurveLine(
    points: List<Pair<Float, Float>>,
    color: Color,
    w: Float,
    h: Float
) {
    if (points.isEmpty()) return
    val path = Path()
    points.forEachIndexed { index, (ptX, ptY) ->
        val px = ptX * w
        val py = ptY * h
        if (index == 0) {
            path.moveTo(px, py)
        } else {
            val prevPx = points[index - 1].first * w
            val prevPy = points[index - 1].second * h
            val controlX1 = prevPx + (px - prevPx) / 2f
            val controlY1 = prevPy
            val controlX2 = prevPx + (px - prevPx) / 2f
            val controlY2 = py
            path.cubicTo(controlX1, controlY1, controlX2, controlY2, px, py)
        }
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

@Composable
private fun FormFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MomoraPurpleText
    )
    Spacer(modifier = Modifier.height(5.dp))
}

@Composable
private fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                fontSize = 13.5.sp,
                color = Color(0xFFA098A6)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = TextStyle(
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Normal,
                color = MomoraPurpleText
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Bottom Navigation Bar.
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
fun BloodPressureScreenPreview() {
    BloodPressureScreenContent()
}
