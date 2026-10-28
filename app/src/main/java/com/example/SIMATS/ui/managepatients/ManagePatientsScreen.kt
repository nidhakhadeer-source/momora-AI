package com.example.SIMATS.ui.managepatients

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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.PatientAccountItem
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun ManagePatientsScreen(
    viewModel: ManagePatientsViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    ManagePatientsScreenContent(
        modifier = modifier,
        uiState = uiState,
        onSearchQueryChanged = { q -> viewModel.onEvent(ManagePatientsUiEvent.OnSearchQueryChanged(q)) },
        onFilterTabSelected = { index -> viewModel.onEvent(ManagePatientsUiEvent.OnFilterTabSelected(index)) },
        onEditAccountClick = { id -> viewModel.onEvent(ManagePatientsUiEvent.OnEditAccountClicked(id)) },
        onManageAccessClick = { id -> viewModel.onEvent(ManagePatientsUiEvent.OnManageAccessClicked(id)) },
        onNextPageClick = { viewModel.onEvent(ManagePatientsUiEvent.OnNextPageClicked) },
        onBackClick = {
            viewModel.onEvent(ManagePatientsUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(ManagePatientsUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(ManagePatientsUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun ManagePatientsScreenContent(
    modifier: Modifier = Modifier,
    uiState: ManagePatientsUiState = ManagePatientsUiState(
        patients = listOf(
            PatientAccountItem(
                id = "1",
                name = "Maya Patel",
                patientIdText = "P-1042",
                assignedDoctorText = "Assigned doctor: Dr Amelia Chen",
                emailText = "maya.patel@example.com",
                isActive = true,
                isHighlighted = true
            ),
            PatientAccountItem(
                id = "2",
                name = "Laila Ahmed",
                patientIdText = "P-1030",
                assignedDoctorText = "Assigned doctor: Dr Amelia Chen",
                isActive = true
            ),
            PatientAccountItem(
                id = "3",
                name = "Emma Wilson",
                patientIdText = "P-1093",
                assignedDoctorText = "Assigned doctor: Dr Amelia Chen",
                isActive = true
            ),
            PatientAccountItem(
                id = "4",
                name = "Isla Martin",
                patientIdText = "P-0986",
                assignedDoctorText = "Account access disabled",
                isActive = false
            )
        )
    ),
    onSearchQueryChanged: (String) -> Unit = {},
    onFilterTabSelected: (Int) -> Unit = {},
    onEditAccountClick: (String) -> Unit = {},
    onManageAccessClick: (String) -> Unit = {},
    onNextPageClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()

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
                text = "Manage patients",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${uiState.totalCount} total · ${uiState.activeCount} active · ${uiState.inactiveCount} inactive",
                fontSize = 13.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            Text(
                text = "Find a patient",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleText
            )

            Spacer(modifier = Modifier.height(6.dp))

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.searchQuery.isEmpty()) {
                            Text(
                                text = "Search name or patient ID",
                                fontSize = 13.sp,
                                color = Color(0xFFA098A6)
                            )
                        }
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChanged,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                color = MomoraPurpleText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    SearchIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs (All · 128, Active · 120, Inactive · 8)
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
                    val tabs = listOf(
                        "All · ${uiState.totalCount}",
                        "Active · ${uiState.activeCount}",
                        "Inactive · ${uiState.inactiveCount}"
                    )
                    tabs.forEachIndexed { index, label ->
                        val isSelected = uiState.selectedFilterTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { onFilterTabSelected(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MomoraPurpleDark else MomoraBodyMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Patient Account List Cards
            val filteredPatients = when (uiState.selectedFilterTab) {
                1 -> uiState.patients.filter { it.isActive }
                2 -> uiState.patients.filter { !it.isActive }
                else -> uiState.patients
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredPatients.forEach { patient ->
                    PatientAccountCardItem(
                        item = patient,
                        onEditClick = { onEditAccountClick(patient.id) },
                        onManageAccessClick = { onManageAccessClick(patient.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pagination Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing 1-4 of ${uiState.totalCount}",
                    fontSize = 11.5.sp,
                    color = MomoraBodyMuted
                )

                Text(
                    text = "Next ➔",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurplePrimary,
                    modifier = Modifier.clickable { onNextPageClick() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Admin Access Restriction Disclaimer Card
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
                        text = "Admin access is for account management. Clinical notes and health readings are restricted to authorized care teams.",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Admin Bottom Navigation Bar
        AdminBottomNavBar(
            selectedIndex = uiState.selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

/**
 * Single Patient Account Card Item.
 */
@Composable
private fun PatientAccountCardItem(
    item: PatientAccountItem,
    onEditClick: () -> Unit,
    onManageAccessClick: () -> Unit
) {
    val cardBg = if (item.isHighlighted) MomoraPinkCircleBg else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(
                1.dp,
                if (item.isHighlighted) Color.Transparent else Color(0xFFEFE8F4),
                RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val avatarBg = if (item.isActive) MomoraMintBadgeBg else MomoraPinkCircleBg

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(avatarBg),
                        contentAlignment = Alignment.Center
                    ) {
                        UserNavIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "${item.name} · ${item.patientIdText}",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.assignedDoctorText,
                            fontSize = 11.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (item.isActive) MomoraMintBadgeBg else MomoraPinkCircleBg)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (item.isActive) "Active" else "Inactive",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isActive) MomoraMintBadgeText else MomoraPurplePrimary
                    )
                }
            }

            if (!item.emailText.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.emailText,
                    fontSize = 11.sp,
                    color = MomoraBodyMuted
                )
            }

            if (item.isHighlighted) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(50))
                            .clickable { onEditClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Edit account",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(50))
                            .clickable { onManageAccessClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Manage access",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                    }
                }
            }
        }
    }
}

/**
 * Admin Bottom Navigation Bar.
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
private fun SearchIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.28f,
            center = Offset(w * 0.40f, h * 0.40f),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = color,
            start = Offset(w * 0.60f, h * 0.60f),
            end = Offset(w * 0.85f, h * 0.85f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
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
fun ManagePatientsScreenPreview() {
    ManagePatientsScreenContent()
}
