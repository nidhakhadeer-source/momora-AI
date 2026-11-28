package com.example.SIMATS.ui.patienthistory

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.SIMATS.data.model.DoctorPatientSummaryData
import com.example.SIMATS.data.model.DoctorPatientSummarySections
import com.example.SIMATS.data.model.PatientMedicalHistoryData
import com.example.SIMATS.data.model.PatientMedicationItem
import com.example.SIMATS.data.model.PatientReportItem
import com.example.SIMATS.data.model.PatientVisitItem
import com.example.SIMATS.navigation.SelectedPatientHolder
import com.example.SIMATS.ui.components.formatDoctorTime
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.components.sanitizeDoctorText
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraIconBtnBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.ThemeManager

@Composable
fun PatientHistoryScreen(
    viewModel: PatientHistoryViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 1,
    onNavigateBack: () -> Unit = {},
    onOpenHealthMonitoringClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (SelectedPatientHolder.initialTab != 0) {
            viewModel.onEvent(PatientHistoryUiEvent.OnTabSelected(SelectedPatientHolder.initialTab))
            SelectedPatientHolder.initialTab = 0
        }
    }

    PatientHistoryScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onTabSelected = { index -> viewModel.onEvent(PatientHistoryUiEvent.OnTabSelected(index)) },
        onMedicationClick = { medication -> viewModel.onEvent(PatientHistoryUiEvent.OnMedicationClicked(medication)) },
        onDismissMedicationSheet = { viewModel.onEvent(PatientHistoryUiEvent.OnDismissMedicationSheet) },
        onReportClick = { report -> viewModel.onEvent(PatientHistoryUiEvent.OnReportClicked(report, context)) },
        onViewAllReportsClick = { viewModel.onEvent(PatientHistoryUiEvent.OnViewAllReportsClicked) },
        onOpenHealthMonitoringClick = {
            viewModel.onEvent(PatientHistoryUiEvent.OnOpenHealthMonitoringClicked)
            onOpenHealthMonitoringClick()
        },
        onToggleHealthMonitoringExpand = {
            viewModel.onEvent(PatientHistoryUiEvent.OnToggleHealthMonitoringExpand)
        },
        onGenerateSummaryClick = { viewModel.onEvent(PatientHistoryUiEvent.OnGenerateSummaryClicked) },
        onGenerateAgainClick = { viewModel.onEvent(PatientHistoryUiEvent.OnGenerateAgainClicked) },
        onRetryClick = { viewModel.onEvent(PatientHistoryUiEvent.OnRetryClicked) },
        onBackClick = {
            viewModel.onEvent(PatientHistoryUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = {
            viewModel.onEvent(PatientHistoryUiEvent.OnNotificationClicked)
            onNotificationClick()
        },
        onNavTabSelected = { index ->
            viewModel.onEvent(PatientHistoryUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun PatientHistoryScreenContent(
    modifier: Modifier = Modifier,
    uiState: PatientHistoryUiState = PatientHistoryUiState(
        historyData = PatientMedicalHistoryData(
            patientName = "Maya Patel",
            patientInitials = "MP",
            gestationalAge = "24w 3d",
            dueDateText = "Due 13 Feb 2027",
            patientIdText = "P-13042",
            clinicalBackgroundText = "Age 29 · O positive · First pregnancy (G1 P0). No known drug allergies or chronic conditions reported.",
            patientConfirmedDateText = "Patient confirmed · 05 Oct 2026",
            medications = listOf(
                PatientMedicationItem(
                    id = "1",
                    name = "Prenatal vitamin",
                    dosageDetails = "1 tablet daily · 08:00 · Taken today",
                    isTakenToday = true
                ),
                PatientMedicationItem(
                    id = "2",
                    name = "Vitamin D · 400 IU",
                    dosageDetails = "1 tablet daily · 20:00",
                    isTakenToday = false
                )
            ),
            recentCareRecordTitle = "Complete blood count",
            recentCareRecordDetails = "28 Oct 2026 · Reviewed 27 Oct 2026"
        )
    ),
    onTabSelected: (Int) -> Unit = {},
    onMedicationClick: (PatientMedicationItem) -> Unit = {},
    onDismissMedicationSheet: () -> Unit = {},
    onReportClick: (PatientReportItem) -> Unit = {},
    onViewAllReportsClick: () -> Unit = {},
    onOpenHealthMonitoringClick: () -> Unit = {},
    onToggleHealthMonitoringExpand: () -> Unit = {},
    onGenerateSummaryClick: () -> Unit = {},
    onGenerateAgainClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    selectedNavIndex: Int = 1,
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.historyData
    val resolvedPatientName = data?.patientName?.takeIf { it.isNotBlank() }
        ?: uiState.patientName?.takeIf { it.isNotBlank() }
        ?: SelectedPatientHolder.selectedPatientName?.takeIf { it.isNotBlank() }
        ?: "Patient"

    val resolvedInitials = data?.patientInitials?.takeIf { it.isNotBlank() }
        ?: run {
            val words = resolvedPatientName.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
            if (words.size >= 2) {
                "${words[0].first().uppercaseChar()}${words[1].first().uppercaseChar()}"
            } else if (resolvedPatientName.isNotEmpty()) {
                resolvedPatientName.take(2).uppercase()
            } else {
                "PT"
            }
        }

    val isDark = ThemeManager.isDarkTheme()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF141218) else MomoraScreenBackground)
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
                    .background(if (isDark) Color(0xFF28212F) else MomoraIconBtnBg)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleText
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                BellIcon(
                    color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary,
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
            if (uiState.isLoading && data == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MomoraPurplePrimary)
                }
            } else if (uiState.errorMessage != null && data == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF2C1E22) else Color.White)
                        .border(1.dp, if (isDark) Color(0xFF4C272C) else Color(0xFFF2DEDE), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatErrorMessage(uiState.errorMessage ?: "Failed to load patient history"),
                            fontSize = 14.sp,
                            color = if (isDark) Color(0xFFFF9B9B) else Color(0xFFA94442),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MomoraPurplePrimary)
                                .clickable { onRetryClick() }
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Retry",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Medical history",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Patient-reported details & clinical recents",
                fontSize = 13.sp,
                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Patient Summary Banner Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDark) Color(0xFF1F1A24) else Color(0xFFF4ECF8))
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color.Transparent, RoundedCornerShape(18.dp))
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
                                .background(if (isDark) Color(0xFF2A2230) else Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = resolvedInitials,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = resolvedPatientName.sanitizeDoctorText(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${(data?.gestationalAge ?: "24w 3d").sanitizeDoctorText()} · ${(data?.dueDateText ?: "Due 13 Feb 2027").sanitizeDoctorText()}",
                                fontSize = 11.5.sp,
                                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                            )
                        }
                    }

                    Text(
                        text = (data?.patientIdText ?: "P-13042").sanitizeDoctorText(),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Tabs (Summary, Reports · 3, Visits)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF2A2230) else Color(0xFFF4ECF8))
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf("Summary", "Reports · ${uiState.reportsCount}", "Visits", "AI Summary")
                    tabs.forEachIndexed { index, label ->
                        val isSelected = uiState.selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) (if (isDark) Color(0xFF4A2F56) else Color.White) else Color.Transparent)
                                .clickable { onTabSelected(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) (if (isDark) Color.White else MomoraPurpleDark) else (if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState.selectedTab) {
                0 -> SummaryTabContent(
                    uiState = uiState,
                    isDark = isDark,
                    onMedicationClick = onMedicationClick,
                    onReportClick = onReportClick,
                    onOpenHealthMonitoringClick = onOpenHealthMonitoringClick,
                    onToggleHealthMonitoringExpand = onToggleHealthMonitoringExpand
                )
                1 -> ReportsTabContent(
                    uiState = uiState,
                    isDark = isDark,
                    onReportClick = onReportClick
                )
                2 -> VisitsTabContent(
                    uiState = uiState,
                    isDark = isDark
                )
                3 -> AiSummaryTabContent(
                    uiState = uiState,
                    isDark = isDark,
                    resolvedPatientName = resolvedPatientName,
                    onGenerateSummaryClick = onGenerateSummaryClick,
                    onGenerateAgainClick = onGenerateAgainClick
                )
            }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Doctor Bottom Navigation Bar

        DoctorBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }

    if (uiState.isMedicationSheetOpen && uiState.selectedMedication != null) {
        MedicationDetailsBottomSheet(
            medication = uiState.selectedMedication,
            onDismiss = onDismissMedicationSheet
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicationDetailsBottomSheet(
    medication: PatientMedicationItem,
    onDismiss: () -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) Color(0xFF1F1A24) else Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Medication details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 14.sp,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFFBF8FC))
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = medication.name.sanitizeDoctorText(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleText,
                            modifier = Modifier.weight(1f)
                        )
                        val bg = if (medication.isTakenToday) (if (isDark) Color(0xFF1D3328) else MomoraMintBadgeBg) else (if (isDark) Color(0xFF2E2435) else Color(0xFFF0EBF4))
                        val textColor = if (medication.isTakenToday) (if (isDark) Color(0xFF85E3B3) else MomoraMintBadgeText) else (if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary)
                        val statusText = if (medication.isTakenToday) "Taken today ✓" else "Pending"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(bg)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Dosage & schedule",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = medication.dosageDetails.sanitizeDoctorText(),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Clinical instructions",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Take daily as prescribed with meals. Patient should report any nausea or skipped doses during prenatal consultations.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = if (isDark) Color(0xFFEDE7EF) else Color(0xFF4A3F50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Close",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MedicationRow(
    item: PatientMedicationItem,
    onClick: () -> Unit,
    isDark: Boolean = ThemeManager.isDarkTheme()
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
            val bg = if (item.isTakenToday) (if (isDark) Color(0xFF1D3328) else MomoraMintBadgeBg) else (if (isDark) Color(0xFF2E2435) else Color(0xFFEDE4F0))
            val iconColor = if (item.isTakenToday) (if (isDark) Color(0xFF85E3B3) else MomoraMintBadgeText) else (if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary)

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                PillCapsuleIcon(color = iconColor, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = item.name.sanitizeDoctorText(),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.dosageDetails.sanitizeDoctorText(),
                    fontSize = 11.5.sp,
                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                )
            }
        }

        Text(
            text = "›",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) Color(0xFFB5ACB9) else Color(0xFF8C7F92)
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
    val isDark = ThemeManager.isDarkTheme()
    val navBg = if (isDark) Color(0xFF1E1A22) else Color.White
    val navBorder = if (isDark) Color(0xFF2E2633) else Color(0xFFF0E8F4)
    val activePillBg = if (isDark) Color(0xFF352B3C) else Color(0xFFF4ECF8)
    val activeColor = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
    val inactiveColor = if (isDark) Color(0xFF8D8392) else MomoraBodyMuted

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(navBg)
            .border(1.dp, navBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
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
                icon = { HomeNavIcon(color = if (selectedIndex == 0) activeColor else inactiveColor, modifier = Modifier.size(20.dp)) },
                activePillBg = activePillBg,
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
            NavTabItem(
                label = "Patients",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { PeopleNavIcon(color = if (selectedIndex == 1) activeColor else inactiveColor, modifier = Modifier.size(20.dp)) },
                activePillBg = activePillBg,
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
            NavTabItem(
                label = "Schedule",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { CalendarNavIcon(color = if (selectedIndex == 2) activeColor else inactiveColor, modifier = Modifier.size(20.dp)) },
                activePillBg = activePillBg,
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
            NavTabItem(
                label = "Advice",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { ChatNavIcon(color = if (selectedIndex == 3) activeColor else inactiveColor, modifier = Modifier.size(20.dp)) },
                activePillBg = activePillBg,
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(134.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(if (isDark) Color(0xFF4A4250) else Color(0xFF333333))
        )
    }
}

@Composable
private fun NavTabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    activePillBg: Color = Color(0xFFF4ECF8),
    activeColor: Color = MomoraPurplePrimary,
    inactiveColor: Color = MomoraBodyMuted
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
                .background(if (isSelected) activePillBg else Color.Transparent)
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
            color = if (isSelected) activeColor else inactiveColor
        )
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
private fun DoctorSummarySectionCard(
    icon: String,
    title: String,
    content: String,
    isDark: Boolean,
    isGapSection: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isGapSection) {
                    if (isDark) Color(0xFF2D241C) else Color(0xFFFFF9F0)
                } else {
                    if (isDark) Color(0xFF1F1A24) else Color.White
                }
            )
            .border(
                1.dp,
                if (isGapSection) {
                    if (isDark) Color(0xFF5A4426) else Color(0xFFFFE0B2)
                } else {
                    if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4)
                },
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = icon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGapSection) {
                            if (isDark) Color(0xFFFFCC80) else Color(0xFFB76E00)
                        } else {
                            if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                        }
                    )
                }
                if (isGapSection) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isDark) Color(0xFF422E15) else Color(0xFFFFF3E0))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Itemized Gaps",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            softWrap = false,
                            maxLines = 1,
                            color = if (isDark) Color(0xFFFFB74D) else Color(0xFFE65100)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content.sanitizeDoctorText(),
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = if (isDark) Color(0xFFD6CEDC) else Color(0xFF3F3546)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun PatientHistoryScreenPreview() {
    PatientHistoryScreenContent()
}

