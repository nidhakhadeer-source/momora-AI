package com.example.SIMATS.ui.patientadvice

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.PatientAdviceItem
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.components.sanitizeDoctorText
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraLavenderCircleBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PatientDoctorAdviceScreen(
    viewModel: PatientDoctorAdviceViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 3,
    onNavigateBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler {
        onNavigateBack()
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(PatientDoctorAdviceUiEvent.OnScreenOpened)
    }

    PatientDoctorAdviceScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onBackClick = onNavigateBack,
        onNotificationClick = onNotificationClick,
        onRetryClick = { viewModel.onEvent(PatientDoctorAdviceUiEvent.OnRetryClicked) },
        onNavTabSelected = onNavTabSelected
    )
}

@Composable
fun PatientDoctorAdviceScreenContent(
    modifier: Modifier = Modifier,
    uiState: PatientDoctorAdviceUiState = PatientDoctorAdviceUiState(),
    selectedNavIndex: Int = 3,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()

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
                text = "Doctor Advice",
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
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                // Loading State
                uiState.isLoading && uiState.adviceList.isEmpty() -> {
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
                                text = "Loading doctor advice...",
                                fontSize = 14.sp,
                                color = MomoraBodyMuted
                            )
                        }
                    }
                }

                // Error State
                uiState.errorMessage != null && uiState.adviceList.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = formatErrorMessage(uiState.errorMessage),
                                fontSize = 14.sp,
                                color = Color(0xFFA94442),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(MomoraPurplePrimary)
                                    .clickable { onRetryClick() }
                                    .padding(horizontal = 24.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Retry",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Empty State
                uiState.adviceList.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MomoraLavenderCircleBg),
                                contentAlignment = Alignment.Center
                            ) {
                                AdviceDocIcon(
                                    color = MomoraPurplePrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "No doctor advice yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurpleDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "When your doctor sends you clinical guidance, care plans, or consultation recommendations, they will appear here.",
                                fontSize = 13.sp,
                                color = MomoraBodyMuted,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(MomoraPurplePrimary)
                                    .clickable { onRetryClick() }
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Refresh",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // List of Advice Items
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 24.dp)
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Doctor Advice",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Clinical notes, treatment plans, and direct guidance from your maternity doctor.",
                            fontSize = 13.sp,
                            color = MomoraBodyMuted,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            uiState.adviceList.forEach { advice ->
                                PatientAdviceCard(advice = advice)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        // 3. Bottom Navigation Bar
        PatientBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

@Composable
private fun PatientAdviceCard(advice: PatientAdviceItem) {
    val doctorName = if (advice.doctorName.isNotBlank()) advice.doctorName else "Doctor"
    val initials = extractInitials(doctorName)
    val formattedDate = formatAdviceDate(advice.createdAt)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            // Doctor header info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MomoraLavenderCircleBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurplePrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = doctorName.sanitizeDoctorText(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Text(
                            text = "Maternity Care Team",
                            fontSize = 11.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }

                if (formattedDate.isNotBlank()) {
                    Text(
                        text = formattedDate,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MomoraBodyMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subject
            if (advice.subject.isNotBlank()) {
                Text(
                    text = advice.subject.sanitizeDoctorText(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Message box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF9F6FC))
                    .padding(12.dp)
            ) {
                Text(
                    text = advice.adviceText.sanitizeDoctorText(),
                    fontSize = 13.5.sp,
                    color = MomoraPurpleDark,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MomoraMintBadgeBg)
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "✓ Clinical Guidance",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraMintBadgeText
                    )
                }
            }
        }
    }
}

private fun extractInitials(name: String): String {
    val clean = name.replace("Dr.", "").replace("Dr ", "").trim()
    val parts = clean.split(" ").filter { it.isNotBlank() }
    return if (parts.size >= 2) {
        "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
    } else if (clean.isNotEmpty()) {
        clean.take(2).uppercase()
    } else {
        "MD"
    }
}

private fun formatAdviceDate(dateStr: String): String {
    if (dateStr.isBlank()) return ""
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val date = parser.parse(dateStr) ?: return dateStr
        val formatter = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.US)
        formatter.format(date)
    } catch (_: Exception) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = parser.parse(dateStr) ?: return dateStr
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.US)
            formatter.format(date)
        } catch (_: Exception) {
            dateStr
        }
    }
}

@Composable
private fun PatientBottomNavBar(
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
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        icon()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
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
private fun AdviceDocIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.dp.toPx()

        val docPath = Path().apply {
            moveTo(w * 0.2f, h * 0.15f)
            lineTo(w * 0.65f, h * 0.15f)
            lineTo(w * 0.8f, h * 0.30f)
            lineTo(w * 0.8f, h * 0.85f)
            lineTo(w * 0.2f, h * 0.85f)
            close()
        }
        drawPath(docPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = color, start = Offset(w * 0.32f, h * 0.42f), end = Offset(w * 0.68f, h * 0.42f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.32f, h * 0.55f), end = Offset(w * 0.68f, h * 0.55f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.32f, h * 0.68f), end = Offset(w * 0.52f, h * 0.68f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun HomeNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.45f)
            lineTo(w * 0.5f, h * 0.18f)
            lineTo(w * 0.85f, h * 0.45f)
            lineTo(w * 0.85f, h * 0.85f)
            lineTo(w * 0.15f, h * 0.85f)
            close()
        }
        drawPath(path, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun TrackNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()
        drawCircle(color = color, radius = w * 0.35f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = stroke))
        drawLine(color = color, start = Offset(w * 0.5f, h * 0.3f), end = Offset(w * 0.5f, h * 0.5f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.5f, h * 0.5f), end = Offset(w * 0.68f, h * 0.5f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun SparklesIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()
        val p = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            cubicTo(w * 0.5f, h * 0.4f, w * 0.6f, h * 0.5f, w * 0.85f, h * 0.5f)
            cubicTo(w * 0.6f, h * 0.5f, w * 0.5f, h * 0.6f, w * 0.5f, h * 0.85f)
            cubicTo(w * 0.5f, h * 0.6f, w * 0.4f, h * 0.5f, w * 0.15f, h * 0.5f)
            cubicTo(w * 0.4f, h * 0.5f, w * 0.5f, h * 0.4f, w * 0.5f, h * 0.15f)
            close()
        }
        drawPath(p, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun CareNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()
        val heartPath = Path().apply {
            moveTo(w * 0.5f, h * 0.8f)
            cubicTo(w * 0.15f, h * 0.55f, w * 0.1f, h * 0.25f, w * 0.32f, h * 0.25f)
            cubicTo(w * 0.44f, h * 0.25f, w * 0.5f, h * 0.38f, w * 0.5f, h * 0.38f)
            cubicTo(w * 0.5f, h * 0.38f, w * 0.56f, h * 0.25f, w * 0.68f, h * 0.25f)
            cubicTo(w * 0.9f, h * 0.25f, w * 0.85f, h * 0.55f, w * 0.5f, h * 0.8f)
            close()
        }
        drawPath(heartPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun UserNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()
        drawCircle(color = color, radius = w * 0.22f, center = Offset(w * 0.5f, h * 0.35f), style = Stroke(width = stroke))
        val bodyPath = Path().apply {
            moveTo(w * 0.2f, h * 0.85f)
            cubicTo(w * 0.25f, h * 0.65f, w * 0.4f, h * 0.60f, w * 0.5f, h * 0.60f)
            cubicTo(w * 0.6f, h * 0.60f, w * 0.75f, h * 0.65f, w * 0.8f, h * 0.85f)
        }
        drawPath(bodyPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
