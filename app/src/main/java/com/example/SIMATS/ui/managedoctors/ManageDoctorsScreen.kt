package com.example.SIMATS.ui.managedoctors

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
import com.example.SIMATS.data.model.DoctorAccountItem
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun ManageDoctorsScreen(
    viewModel: ManageDoctorsViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onAddDoctorClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    ManageDoctorsScreenContent(
        modifier = modifier,
        uiState = uiState,
        onSearchQueryChanged = { q -> viewModel.onEvent(ManageDoctorsUiEvent.OnSearchQueryChanged(q)) },
        onFilterTabSelected = { index -> viewModel.onEvent(ManageDoctorsUiEvent.OnFilterTabSelected(index)) },
        onToggleDoctorStatus = { id -> viewModel.onEvent(ManageDoctorsUiEvent.OnToggleDoctorStatusClicked(id)) },
        onEditDetailsClick = { id -> viewModel.onEvent(ManageDoctorsUiEvent.OnEditDetailsClicked(id)) },
        onAddDoctorClick = {
            viewModel.onEvent(ManageDoctorsUiEvent.OnAddDoctorClicked)
            onAddDoctorClick()
        },
        onNextPageClick = { viewModel.onEvent(ManageDoctorsUiEvent.OnNextPageClicked) },
        onBackClick = {
            viewModel.onEvent(ManageDoctorsUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(ManageDoctorsUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(ManageDoctorsUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun ManageDoctorsScreenContent(
    modifier: Modifier = Modifier,
    uiState: ManageDoctorsUiState = ManageDoctorsUiState(
        doctors = listOf(
            DoctorAccountItem(
                id = "1",
                name = "Dr Amelia Chen",
                specialtyAndPatientsText = "Obstetrics · 18 assigned patients",
                emailAndLicenseText = "amelia.chen@willow.example · OBS-19382",
                isActive = true,
                isHighlighted = true
            ),
            DoctorAccountItem(
                id = "2",
                name = "Dr Daniel Okafor",
                specialtyAndPatientsText = "Obstetrics · 14 assigned patients",
                isActive = true
            ),
            DoctorAccountItem(
                id = "3",
                name = "Dr Priya Shah",
                specialtyAndPatientsText = "Maternal medicine · Access disabled",
                isActive = false
            )
        )
    ),
    onSearchQueryChanged: (String) -> Unit = {},
    onFilterTabSelected: (Int) -> Unit = {},
    onToggleDoctorStatus: (String) -> Unit = {},
    onEditDetailsClick: (String) -> Unit = {},
    onAddDoctorClick: () -> Unit = {},
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
                text = "Manage doctors",
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
                text = "Find a doctor",
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
                                text = "Search name, email or license",
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

            // Filter Tabs (All · 12, Active · 10, Inactive · 2)
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

            // Doctor List Cards
            val filteredDoctors = when (uiState.selectedFilterTab) {
                1 -> uiState.doctors.filter { it.isActive }
                2 -> uiState.doctors.filter { !it.isActive }
                else -> uiState.doctors
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredDoctors.forEach { doctor ->
                    DoctorCardItem(
                        item = doctor,
                        onEditClick = { onEditDetailsClick(doctor.id) },
                        onToggleStatusClick = { onToggleDoctorStatus(doctor.id) }
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
                    text = "Showing 1-3 of ${uiState.totalCount}",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Add Doctor Primary Button
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
                text = "Deactivation removes access, not clinical records. Reassign patients before confirming.",
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
 * Single Doctor Card Item.
 */
@Composable
private fun DoctorCardItem(
    item: DoctorAccountItem,
    onEditClick: () -> Unit,
    onToggleStatusClick: () -> Unit
) {
    val cardBg = if (item.isHighlighted) Color(0xFFF4ECF8) else Color.White

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
                        StethoscopeIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.name,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.specialtyAndPatientsText,
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

            if (!item.emailAndLicenseText.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.emailAndLicenseText,
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
                            text = "Edit details",
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
                            .clickable { onToggleStatusClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (item.isActive) "Deactivate" else "Activate",
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
fun ManageDoctorsScreenPreview() {
    ManageDoctorsScreenContent()
}
