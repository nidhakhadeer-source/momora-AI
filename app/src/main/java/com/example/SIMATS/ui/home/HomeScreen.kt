package com.example.SIMATS.ui.home

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
import com.example.SIMATS.data.model.DailyCareItem
import com.example.SIMATS.data.model.PatientDashboardData
import com.example.SIMATS.ui.theme.MomoraBodyMuted
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
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        modifier = modifier,
        uiState = uiState,
        onNavTabSelected = { index -> viewModel.onEvent(HomeUiEvent.OnNavTabSelected(index)) },
        onNotificationClick = { viewModel.onEvent(HomeUiEvent.OnNotificationClicked) },
        onPregnancyDetailsClick = { viewModel.onEvent(HomeUiEvent.OnPregnancyDetailsClicked) },
        onBloodPressureClick = { viewModel.onEvent(HomeUiEvent.OnBloodPressureCardClicked) },
        onWeightClick = { viewModel.onEvent(HomeUiEvent.OnWeightCardClicked) },
        onAskAiClick = { viewModel.onEvent(HomeUiEvent.OnAskAiClicked) },
        onBabyGrowthClick = { viewModel.onEvent(HomeUiEvent.OnBabyGrowthClicked) },
        onNutritionClick = { viewModel.onEvent(HomeUiEvent.OnNutritionClicked) },
        onViewAllDailyCareClick = { viewModel.onEvent(HomeUiEvent.OnViewAllDailyCareClicked) }
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(
        data = PatientDashboardData(
            patientName = "Maya",
            dateText = "Tuesday, 27 Oct 2026 · A little care, every day.",
            trimesterText = "SECOND TRIMESTER",
            gestationalAgeText = "24 weeks, 3 days",
            dueDateText = "Due 13 Feb 2027 · Pregnancy details ➔",
            progressPercent = 0.60f,
            bloodPressureValue = "112/72",
            bloodPressureTime = "mmHg · Today, 08:15",
            weightValue = "64.2",
            weightTime = "kg · Today, 08:10",
            dailyCareItems = listOf(
                DailyCareItem(
                    id = 1,
                    title = "Prenatal vitamin",
                    subtitle = "1 tablet · Today at 08:00",
                    isTaken = true,
                    isAppointment = false
                ),
                DailyCareItem(
                    id = 2,
                    title = "Dr Amelia Chen",
                    subtitle = "29 Oct 2026 · 10:30 · In-person",
                    isTaken = false,
                    isAppointment = true
                )
            )
        )
    ),
    onNavTabSelected: (Int) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onPregnancyDetailsClick: () -> Unit = {},
    onBloodPressureClick: () -> Unit = {},
    onWeightClick: () -> Unit = {},
    onAskAiClick: () -> Unit = {},
    onBabyGrowthClick: () -> Unit = {},
    onNutritionClick: () -> Unit = {},
    onViewAllDailyCareClick: () -> Unit = {}
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

        // 2. Scrollable Dashboard Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            // Greeting Header
            Text(
                text = "Good morning, ${data?.patientName ?: "Maya"}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = data?.dateText ?: "Tuesday, 27 Oct 2026 · A little care, every day.",
                fontSize = 12.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pregnancy Progress Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MomoraPinkCircleBg)
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = data?.trimesterText ?: "SECOND TRIMESTER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurplePrimary,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = data?.gestationalAgeText ?: "24 weeks, 3 days",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurpleDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = data?.dueDateText ?: "Due 13 Feb 2027 · Pregnancy details ➔",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MomoraPurplePrimary,
                                modifier = Modifier.clickable { onPregnancyDetailsClick() }
                            )
                        }

                        // Circular Baby Badge Graphic
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFF0E4EC), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            BabyFaceIcon(
                                color = MomoraPurplePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Track Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFEFE8F4))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(data?.progressPercent ?: 0.60f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MomoraPurplePrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Vitals Grid (Blood Pressure & Weight)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Blood Pressure Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MomoraMintBadgeBg)
                        .clickable { onBloodPressureClick() }
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Blood pressure",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF43524E)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data?.bloodPressureValue ?: "112/72",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2C28)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = data?.bloodPressureTime ?: "mmHg · Today, 08:15",
                            fontSize = 10.5.sp,
                            color = Color(0xFF7A8984)
                        )
                    }
                }

                // Weight Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MomoraLavenderCircleBg)
                        .clickable { onWeightClick() }
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
                            text = data?.weightValue ?: "64.2",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = data?.weightTime ?: "kg · Today, 08:10",
                            fontSize = 10.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "Your daily care" Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your daily care",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )

                Text(
                    text = "View all",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MomoraPurplePrimary,
                    modifier = Modifier.clickable { onViewAllDailyCareClick() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Daily Care Items Container
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                data?.dailyCareItems?.forEach { item ->
                    DailyCareCardItem(item = item)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "Explore your care" Section
            Text(
                text = "Explore your care",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ExploreCard(
                    title = "Ask AI",
                    icon = { SparklesIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp)) },
                    onClick = onAskAiClick,
                    modifier = Modifier.weight(1f)
                )
                ExploreCard(
                    title = "Baby growth",
                    icon = { BabyFaceIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp)) },
                    onClick = onBabyGrowthClick,
                    modifier = Modifier.weight(1f)
                )
                ExploreCard(
                    title = "Nutrition",
                    icon = { NutritionIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp)) },
                    onClick = onNutritionClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Health monitoring · Reports · Notifications ➔",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = MomoraPurplePrimary,
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

