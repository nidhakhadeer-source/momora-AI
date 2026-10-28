package com.example.SIMATS.ui.admindashboard

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
import com.example.SIMATS.data.model.AdminDashboardData
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraIconBtnBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel,
    modifier: Modifier = Modifier,
    onManageDoctorsClick: () -> Unit = {},
    onManagePatientsClick: () -> Unit = {},
    onAddDoctorClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    AdminDashboardScreenContent(
        modifier = modifier,
        uiState = uiState,
        onManageDoctorsClick = {
            viewModel.onEvent(AdminDashboardUiEvent.OnManageDoctorsClicked)
            onManageDoctorsClick()
        },
        onManagePatientsClick = {
            viewModel.onEvent(AdminDashboardUiEvent.OnManagePatientsClicked)
            onManagePatientsClick()
        },
        onAddDoctorClick = {
            viewModel.onEvent(AdminDashboardUiEvent.OnAddDoctorClicked)
            onAddDoctorClick()
        },
        onNotificationClick = { viewModel.onEvent(AdminDashboardUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(AdminDashboardUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun AdminDashboardScreenContent(
    modifier: Modifier = Modifier,
    uiState: AdminDashboardUiState = AdminDashboardUiState(
        dashboardData = AdminDashboardData(
            lastUpdatedText = "Administrator · Updated 27 Oct 2026, 09:30",
            totalPatientsCount = 128,
            activePatientsCount = 128,
            inactivePatientsCount = 0,
            totalDoctorsCount = 12,
            activeDoctorsCount = 10,
            inactiveDoctorsCount = 2,
            registrationTrendValues = listOf(10f, 18f, 22f, 38f),
            registrationMonths = listOf("Jul", "Aug", "Sep", "Oct")
        )
    ),
    onManageDoctorsClick: () -> Unit = {},
    onManagePatientsClick: () -> Unit = {},
    onAddDoctorClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
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

        // 2. Scrollable Body Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Care, well organized",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = data?.lastUpdatedText ?: "Administrator · Updated 27 Oct 2026, 09:30",
                fontSize = 12.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics Summary Grid (Total Patients & Total Doctors)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Patients Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MomoraPinkCircleBg)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Total patients",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MomoraBodyMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${data?.totalPatientsCount ?: 128}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${data?.activePatientsCount ?: 128} active · ${data?.inactivePatientsCount ?: 0} inactive",
                            fontSize = 10.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }

                // Total Doctors Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MomoraMintBadgeBg)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Total doctors",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MomoraMintBadgeText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${data?.totalDoctorsCount ?: 12}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${data?.activeDoctorsCount ?: 10} active · ${data?.inactiveDoctorsCount ?: 2} inactive",
                            fontSize = 10.5.sp,
                            color = Color(0xFF43524E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Patient Registrations Chart Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Patient registrations",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )

                        Text(
                            text = "Last 4 months",
                            fontSize = 11.5.sp,
                            color = MomoraBodyMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MomoraPurplePrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "New patients - accounts",
                            fontSize = 11.sp,
                            color = MomoraPurplePrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Native Canvas Registrations Chart Drawing
                    RegistrationsTrendChart(
                        values = data?.registrationTrendValues ?: listOf(10f, 18f, 22f, 38f),
                        months = data?.registrationMonths ?: listOf("Jul", "Aug", "Sep", "Oct"),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Monthly new accounts - Jul-Oct 2026",
                        fontSize = 11.sp,
                        color = MomoraBodyMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "Account management" Section
            Text(
                text = "Account management",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Management Links Container Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                // Manage doctors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onManageDoctorsClick() }
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
                            StethoscopeIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Manage doctors",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurpleText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${data?.totalDoctorsCount ?: 12} accounts · Edit, assign or deactivate",
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

                Spacer(modifier = Modifier.height(4.dp))

                // Manage patients
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onManagePatientsClick() }
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
                                .background(MomoraPinkCircleBg),
                            contentAlignment = Alignment.Center
                        ) {
                            PeopleNavIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Manage patients",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurpleText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${data?.totalPatientsCount ?: 128} accounts · Access and status",
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

            Spacer(modifier = Modifier.height(18.dp))

            // Add doctor Primary Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { onAddDoctorClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add doctor",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "👤+",
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Doctor accounts are created only by an administrator. No public doctor registration.",
                fontSize = 11.sp,
                color = MomoraFooterText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Admin Bottom Navigation Bar (4 Admin Tabs)
        AdminBottomNavBar(
            selectedIndex = uiState.selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

/**
 * Native Canvas Registration Trend Chart.
 */
@Composable
private fun RegistrationsTrendChart(
    values: List<Float>,
    months: List<String>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val gridColor = Color(0xFFEFE8F4)
        val lineStroke = 2.dp.toPx()

        val yMin = 0f
        val yMax = 40f

        // Y-axis grid lines (40, 30, 20, 10)
        val ySteps = listOf(40, 30, 20, 10)
        ySteps.forEachIndexed { i, _ ->
            val yPos = (h / ySteps.size) * (i + 1)
            drawLine(
                color = gridColor,
                start = Offset(0f, yPos),
                end = Offset(w, yPos),
                strokeWidth = 1.dp.toPx()
            )
        }

        val stepX = w / (values.size - 1)

        // Draw Growth Curve (Purple)
        val path = Path()
        values.forEachIndexed { index, valItem ->
            val x = index * stepX
            val normY = (valItem - yMin) / (yMax - yMin)
            val y = h - (normY * h)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = Color(0xFF6E4F76),
            style = Stroke(width = lineStroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * Admin Bottom Navigation Bar with 4 Tabs (Overview, Doctors, Patients, Settings).
 */
@Composable
private fun AdminBottomNavBar(
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
                label = "Overview",
                isSelected = selectedIndex == 0,
                onClick = { onTabSelected(0) },
                icon = { OverviewNavIcon(color = if (selectedIndex == 0) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Doctors",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { StethoscopeIcon(color = if (selectedIndex == 1) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Patients",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { PeopleNavIcon(color = if (selectedIndex == 2) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Settings",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { SettingsNavIcon(color = if (selectedIndex == 3) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
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
private fun StethoscopeIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val tubePath = Path().apply {
            moveTo(w * 0.30f, h * 0.20f)
            lineTo(w * 0.30f, h * 0.45f)
            cubicTo(w * 0.30f, h * 0.75f, w * 0.70f, h * 0.75f, w * 0.70f, h * 0.45f)
            lineTo(w * 0.70f, h * 0.20f)
        }
        drawPath(tubePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawCircle(color = color, radius = strokeWidth * 1.5f, center = Offset(w * 0.50f, h * 0.70f))
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

@Composable
private fun OverviewNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRect(color = color, topLeft = Offset(w * 0.15f, h * 0.15f), size = Size(w * 0.30f, h * 0.30f), style = Stroke(width = strokeWidth))
        drawRect(color = color, topLeft = Offset(w * 0.55f, h * 0.15f), size = androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.30f), style = Stroke(width = strokeWidth))
        drawRect(color = color, topLeft = Offset(w * 0.15f, h * 0.55f), size = androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.30f), style = Stroke(width = strokeWidth))
        drawRect(color = color, topLeft = Offset(w * 0.55f, h * 0.55f), size = androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.30f), style = Stroke(width = strokeWidth))
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
private fun SettingsNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(color = color, radius = w * 0.30f, center = Offset(w * 0.50f, h * 0.50f), style = Stroke(width = strokeWidth))
        drawCircle(color = color, radius = w * 0.12f, center = Offset(w * 0.50f, h * 0.50f), style = Stroke(width = strokeWidth))
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun AdminDashboardScreenPreview() {
    AdminDashboardScreenContent()
}