@Composable
private fun SummaryTabContent(
    uiState: PatientHistoryUiState,
    isDark: Boolean,
    onMedicationClick: (PatientMedicationItem) -> Unit,
    onReportClick: (PatientReportItem) -> Unit,
    onOpenHealthMonitoringClick: () -> Unit,
    onToggleHealthMonitoringExpand: () -> Unit
) {
    val data = uiState.historyData
                    // Summary Tab – Normal Patient Information
                    // A. Clinical Background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                            .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "Clinical background",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = (data?.clinicalBackgroundText ?: "No clinical background notes recorded.").sanitizeDoctorText(),
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                                color = if (isDark) Color(0xFFEDE7EF) else Color(0xFF4A3F50)
                            )

                            if (!data?.patientConfirmedDateText.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = data.patientConfirmedDateText.sanitizeDoctorText(),
                                    fontSize = 11.sp,
                                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // "Current medication" Section
                    Text(
                        text = "Current medication",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Medications Card List
                    if (data?.medications.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No medications recorded",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Prescribed medications and vitamins will appear here.",
                                    fontSize = 11.5.sp,
                                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            data?.medications?.forEachIndexed { index, item ->
                                MedicationRow(
                                    item = item,
                                    onClick = { onMedicationClick(item) },
                                    isDark = isDark
                                )
                                if (index < (data.medications.size - 1)) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // C. Recent CBC Record
                    val cbcReport = data?.reports?.firstOrNull { report ->
                        report.title.contains("cbc", ignoreCase = true) ||
                        report.title.contains("complete blood count", ignoreCase = true)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent CBC record",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                        )

                        if (cbcReport != null) {
                            Text(
                                text = "Open report ›",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary,
                                modifier = Modifier.clickable { onReportClick(cbcReport) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (cbcReport != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                .clickable { onReportClick(cbcReport) }
                                .padding(14.dp)
                        ) {
                            Column {
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
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(if (isDark) Color(0xFF2E2435) else Color(0xFFEDE4F0)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            DocumentIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = cbcReport.title.sanitizeDoctorText(),
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Report date: ${(cbcReport.reportDate ?: "Recent").sanitizeDoctorText()}${if (!cbcReport.size.isNullOrBlank()) " · ${cbcReport.size}" else ""}",
                                                fontSize = 11.5.sp,
                                                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(if (isDark) Color(0xFF1B3D2F) else MomoraMintBadgeBg)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = (cbcReport.fileType ?: "PDF").uppercase(),
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFF86E2B8) else MomoraMintBadgeText
                                        )
                                    }
                                }

                                if (!cbcReport.description.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isDark) Color(0xFF28212F) else Color(0xFFF9F6FA))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = cbcReport.description.sanitizeDoctorText(),
                                            fontSize = 11.5.sp,
                                            lineHeight = 16.sp,
                                            color = if (isDark) Color(0xFFEDE7EF) else Color(0xFF4A3F50)
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap to open complete CBC laboratory report",
                                        fontSize = 11.sp,
                                        color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    DocumentIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No CBC record available",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Complete blood count reports will appear here when uploaded.",
                                    fontSize = 11.5.sp,
                                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // D. Health Monitoring (Expandable)
                    val isMonitoringExpanded = uiState.isHealthMonitoringExpanded
                    val mData = uiState.healthMonitoringData

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                            .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    ) {
                        Column {
                            // Expandable Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleHealthMonitoringExpand() }
                                    .padding(14.dp),
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
                                            .background(if (isDark) Color(0xFF2E2435) else Color(0xFFEDE4F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "⚡", fontSize = 16.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = "Health monitoring",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        val bpSnapshot = if (mData?.latestSystolic != null && mData.latestDiastolic != null) {
                                            "BP ${mData.latestSystolic}/${mData.latestDiastolic} mmHg"
                                        } else null
                                        val weightSnapshot = if (mData?.latestWeightKg != null) {
                                            "${mData.latestWeightKg} kg"
                                        } else null
                                        val snapshotText = listOfNotNull(bpSnapshot, weightSnapshot).joinToString(" · ")
                                        Text(
                                            text = if (snapshotText.isNotBlank()) snapshotText else "Blood pressure, weight & vitals",
                                            fontSize = 11.5.sp,
                                            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isMonitoringExpanded) "▲" else "▼",
                                            fontSize = 11.sp,
                                            color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                                        )
                                    }
                                }
                            }

                            if (isMonitoringExpanded) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4))
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Blood Pressure Snapshot
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isDark) Color(0xFF28212F) else Color(0xFFF9F6FA))
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "🩺", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Blood pressure",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                                )
                                                val bpTime = mData?.bpReadingTime
                                                if (!bpTime.isNullOrBlank()) {
                                                    Text(
                                                        text = "Recorded at $bpTime",
                                                        fontSize = 10.5.sp,
                                                        color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                                    )
                                                }
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            if (mData?.latestSystolic != null && mData.latestDiastolic != null) {
                                                Text(
                                                    text = "${mData.latestSystolic}/${mData.latestDiastolic} mmHg",
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) Color(0xFF86E2B8) else Color(0xFF1E7E55)
                                                )
                                                if (mData.pulse != null) {
                                                    Text(
                                                        text = "Pulse: ${mData.pulse} bpm",
                                                        fontSize = 10.5.sp,
                                                        color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                                    )
                                                }
                                            } else {
                                                Text(
                                                    text = "Not recorded",
                                                    fontSize = 12.sp,
                                                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                                )
                                            }
                                        }
                                    }

                                    // Weight Snapshot
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isDark) Color(0xFF28212F) else Color(0xFFF9F6FA))
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "⚖️", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Weight tracking",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                                )
                                                val wTime = mData?.weightReadingTime
                                                if (!wTime.isNullOrBlank()) {
                                                    Text(
                                                        text = "Recorded at $wTime",
                                                        fontSize = 10.5.sp,
                                                        color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                                    )
                                                }
                                            }
                                        }

                                        if (mData?.latestWeightKg != null) {
                                            Text(
                                                text = "${mData.latestWeightKg} kg",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                            )
                                        } else {
                                            Text(
                                                text = "Not recorded",
                                                fontSize = 12.sp,
                                                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                            )
                                        }
                                    }

                                    // Temperature / Vitals if available
                                    if (mData?.temperature != null || mData?.latestSymptom != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isDark) Color(0xFF28212F) else Color(0xFFF9F6FA))
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "🌡️", fontSize = 16.sp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = if (mData.temperature != null) "Vitals" else "Recent symptom",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                                    )
                                                    if (!mData.latestSymptom.isNullOrBlank()) {
                                                        Text(
                                                            text = mData.latestSymptom.sanitizeDoctorText(),
                                                            fontSize = 10.5.sp,
                                                            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                                        )
                                                    }
                                                }
                                            }

                                            if (mData.temperature != null) {
                                                Text(
                                                    text = "${mData.temperature} ${mData.temperatureUnit ?: "°C"}",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                                )
                                            }
                                        }
                                    }

                                    // Button to open full monitoring screen
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MomoraPurplePrimary)
                                            .clickable { onOpenHealthMonitoringClick() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Open full health monitoring",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "⚡", fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
}

