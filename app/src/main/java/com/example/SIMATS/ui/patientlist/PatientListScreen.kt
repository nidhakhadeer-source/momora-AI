package com.example.SIMATS.ui.patientlist

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AssignedPatientItem
import com.example.SIMATS.data.repository.NotificationStateManager
import com.example.SIMATS.ui.components.formatDoctorTime
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.components.sanitizeDoctorText
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraIconBtnBg
import com.example.SIMATS.ui.theme.MomoraLavenderCircleBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun PatientListScreen(
    viewModel: PatientListViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 1,
    onNavigateBack: () -> Unit = {},
    onPatientClick: (String, String) -> Unit = { _, _ -> },
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    PatientListScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onSearchQueryChanged = { q -> viewModel.onEvent(PatientListUiEvent.OnSearchQueryChanged(q)) },
        onFilterTabSelected = { index -> viewModel.onEvent(PatientListUiEvent.OnFilterTabSelected(index)) },
        onPatientClick = { id, name ->
            viewModel.onEvent(PatientListUiEvent.OnPatientClicked(id, name))
            onPatientClick(id, name)
        },
        onPreviousPageClick = { viewModel.onEvent(PatientListUiEvent.OnPreviousPageClicked) },
        onNextPageClick = { viewModel.onEvent(PatientListUiEvent.OnNextPageClicked) },
        onRetryClick = { viewModel.onEvent(PatientListUiEvent.OnRetryClicked) },
        onBackClick = {
            viewModel.onEvent(PatientListUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = {
            viewModel.onEvent(PatientListUiEvent.OnNotificationClicked)
            onNotificationClick()
        },
        onNavTabSelected = { index ->
            viewModel.onEvent(PatientListUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun PatientListScreenContent(
    modifier: Modifier = Modifier,
    uiState: PatientListUiState = PatientListUiState(
        patients = listOf(
            AssignedPatientItem(
                id = "1",
                patientIdText = "P-13042",
                name = "Maya Patel",
                gestationalAge = "24w 3d",
                dueDateText = "Due 13 Feb 2027",
                latestVitalsNote = "Latest BP 112/72 mmHg · Updated today",
                needsReview = false,
                isHighlighted = true
            ),
            AssignedPatientItem(
                id = "2",
                patientIdText = "P-1098",
                name = "Laila Ahmed",
                gestationalAge = "30w 2d",
                needsReview = true,
                reviewType = "New ultrasound report"
            ),
            AssignedPatientItem(
                id = "3",
                patientIdText = "P-1011",
                name = "Sofia Reyes",
                gestationalAge = "18w 5d",
                needsReview = true,
                reviewType = "New lab report"
            ),
            AssignedPatientItem(
                id = "4",
                patientIdText = "P-1027",
                name = "Nora Williams",
                gestationalAge = "38w 0d",
                visitNote = "Visit today",
                needsReview = false
            ),
            AssignedPatientItem(
                id = "5",
                patientIdText = "P-1093",
                name = "Emma Wilson",
                gestationalAge = "12w 1d",
                needsReview = false
            )
        )
    ),
    onSearchQueryChanged: (String) -> Unit = {},
    onFilterTabSelected: (Int) -> Unit = {},
    onPatientClick: (String, String) -> Unit = { _, _ -> },
    onPreviousPageClick: () -> Unit = {},
    onNextPageClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    selectedNavIndex: Int = 1,
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(elevation = 1.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFFF7F2FA))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                BackArrowIcon(
                    color = Color(0xFF5A3D63),
                    modifier = Modifier.size(18.dp)
                )
            }

            val unreadCount by NotificationStateManager.unreadCount.collectAsState()
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(elevation = 1.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFFFAF0F3))
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                BellIcon(
                    color = Color(0xFF6E4F76),
                    modifier = Modifier.size(19.dp)
                )
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935))
                    )
                }
            }
        }

        // 2. Scrollable Body Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Your assigned patients",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            val rawDoc = SessionManager.getUserName().takeIf { it.isNotBlank() } ?: "Dr Amelia Chen"
            val formattedDoc = if (rawDoc.startsWith("Dr", ignoreCase = true)) rawDoc else "Dr $rawDoc"
            val patientCount = if (uiState.totalCount > 0) uiState.totalCount else uiState.patients.size
            val countLabel = if (patientCount == 1) "1 patient" else "$patientCount patients"

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1E7F5))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = countLabel,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6E4F76)
                    )
                }
                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formattedDoc,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            Text(
                text = "Find a patient",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEADBEE), RoundedCornerShape(16.dp))
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
                                text = "Search by name or patient ID",
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
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    SearchIcon(color = Color(0xFF6E4F76), modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs (All vs To review)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF3ECF7))
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf("All · ${uiState.totalCount}", "To review · ${uiState.reviewCount}")
                    tabs.forEachIndexed { index, label ->
                        val isSelected = uiState.selectedFilterTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .then(
                                    if (isSelected) Modifier.shadow(elevation = 1.5.dp, shape = RoundedCornerShape(12.dp)) else Modifier
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { onFilterTabSelected(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF5A3D63) else Color(0xFF7D7584)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Patient List Cards
            val filteredList = if (uiState.selectedFilterTab == 1) {
                uiState.patients.filter { it.needsReview }
            } else {
                uiState.patients
            }

            if (uiState.isLoading && uiState.patients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6E4F76))
                }
            } else if (uiState.errorMessage != null && uiState.patients.isEmpty()) {
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
                            text = formatErrorMessage(uiState.errorMessage ?: "Failed to load assigned patients"),
                            fontSize = 13.5.sp,
                            color = Color(0xFFA94442),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF6E4F76))
                                .clickable { onRetryClick() }
                                .padding(horizontal = 18.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Retry",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(18.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (uiState.selectedFilterTab == 1) "No patients need review" else "No assigned patients found",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (uiState.selectedFilterTab == 1) "All patient reports have been reviewed." else "Patients assigned to you will appear here.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                if (uiState.errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFEBEE))
                            .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatErrorMessage(uiState.errorMessage),
                                fontSize = 12.sp,
                                color = Color(0xFFC62828),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Retry",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6E4F76),
                                modifier = Modifier.clickable { onRetryClick() }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filteredList.forEach { patient ->
                        PatientCardItem(
                            item = patient,
                            onClick = { onPatientClick(patient.id, patient.name) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 3. Doctor Bottom Navigation Bar (4 Doctor Tabs)
        DoctorBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

/**
 * Single Patient Row Card Item.
 */
@Composable
private fun PatientCardItem(
    item: AssignedPatientItem,
    onClick: () -> Unit
) {
    val cardBg = if (item.isHighlighted) Color(0xFFFBF8FD) else Color.White
    val borderColor = if (item.isHighlighted) Color(0xFFEADBEE) else Color(0xFFEFE8F4)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable { onClick() }
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
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val avatarBg = when {
                        item.visitNote != null -> Color(0xFFE2F4EB)
                        item.needsReview -> Color(0xFFFAF0F3)
                        else -> Color(0xFFF1E7F5)
                    }
                    val avatarBorder = when {
                        item.visitNote != null -> Color(0xFFC7EBD7)
                        item.needsReview -> Color(0xFFF5D8E4)
                        else -> Color(0xFFEADBEE)
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(avatarBg)
                            .border(1.dp, avatarBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        UserNavIcon(color = Color(0xFF6E4F76), modifier = Modifier.size(18.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name.sanitizeDoctorText(),
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        val subtitleText = buildAnnotatedString {
                            append(item.patientIdText.sanitizeDoctorText())
                            if (item.gestationalAge.isNotBlank()) {
                                append(" · ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF944B68))) {
                                    append(item.gestationalAge.sanitizeDoctorText())
                                }
                            }
                            if (!item.dueDateText.isNullOrEmpty()) {
                                append(" · ")
                                append(item.dueDateText.sanitizeDoctorText().replace(" ", "\u00A0"))
                            }
                            if (!item.visitNote.isNullOrEmpty()) {
                                append(" · ")
                                append(item.visitNote.sanitizeDoctorText())
                            }
                        }

                        Text(
                            text = subtitleText,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (item.needsReview) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF6E4F76))
                            .clickable { onClick() }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Review",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF7F2FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "›",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6E4F76)
                        )
                    }
                }
            }

            // Optional note (vitals note or review type or visit note)
            val hasVitals = !item.latestVitalsNote.isNullOrEmpty()
            val hasReviewType = !item.reviewType.isNullOrEmpty()
            val hasVisitNote = !item.visitNote.isNullOrEmpty()

            if (hasVitals || hasReviewType || hasVisitNote) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFF5EEF7))
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (hasVitals) {
                        VitalsPulseIcon(
                            color = Color(0xFF8E8394),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = item.latestVitalsNote!!.sanitizeDoctorText(),
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else if (hasReviewType) {
                        DocumentIcon(
                            color = Color(0xFF944B68),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = item.reviewType!!.sanitizeDoctorText(),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF944B68)
                        )
                    } else if (hasVisitNote) {
                        CalendarClockIcon(
                            color = Color(0xFF3D7B59),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = item.visitNote!!.sanitizeDoctorText(),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3D7B59)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Doctor Bottom Navigation Bar (4 Doctor Tabs).
 */
@Composable
private fun DoctorBottomNavBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val navSurface = MaterialTheme.colorScheme.surface
    val navBorder = Color(0xFFEFE8F2)
    val indicatorColor = Color(0xFFDFD5E3)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(navSurface)
            .border(1.dp, navBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
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
                icon = { HomeNavIcon(color = if (selectedIndex == 0) Color(0xFF6E4F76) else Color(0xFF8E8394), modifier = Modifier.size(22.dp)) }
            )
            NavTabItem(
                label = "Patients",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { PeopleNavIcon(color = if (selectedIndex == 1) Color(0xFF6E4F76) else Color(0xFF8E8394), modifier = Modifier.size(22.dp)) }
            )
            NavTabItem(
                label = "Schedule",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { CalendarNavIcon(color = if (selectedIndex == 2) Color(0xFF6E4F76) else Color(0xFF8E8394), modifier = Modifier.size(22.dp)) }
            )
            NavTabItem(
                label = "Advice",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { ChatNavIcon(color = if (selectedIndex == 3) Color(0xFF6E4F76) else Color(0xFF8E8394), modifier = Modifier.size(22.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(134.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(indicatorColor)
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
    val activeBg = Color(0xFFF1E7F5)
    val activeColor = Color(0xFF5A3D63)
    val inactiveColor = Color(0xFF8E8394)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) activeBg else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}

/**
 * Custom Canvas vector drawings.
 */
@Composable
private fun BackArrowIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val arrowPath = Path().apply {
            moveTo(w * 0.65f, h * 0.20f)
            lineTo(w * 0.35f, h * 0.50f)
            lineTo(w * 0.65f, h * 0.80f)
        }
        drawPath(
            path = arrowPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
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
private fun DocumentIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.6.dp.toPx()

        val docPath = Path().apply {
            moveTo(w * 0.25f, h * 0.18f)
            lineTo(w * 0.58f, h * 0.18f)
            lineTo(w * 0.75f, h * 0.35f)
            lineTo(w * 0.75f, h * 0.82f)
            lineTo(w * 0.25f, h * 0.82f)
            close()
        }
        drawPath(docPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val fold = Path().apply {
            moveTo(w * 0.58f, h * 0.18f)
            lineTo(w * 0.58f, h * 0.35f)
            lineTo(w * 0.75f, h * 0.35f)
        }
        drawPath(fold, color = color, style = Stroke(width = strokeWidth * 0.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = color, start = Offset(w * 0.36f, h * 0.50f), end = Offset(w * 0.64f, h * 0.50f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.36f, h * 0.64f), end = Offset(w * 0.56f, h * 0.64f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    }
}

@Composable
private fun CalendarClockIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.6.dp.toPx()

        val calPath = Path().apply {
            moveTo(w * 0.20f, h * 0.25f)
            lineTo(w * 0.80f, h * 0.25f)
            lineTo(w * 0.80f, h * 0.82f)
            lineTo(w * 0.20f, h * 0.82f)
            close()
        }
        drawPath(calPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(color = color, start = Offset(w * 0.20f, h * 0.42f), end = Offset(w * 0.80f, h * 0.42f), strokeWidth = strokeWidth)
        drawLine(color = color, start = Offset(w * 0.36f, h * 0.16f), end = Offset(w * 0.36f, h * 0.25f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.64f, h * 0.16f), end = Offset(w * 0.64f, h * 0.25f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    }
}

@Composable
private fun VitalsPulseIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.6.dp.toPx()

        val pulsePath = Path().apply {
            moveTo(w * 0.10f, h * 0.52f)
            lineTo(w * 0.30f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.20f)
            lineTo(w * 0.56f, h * 0.82f)
            lineTo(w * 0.68f, h * 0.42f)
            lineTo(w * 0.78f, h * 0.52f)
            lineTo(w * 0.90f, h * 0.52f)
        }
        drawPath(
            path = pulsePath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun PatientListScreenPreview() {
    PatientListScreenContent()
}
