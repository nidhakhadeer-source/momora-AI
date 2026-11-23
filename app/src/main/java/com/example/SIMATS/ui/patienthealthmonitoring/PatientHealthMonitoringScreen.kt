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
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.example.SIMATS.data.model.PatientHealthMonitoringData
import com.example.SIMATS.navigation.SelectedPatientHolder
import com.example.SIMATS.ui.components.formatDoctorTime
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.components.sanitizeDoctorText
import com.example.SIMATS.ui.theme.ThemeManager
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
    selectedNavIndex: Int = 1,
    onNavigateBack: () -> Unit = {},
    onWriteAdviceClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    PatientHealthMonitoringScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onDaysSelected = { days -> viewModel.onEvent(PatientHealthMonitoringUiEvent.OnDaysSelected(days)) },
        onBpCardClick = { viewModel.onEvent(PatientHealthMonitoringUiEvent.OnBpCardClicked) },
        onWeightCardClick = { viewModel.onEvent(PatientHealthMonitoringUiEvent.OnWeightCardClicked) },
        onDismissSheet = { viewModel.onEvent(PatientHealthMonitoringUiEvent.OnDismissSheet) },
        onWriteAdviceClick = {
            viewModel.onEvent(PatientHealthMonitoringUiEvent.OnWriteAdviceClicked)
            onWriteAdviceClick()
        },
        onRetryClick = { viewModel.onEvent(PatientHealthMonitoringUiEvent.OnRetryClicked) },
        onBackClick = {
            viewModel.onEvent(PatientHealthMonitoringUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = {
            viewModel.onEvent(PatientHealthMonitoringUiEvent.OnNotificationClicked)
            onNotificationClick()
        },
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
    onDaysSelected: (Int) -> Unit = {},
    onBpCardClick: () -> Unit = {},
    onWeightCardClick: () -> Unit = {},
    onDismissSheet: () -> Unit = {},
    onWriteAdviceClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    selectedNavIndex: Int = 1,
    onNavTabSelected: (Int) -> Unit = {}
) {
    val isDark = ThemeManager.isDarkTheme()
    val scrollState = rememberScrollState()
    val data = uiState.monitoringData
    val resolvedPatientName = data?.patientName?.takeIf { it.isNotBlank() }
        ?: uiState.patientName?.takeIf { it.isNotBlank() }
        ?: SelectedPatientHolder.selectedPatientName?.takeIf { it.isNotBlank() }
        ?: "Patient"

    val patientFirstName = resolvedPatientName.trim().split("\\s+".toRegex()).firstOrNull()?.takeIf { it.isNotBlank() } ?: resolvedPatientName

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
                        .background(Color.White)
                        .border(1.dp, Color(0xFFF2DEDE), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatErrorMessage(uiState.errorMessage ?: "Failed to load health monitoring data"),
                            fontSize = 14.sp,
                            color = Color(0xFFA94442),
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
                    text = "Health monitoring",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Patient-entered readings · ${(data?.dateText ?: "27 Oct 2026").sanitizeDoctorText()}",
                fontSize = 13.sp,
                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Patient Card
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
                                .background(if (isDark) Color(0xFF2E2435) else Color.White),
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
                        text = data?.patientIdText ?: "P-13042",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Vitals Overview Cards Row (BP & Weight)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Blood pressure Card (Tappable to show readings list)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF1D3328) else MomoraMintBadgeBg)
                        .border(1.dp, if (isDark) Color(0xFF2C4D3D) else Color.Transparent, RoundedCornerShape(16.dp))
                        .clickable { onBpCardClick() }
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Blood pressure",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFF88D4A8) else MomoraMintBadgeText
                            )
                            Text(
                                text = "›",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFF88D4A8) else MomoraMintBadgeText
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val bpVal = if (data?.latestSystolic != null && data?.latestDiastolic != null) {
                            "${data.latestSystolic}/${data.latestDiastolic}"
                        } else {
                            "—/—"
                        }
                        Text(
                            text = bpVal,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = data?.bpReadingTime?.let { "mmHg · ${it.formatDoctorTime()}" } ?: "mmHg · Tap to view",
                            fontSize = 10.5.sp,
                            color = if (isDark) Color(0xFF88D4A8) else Color(0xFF43524E)
                        )
                    }
                }

                // Weight Card (Tappable to show readings list)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF261F2C) else MomoraLavenderCircleBg)
                        .border(1.dp, if (isDark) Color(0xFF45364E) else Color.Transparent, RoundedCornerShape(16.dp))
                        .clickable { onWeightCardClick() }
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Weight",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                            )
                            Text(
                                text = "›",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val wVal = data?.latestWeightKg?.let { "$it" } ?: "—"
                        Text(
                            text = wVal,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = data?.weightReadingTime?.let { "kg · ${it.formatDoctorTime()}" } ?: "kg · Tap to view",
                            fontSize = 10.5.sp,
                            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Line Chart Card (Blood pressure with 7 / 14 / 30 day selector)
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
                            text = "Blood pressure",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // 7d / 14d / 30d selector tabs (unified segmented pill design)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF6F0F8))
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(7, 14, 30).forEach { days ->
                                val isSelected = uiState.selectedDays == days
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MomoraPurplePrimary else Color.Transparent)
                                        .clickable { onDaysSelected(days) }
                                        .padding(horizontal = 9.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${days}d",
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MomoraPurpleDark,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Systolic · mmHg",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MomoraPurplePrimary,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MomoraMintBadgeText)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Diastolic · mmHg",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MomoraMintBadgeText,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Safe Native Chart Drawing
                    val sysTrends = data?.systolicTrends ?: emptyList()
                    val diaTrends = data?.diastolicTrends ?: emptyList()

                    if (sysTrends.isEmpty() || diaTrends.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No readings logged in the last ${uiState.selectedDays} days",
                                fontSize = 12.sp,
                                color = MomoraBodyMuted
                            )
                        }
                    } else {
                        HealthMonitoringChart(
                            systolicTrends = sysTrends,
                            diastolicTrends = diaTrends,
                            latestTime = data?.bpReadingTime,
                            selectedDays = uiState.selectedDays,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real Vitals & Symptoms Section
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
                        text = "Vitals & symptoms",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Pulse
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF9F5FB))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "Pulse", fontSize = 11.sp, color = MomoraBodyMuted)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = data?.pulse?.let { "$it bpm" } ?: "—",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraPurpleDark
                                )
                            }
                        }

                        // Temperature
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF9F5FB))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "Temperature", fontSize = 11.sp, color = MomoraBodyMuted)
                                Spacer(modifier = Modifier.height(2.dp))
                                val rawUnit = data?.temperatureUnit
                                val tempUnit = if (rawUnit?.contains("??") == true || rawUnit.isNullOrBlank()) "°C" else rawUnit.sanitizeDoctorText()
                                val tempStr = if (data?.temperature != null) {
                                    "${data.temperature} $tempUnit"
                                } else {
                                    "—"
                                }
                                Text(
                                    text = tempStr,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraPurpleDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Latest Symptom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9F5FB))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "Latest symptom", fontSize = 11.sp, color = MomoraBodyMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = (data?.latestSymptom?.takeIf { it.isNotBlank() } ?: "No symptoms reported").sanitizeDoctorText(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MomoraPurpleDark
                            )
                        }
                    }
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
                        text = "Write advice for $patientFirstName",
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
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Doctor Bottom Navigation Bar
        DoctorBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }

    if (uiState.isBpSheetOpen) {
        BpReadingsBottomSheet(
            data = data,
            selectedDays = uiState.selectedDays,
            onClose = onDismissSheet
        )
    }

    if (uiState.isWeightSheetOpen) {
        WeightReadingsBottomSheet(
            data = data,
            onClose = onDismissSheet
        )
    }
}