@Composable
private fun ReportsTabContent(
    uiState: PatientHistoryUiState,
    isDark: Boolean,
    onReportClick: (PatientReportItem) -> Unit
) {
    val data = uiState.historyData
                    // Reports Tab
                    Text(
                        text = "Medical reports (${data?.reports?.size ?: 0})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (data?.reports.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    DocumentIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No medical reports available",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Uploaded lab results, ultrasounds and scans for this patient will appear here.",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            data?.reports?.forEach { report ->
                                val isDownloadingThis = uiState.downloadingReportId == report.id
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                                        .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                        .clickable { onReportClick(report) }
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
                                                    .background(if (isDark) Color(0xFF2E2435) else Color(0xFFEDE4F0)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                DocumentIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = report.title.sanitizeDoctorText(),
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${(report.category ?: "Medical report").sanitizeDoctorText()} · ${(report.reportDate ?: "Recent").sanitizeDoctorText()}",
                                                    fontSize = 11.5.sp,
                                                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (isDownloadingThis) {
                                                CircularProgressIndicator(
                                                    color = MomoraPurplePrimary,
                                                    modifier = Modifier.size(16.dp),
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                if (!report.size.isNullOrBlank()) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(if (isDark) Color(0xFF2E2435) else Color(0xFFF4ECF8))
                                                            .padding(horizontal = 7.dp, vertical = 3.dp)
                                                    ) {
                                                        Text(
                                                            text = report.size,
                                                            fontSize = 10.5.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                                                        )
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (isDark) Color(0xFF1D3328) else MomoraMintBadgeBg)
                                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                                ) {
                                                    Text(
                                                        text = (report.fileType ?: "PDF").uppercase(),
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isDark) Color(0xFF85E3B3) else MomoraMintBadgeText
                                                    )
                                                }

                                                Text(
                                                    text = "›",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (isDark) Color(0xFFB5ACB9) else Color(0xFF8C7F92)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
}

@Composable
private fun VisitsTabContent(
    uiState: PatientHistoryUiState,
    isDark: Boolean
) {
    val data = uiState.historyData
                    // Visits Tab
                    Text(
                        text = "Clinical visits (${data?.visits?.size ?: 0})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (data?.visits.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CalendarNavIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No visits recorded",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Appointments, routine prenatal checks and clinical consultations will appear here.",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            data?.visits?.forEach { visit ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                                        .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
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
                                                        .background(if (isDark) Color(0xFF2E2435) else Color(0xFFEDE4F0)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CalendarNavIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                                                }

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Column {
                                                    val formattedVisitTime = visit.time.formatDoctorTime()
                                                    val dateTimeStr = buildString {
                                                        append((visit.date ?: "Date TBD").sanitizeDoctorText())
                                                        if (formattedVisitTime.isNotBlank()) {
                                                            append(" · ")
                                                            append(formattedVisitTime)
                                                        }
                                                    }
                                                    Text(
                                                        text = dateTimeStr,
                                                        fontSize = 13.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                                    )
                                                }
                                            }

                                            val status = visit.status ?: "Scheduled"
                                            val isDone = status.lowercase() in listOf("completed", "attended")
                                            val statusBg = if (isDone) (if (isDark) Color(0xFF1D3328) else MomoraMintBadgeBg) else (if (isDark) Color(0xFF2E2435) else Color(0xFFF4ECF8))
                                            val statusColor = if (isDone) (if (isDark) Color(0xFF85E3B3) else MomoraMintBadgeText) else (if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary)

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(statusBg)
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = status.replaceFirstChar { it.uppercase() },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = statusColor
                                                )
                                            }
                                        }

                                        if (!visit.notes.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = visit.notes.sanitizeDoctorText(),
                                                fontSize = 11.5.sp,
                                                lineHeight = 15.sp,
                                                color = Color(0xFF4A3F50)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
}