/**
 * Daily Care Row Item Card.
 */
@Composable
private fun DailyCareCardItem(
    item: DailyCareItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (item.isTaken) MomoraMintBadgeBg else Color(0xFFEDE4F0)),
                contentAlignment = Alignment.Center
            ) {
                if (item.isTaken) {
                    PillCapsuleIcon(color = MomoraMintBadgeText, modifier = Modifier.size(18.dp))
                } else {
                    CalendarIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = item.title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
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
        } else {
            Text(
                text = "›",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8C7F92)
            )
        }
    }
}

/**
 * Explore Care Action Card.
 */
@Composable
private fun ExploreCard(
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF4ECF8))
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )
        }
    }
}

/**
 * Bottom Navigation Bar for Patient Dashboard.
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

        // Home Bar Indicator Line
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

        drawPath(
            path = pulsePath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
private fun BabyFaceIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(color = color, radius = size.minDimension * 0.40f, style = Stroke(width = strokeWidth))
        drawCircle(color = color, radius = strokeWidth * 0.8f, center = Offset(w * 0.38f, h * 0.42f))
        drawCircle(color = color, radius = strokeWidth * 0.8f, center = Offset(w * 0.62f, h * 0.42f))

        val smilePath = Path().apply {
            moveTo(w * 0.38f, h * 0.60f)
            cubicTo(w * 0.45f, h * 0.72f, w * 0.55f, h * 0.72f, w * 0.62f, h * 0.60f)
        }
        drawPath(smilePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
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
private fun CalendarIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRect(color = color, style = Stroke(width = strokeWidth))
        drawLine(color = color, start = Offset(0f, h * 0.35f), end = Offset(w, h * 0.35f), strokeWidth = strokeWidth)
    }
}

@Composable
private fun NutritionIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawLine(color = color, start = Offset(w * 0.35f, h * 0.20f), end = Offset(w * 0.35f, h * 0.80f), strokeWidth = strokeWidth)
        drawLine(color = color, start = Offset(w * 0.65f, h * 0.20f), end = Offset(w * 0.65f, h * 0.80f), strokeWidth = strokeWidth)
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
    TopBarHeartIcon(color = color, modifier = modifier)
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
fun HomeScreenPreview() {
    HomeScreenContent()
}