/**
 * Native Canvas Chart for Blood Pressure Trends with real medical mmHg scale.
 */
@Composable
private fun HealthMonitoringChart(
    systolicTrends: List<Float>,
    diastolicTrends: List<Float>,
    latestTime: String? = null,
    selectedDays: Int = 7,
    modifier: Modifier = Modifier
) {
    val allValues = systolicTrends + diastolicTrends
    val maxReading = allValues.maxOrNull() ?: 120f

    // Medical BP scale (mmHg)
    val ySteps = if (maxReading > 140f) {
        listOf(160, 130, 100, 70)
    } else {
        listOf(140, 120, 100, 80, 60)
    }
    val yMax = ySteps.first().toFloat()
    val yMin = ySteps.last().toFloat()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            // Y-axis numerical labels (mmHg)
            Column(
                modifier = Modifier
                    .width(26.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                ySteps.forEach { step ->
                    Text(
                        text = "$step",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9E8E9F)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Canvas drawing grid, lines, data points, and highlight
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val gridColor = Color(0xFFF1E9F5)
                    val strokeWidth = 2.5.dp.toPx()

                    // Draw horizontal grid lines matching the ySteps labels exactly
                    ySteps.forEachIndexed { i, _ ->
                        val yPos = if (ySteps.size > 1) (h / (ySteps.size - 1)) * i else 0f
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, yPos),
                            end = Offset(w, yPos),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw reference target guide line for normal Systolic (<120) and Diastolic (<80)
                    if (yMax >= 120f && yMin <= 120f) {
                        val norm120 = ((120f - yMin) / (yMax - yMin)).coerceIn(0f, 1f)
                        val y120 = h - (norm120 * h)
                        drawLine(
                            color = Color(0x336E4F76),
                            start = Offset(0f, y120),
                            end = Offset(w, y120),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    if (yMax >= 80f && yMin <= 80f) {
                        val norm80 = ((80f - yMin) / (yMax - yMin)).coerceIn(0f, 1f)
                        val y80 = h - (norm80 * h)
                        drawLine(
                            color = Color(0x333D7B59),
                            start = Offset(0f, y80),
                            end = Offset(w, y80),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val count = systolicTrends.size
                    if (count == 1) {
                        val normYSys = ((systolicTrends[0] - yMin) / (yMax - yMin)).coerceIn(0f, 1f)
                        val ySys = h - (normYSys * h)
                        drawCircle(color = Color(0xFF6E4F76), radius = 5.dp.toPx(), center = Offset(w / 2f, ySys))
                        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(w / 2f, ySys))

                        if (diastolicTrends.isNotEmpty()) {
                            val normYDia = ((diastolicTrends[0] - yMin) / (yMax - yMin)).coerceIn(0f, 1f)
                            val yDia = h - (normYDia * h)
                            drawCircle(color = Color(0xFF3D7B59), radius = 5.dp.toPx(), center = Offset(w / 2f, yDia))
                            drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(w / 2f, yDia))
                        }
                    } else if (count > 1) {
                        val stepX = w / (count - 1)

                        // 1. Draw Systolic Line (Purple)
                        val sysPath = Path()
                        val sysPoints = mutableListOf<Offset>()
                        systolicTrends.forEachIndexed { index, sysVal ->
                            val x = index * stepX
                            val normY = ((sysVal - yMin) / (yMax - yMin)).coerceIn(0f, 1f)
                            val y = h - (normY * h)
                            val pt = Offset(x, y)
                            sysPoints.add(pt)
                            if (index == 0) sysPath.moveTo(x, y) else sysPath.lineTo(x, y)
                        }
                        drawPath(
                            path = sysPath,
                            color = Color(0xFF6E4F76),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        // Draw Systolic data points
                        sysPoints.forEachIndexed { index, pt ->
                            val isLatest = index == sysPoints.lastIndex
                            if (isLatest) {
                                drawCircle(color = Color(0xFF6E4F76), radius = 5.dp.toPx(), center = pt)
                                drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = pt)
                            } else {
                                drawCircle(color = Color(0xFF6E4F76), radius = 3.dp.toPx(), center = pt)
                            }
                        }

                        // 2. Draw Diastolic Line (Mint Green)
                        val diaPath = Path()
                        val diaPoints = mutableListOf<Offset>()
                        diastolicTrends.forEachIndexed { index, diaVal ->
                            val x = index * stepX
                            val normY = ((diaVal - yMin) / (yMax - yMin)).coerceIn(0f, 1f)
                            val y = h - (normY * h)
                            val pt = Offset(x, y)
                            diaPoints.add(pt)
                            if (index == 0) diaPath.moveTo(x, y) else diaPath.lineTo(x, y)
                        }
                        drawPath(
                            path = diaPath,
                            color = Color(0xFF3D7B59),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        // Draw Diastolic data points
                        diaPoints.forEachIndexed { index, pt ->
                            val isLatest = index == diaPoints.lastIndex
                            if (isLatest) {
                                drawCircle(color = Color(0xFF3D7B59), radius = 5.dp.toPx(), center = pt)
                                drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = pt)
                            } else {
                                drawCircle(color = Color(0xFF3D7B59), radius = 3.dp.toPx(), center = pt)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // X-axis timeline indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Day 1",
                fontSize = 10.sp,
                color = MomoraBodyMuted
            )
            Text(
                text = "${systolicTrends.size} readings in ${selectedDays}d",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MomoraPurpleDark
            )
            Text(
                text = if (!latestTime.isNullOrBlank()) "Latest · ${latestTime.formatDoctorTime()}" else "Latest",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MomoraPurpleDark
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BpReadingsBottomSheet(
    data: PatientHealthMonitoringData?,
    selectedDays: Int,
    onClose: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = Color.White,
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
                    text = "Blood pressure readings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF4ECF8))
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 14.sp,
                        color = MomoraPurpleDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Last $selectedDays days · ${data?.systolicTrends?.size ?: 0} readings logged",
                fontSize = 12.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Latest highlight card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MomoraMintBadgeBg)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Latest Reading",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MomoraMintBadgeText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val bpStr = if (data?.latestSystolic != null && data?.latestDiastolic != null) {
                            "${data.latestSystolic}/${data.latestDiastolic} mmHg"
                        } else {
                            "—/— mmHg"
                        }
                        Text(
                            text = bpStr,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = data?.bpReadingTime?.let { "Time: ${it.formatDoctorTime()}" } ?: "Recent",
                            fontSize = 11.5.sp,
                            color = Color(0xFF43524E)
                        )
                        if (data?.pulse != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Pulse: ${data.pulse} bpm",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MomoraPurpleDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val sys = data?.systolicTrends ?: emptyList()
            val dia = data?.diastolicTrends ?: emptyList()

            if (sys.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFBF8FC))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No readings logged in the last $selectedDays days",
                        fontSize = 13.sp,
                        color = MomoraBodyMuted
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFBF8FC))
                        .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    sys.indices.reversed().forEach { idx ->
                        val s = sys[idx].toInt()
                        val d = if (idx < dia.size) dia[idx].toInt() else 0
                        val isNormal = s < 120 && d < 80
                        val isElevated = s in 120..129 && d < 80
                        val statusTag = if (isNormal) "Normal" else if (isElevated) "Elevated" else "High"
                        val tagBg = if (isNormal) MomoraMintBadgeBg else if (isElevated) Color(0xFFF0EBF4) else Color(0xFFFFE8E8)
                        val tagColor = if (isNormal) MomoraMintBadgeText else if (isElevated) MomoraPurplePrimary else Color(0xFFC0392B)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reading #${idx + 1}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MomoraPurpleDark
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$s / $d mmHg",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraPurpleText
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(tagBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = statusTag,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = tagColor
                                    )
                                }
                            }
                        }
                        if (idx > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.5.dp)
                                    .background(Color(0xFFEFE8F4))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { onClose() },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeightReadingsBottomSheet(
    data: PatientHealthMonitoringData?,
    onClose: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = Color.White,
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
                    text = "Weight tracker details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF4ECF8))
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 14.sp,
                        color = MomoraPurpleDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            val patientDisplayName = data?.patientName?.takeIf { it.isNotBlank() } ?: "Patient"
            Text(
                text = "${patientDisplayName.sanitizeDoctorText()} · ${(data?.gestationalAge ?: "Gestational progress").sanitizeDoctorText()}",
                fontSize = 12.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (data?.latestWeightKg == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFBF8FC))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No weight readings logged yet",
                        fontSize = 13.sp,
                        color = MomoraBodyMuted
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MomoraLavenderCircleBg)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Latest Recorded Weight",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MomoraPurplePrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${data.latestWeightKg} kg",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Logged at ${(data.weightReadingTime ?: "Recent").formatDoctorTime()}",
                            fontSize = 11.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFBF8FC))
                        .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Clinical Context",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Weight progression is monitored across trimesters. Expected maternal weight gain is evaluated against baseline pre-pregnancy BMI during clinical consultations.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color(0xFF4A3F50)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { onClose() },
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