@Composable
private fun AiSummaryTabContent(
    uiState: PatientHistoryUiState,
    isDark: Boolean,
    resolvedPatientName: String,
    onGenerateSummaryClick: () -> Unit,
    onGenerateAgainClick: () -> Unit
) {
    val data = uiState.historyData
                    // AI Summary Tab
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "AI Patient Summary",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                            )
                            Text(
                                text = "7-section clinical synthesis",
                                fontSize = 11.5.sp,
                                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                            )
                        }

                        if (uiState.summaryData != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isDark) Color(0xFF2E2435) else Color(0xFFEDE4F0))
                                    .clickable(enabled = !uiState.isSummaryLoading) { onGenerateAgainClick() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (uiState.isSummaryLoading) "Generating..." else "Generate Again ↻",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.isSummaryLoading && uiState.summaryData == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(36.dp),
                                    color = MomoraPurplePrimary,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Analyzing patient records with local AI...",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Synthesizing gestational progress, vitals, symptoms, care plan, and unrecorded clinical information with Qwen2.5:3B.",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp,
                                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                )
                            }
                        }
                    } else if (uiState.summaryErrorMessage != null && uiState.summaryData == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF2C1E22) else Color(0xFFFFF0F0))
                                .border(1.dp, if (isDark) Color(0xFF4C272C) else Color(0xFFFFCDD2), RoundedCornerShape(16.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "⚠️ Generation Error",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFFF9B9B) else Color(0xFFC62828)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = formatErrorMessage(uiState.summaryErrorMessage ?: "Failed to generate AI patient summary"),
                                    fontSize = 12.5.sp,
                                    color = if (isDark) Color(0xFFFF9B9B) else Color(0xFFC62828),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(MomoraPurplePrimary)
                                        .clickable { onGenerateAgainClick() }
                                        .padding(horizontal = 22.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Retry Generation",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    } else if (uiState.summaryData == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF2E2435) else Color(0xFFF4ECF8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "✨", fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "AI Patient Clinical Summary",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Generate a comprehensive clinical summary of $resolvedPatientName's pregnancy records, vitals, symptoms, care schedule, and unrecorded clinical gaps.",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp,
                                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(MomoraPurplePrimary)
                                        .clickable { onGenerateSummaryClick() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Generate AI Patient Summary ✨",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    } else {
                        val summary = uiState.summaryData

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (uiState.isSummaryLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0xFF2E2435) else Color(0xFFF4ECF8))
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = MomoraPurplePrimary,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Refreshing summary with local AI...",
                                            fontSize = 12.sp,
                                            color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                                        )
                                    }
                                }
                            }

                            // 1. Clinical Reference Safety Disclaimer Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0xFF26212E) else Color(0xFFF6F2F8))
                                    .border(1.dp, if (isDark) Color(0xFF3F354A) else Color(0xFFE2D6E7), RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(text = "⚠️", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Clinical Reference Only",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = (summary.disclaimer ?: "This summary is generated from recorded patient data for clinical reference by authorized healthcare providers. It does not replace clinical judgment or make autonomous treatment changes.").sanitizeDoctorText(),
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp,
                                            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                                        )
                                    }
                                }
                            }

                            // 2. Metadata Tag Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (!summary.gestationalAge.isNullOrBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(if (isDark) Color(0xFF1B3D2F) else MomoraMintBadgeBg)
                                            .padding(horizontal = 9.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = summary.gestationalAge.sanitizeDoctorText(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDark) Color(0xFF86E2B8) else MomoraMintBadgeText
                                        )
                                    }
                                }
                                if (!summary.dueDateText.isNullOrBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(if (isDark) Color(0xFF2E2435) else Color(0xFFF4ECF8))
                                            .padding(horizontal = 9.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = summary.dueDateText.sanitizeDoctorText(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                                        )
                                    }
                                }
                            }

                            // 3. The 7 Structured Sections
                            val sections = summary.sections

                            // Section 1: Pregnancy Overview
                            DoctorSummarySectionCard(
                                icon = "🌸",
                                title = "1. Pregnancy Overview",
                                content = sections?.pregnancyOverview ?: "No pregnancy details recorded.",
                                isDark = isDark
                            )

                            // Section 2: Medical and Health Profile
                            DoctorSummarySectionCard(
                                icon = "🩺",
                                title = "2. Medical and Health Profile",
                                content = sections?.medicalHealthProfile ?: "No profile details recorded.",
                                isDark = isDark
                            )

                            // Section 3: Recent Symptoms and Concerns
                            DoctorSummarySectionCard(
                                icon = "⚠️",
                                title = "3. Recent Symptoms and Concerns",
                                content = sections?.recentSymptomsConcerns ?: "No symptoms logged.",
                                isDark = isDark
                            )

                            // Section 4: Available Health Measurements
                            DoctorSummarySectionCard(
                                icon = "📊",
                                title = "4. Available Health Measurements",
                                content = sections?.availableHealthMeasurements ?: "No measurements logged.",
                                isDark = isDark
                            )

                            // Section 5: Medication and Care Schedule
                            DoctorSummarySectionCard(
                                icon = "💊",
                                title = "5. Medication and Care Schedule",
                                content = sections?.medicationCareSchedule ?: "No active medications recorded.",
                                isDark = isDark
                            )

                            // Section 6: Recent Visits and Follow-up
                            DoctorSummarySectionCard(
                                icon = "🗓️",
                                title = "6. Recent Visits and Follow-up",
                                content = sections?.recentVisitsFollowup ?: "No visits recorded.",
                                isDark = isDark
                            )

                            // Section 7: Information Not Yet Recorded (highlighted gaps)
                            DoctorSummarySectionCard(
                                icon = "📋",
                                title = "7. Information Not Yet Recorded",
                                content = sections?.informationNotRecorded ?: "All standard prenatal items currently recorded.",
                                isDark = isDark,
                                isGapSection = true
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Bottom Action: Generate Again Button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isDark) Color(0xFF2E2435) else Color(0xFFF4ECF8))
                                    .clickable(enabled = !uiState.isSummaryLoading) { onGenerateAgainClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Generate Again ↻",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                                    )
                                }
                            }
                        }
                    }
}
