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
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.data.model.DoctorAccountItem
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import androidx.compose.material3.CircularProgressIndicator
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.ThemeManager
import com.example.SIMATS.data.repository.NotificationStateManager
import com.example.SIMATS.ui.admindashboard.AdminNotificationsDialog
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ManageDoctorsScreen(
    viewModel: ManageDoctorsViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 1,
    onNavigateBack: () -> Unit = {},
    onAddDoctorClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadDoctors()
        viewModel.loadNotifications()
    }

    ManageDoctorsScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onSearchQueryChanged = { q -> viewModel.onEvent(ManageDoctorsUiEvent.OnSearchQueryChanged(q)) },
        onFilterTabSelected = { index -> viewModel.onEvent(ManageDoctorsUiEvent.OnFilterTabSelected(index)) },
        onCardClick = { doc -> viewModel.onEvent(ManageDoctorsUiEvent.OnDoctorCardClicked(doc)) },
        onToggleDoctorStatus = { doc -> viewModel.onEvent(ManageDoctorsUiEvent.OnRequestToggleStatus(doc)) },
        onEditDetailsClick = { doc -> viewModel.onEvent(ManageDoctorsUiEvent.OnOpenEditDoctor(doc)) },
        onDismissFeedback = { viewModel.onEvent(ManageDoctorsUiEvent.OnDismissFeedbackMessage) },
        onRetryClick = { viewModel.onEvent(ManageDoctorsUiEvent.OnRetryClicked) },
        onAddDoctorClick = {
            viewModel.onEvent(ManageDoctorsUiEvent.OnAddDoctorClicked)
            onAddDoctorClick()
        },
        onNextPageClick = { viewModel.onEvent(ManageDoctorsUiEvent.OnNextPageClicked) },
        onBackClick = {
            viewModel.onEvent(ManageDoctorsUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = {
            viewModel.onEvent(ManageDoctorsUiEvent.OnNotificationClicked)
            onNotificationClick()
        },
        onNavTabSelected = { index ->
            viewModel.onEvent(ManageDoctorsUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )

    // Doctor Details Dialog
    if (uiState.showDoctorDetailsDialog && uiState.selectedDoctor != null) {
        DoctorDetailsDialog(
            doctor = uiState.selectedDoctor!!,
            onDismiss = { viewModel.onEvent(ManageDoctorsUiEvent.OnDismissDoctorDetailsDialog) },
            onEditClick = { doc ->
                viewModel.onEvent(ManageDoctorsUiEvent.OnDismissDoctorDetailsDialog)
                viewModel.onEvent(ManageDoctorsUiEvent.OnOpenEditDoctor(doc))
            },
            onToggleStatusClick = { doc ->
                viewModel.onEvent(ManageDoctorsUiEvent.OnDismissDoctorDetailsDialog)
                viewModel.onEvent(ManageDoctorsUiEvent.OnRequestToggleStatus(doc))
            }
        )
    }

    // Toggle Confirm Dialog
    if (uiState.showToggleConfirmDialog && uiState.doctorToToggle != null) {
        DoctorToggleConfirmDialog(
            doctor = uiState.doctorToToggle!!,
            isToggling = uiState.isTogglingStatus,
            onConfirm = { viewModel.onEvent(ManageDoctorsUiEvent.OnConfirmToggleStatus) },
            onDismiss = { viewModel.onEvent(ManageDoctorsUiEvent.OnDismissToggleConfirmDialog) }
        )
    }

    // Edit Doctor Dialog
    if (uiState.showEditDoctorDialog && uiState.doctorToEdit != null) {
        DoctorEditDialog(
            uiState = uiState,
            onNameChanged = { viewModel.onEvent(ManageDoctorsUiEvent.OnEditNameChanged(it)) },
            onEmailChanged = { viewModel.onEvent(ManageDoctorsUiEvent.OnEditEmailChanged(it)) },
            onSpecialtyChanged = { viewModel.onEvent(ManageDoctorsUiEvent.OnEditSpecialtyChanged(it)) },
            onLicenseIdChanged = { viewModel.onEvent(ManageDoctorsUiEvent.OnEditLicenseIdChanged(it)) },
            onClinicChanged = { viewModel.onEvent(ManageDoctorsUiEvent.OnEditClinicChanged(it)) },
            onSave = { viewModel.onEvent(ManageDoctorsUiEvent.OnSaveDoctorDetails) },
            onDismiss = { viewModel.onEvent(ManageDoctorsUiEvent.OnDismissEditDoctorDialog) }
        )
    }

    // Notifications Dialog
    if (uiState.showNotificationsDialog) {
        AdminNotificationsDialog(
            notifications = uiState.notificationsList,
            isLoading = uiState.isLoadingNotifications,
            onDismiss = { viewModel.onEvent(ManageDoctorsUiEvent.OnDismissNotificationsDialog) }
        )
    }
}

@Composable
fun ManageDoctorsScreenContent(
    modifier: Modifier = Modifier,
    uiState: ManageDoctorsUiState = ManageDoctorsUiState(),
    onSearchQueryChanged: (String) -> Unit = {},
    onFilterTabSelected: (Int) -> Unit = {},
    onCardClick: (DoctorAccountItem) -> Unit = {},
    onToggleDoctorStatus: (DoctorAccountItem) -> Unit = {},
    onEditDetailsClick: (DoctorAccountItem) -> Unit = {},
    onDismissFeedback: () -> Unit = {},
    onAddDoctorClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onNextPageClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    selectedNavIndex: Int = 1,
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val isDark = ThemeManager.isDarkTheme()

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
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleText
                )
            }

            val unreadCount by NotificationStateManager.unreadCount.collectAsState()
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                BellIcon(
                    color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary,
                    modifier = Modifier.size(20.dp)
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Manage doctors",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${uiState.totalCount} total · ${uiState.activeCount} active · ${uiState.inactiveCount} inactive",
                fontSize = 13.sp,
                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (!uiState.actionFeedbackMessage.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEBF7F0))
                        .border(1.dp, Color(0xFFD4EDDA), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.actionFeedbackMessage,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E6B47),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "✕",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E6B47),
                            modifier = Modifier.clickable { onDismissFeedback() }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Search Bar
            Text(
                text = "Find a doctor",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
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
                                color = if (isDark) Color(0xFF8E8394) else Color(0xFFA098A6)
                            )
                        }
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChanged,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    SearchIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs (All · 12, Active · 10, Inactive · 2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF1F1A24) else Color(0xFFF4ECF8))
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
                                .background(if (isSelected) (if (isDark) Color(0xFF28212F) else Color.White) else Color.Transparent)
                                .clickable { onFilterTabSelected(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) (if (isDark) Color(0xFFD4B3DC) else MomoraPurpleDark) else (if (isDark) Color(0xFF8E8394) else MomoraBodyMuted)
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
            }.filter { doc ->
                if (uiState.searchQuery.isBlank()) true
                else {
                    val q = uiState.searchQuery.trim()
                    doc.name.contains(q, ignoreCase = true) ||
                    (doc.email?.contains(q, ignoreCase = true) == true) ||
                    (doc.specialty?.contains(q, ignoreCase = true) == true) ||
                    (doc.licenseId?.contains(q, ignoreCase = true) == true) ||
                    (doc.clinic?.contains(q, ignoreCase = true) == true) ||
                    (doc.emailAndLicenseText?.contains(q, ignoreCase = true) == true) ||
                    doc.specialtyAndPatientsText.contains(q, ignoreCase = true)
                }
            }

            if (uiState.isLoading && uiState.doctors.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MomoraPurplePrimary)
                }
            } else if (uiState.errorMessage != null && uiState.doctors.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFF2DEDE), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatErrorMessage(uiState.errorMessage ?: "Failed to load doctors list"),
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
            } else if (filteredDoctors.isEmpty()) {
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
                                color = MomoraPurplePrimary,
                                modifier = Modifier.clickable { onRetryClick() }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
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
                        Text(
                            text = "No doctors found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (uiState.selectedFilterTab == 2) "No inactive doctors." else "Doctors added by the administrator will appear here.",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted,
                            textAlign = TextAlign.Center
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
                                color = MomoraPurplePrimary,
                                modifier = Modifier.clickable { onRetryClick() }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filteredDoctors.forEach { doctor ->
                        DoctorCardItem(
                            item = doctor,
                            onCardClick = { onCardClick(doctor) },
                            onEditClick = { onEditDetailsClick(doctor) },
                            onToggleStatusClick = { onToggleDoctorStatus(doctor) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Admin Bottom Navigation Bar (4 Admin Tabs)
        AdminBottomNavBar(
            selectedIndex = selectedNavIndex,
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
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onToggleStatusClick: () -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()
    val cardBg = if (isDark) Color(0xFF1F1A24) else Color.White
    val borderColor = if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(
                1.dp,
                borderColor,
                RoundedCornerShape(18.dp)
            )
            .clickable { onCardClick() }
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
                    val avatarBg = if (item.isActive) (if (isDark) Color(0xFF1E3A2B) else MomoraMintBadgeBg) else (if (isDark) Color(0xFF331A1E) else MomoraPinkCircleBg)

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(avatarBg),
                        contentAlignment = Alignment.Center
                    ) {
                        StethoscopeIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.name,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.specialtyAndPatientsText,
                            fontSize = 11.5.sp,
                            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (item.isActive) (if (isDark) Color(0xFF1E3A2B) else MomoraMintBadgeBg) else (if (isDark) Color(0xFF331A1E) else MomoraPinkCircleBg))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (item.isActive) "Active" else "Inactive",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isActive) (if (isDark) Color(0xFF86EFAC) else MomoraMintBadgeText) else (if (isDark) Color(0xFFFF8A80) else MomoraPurplePrimary)
                    )
                }
            }

            if (!item.emailAndLicenseText.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.emailAndLicenseText,
                    fontSize = 11.sp,
                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                )
            }

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
                        .background(if (isDark) Color(0xFF28212F) else Color.White)
                        .border(1.dp, if (isDark) Color(0xFF3D3245) else Color(0xFFEFE8F4), RoundedCornerShape(50))
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Edit details",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFD4B3DC) else MomoraPurpleDark
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (item.isActive) (if (isDark) Color(0xFF3D2125) else Color(0xFFFFF0F2)) else (if (isDark) Color(0xFF1E3A2B) else Color(0xFFF0FDF4)))
                        .border(
                            1.dp,
                            if (item.isActive) (if (isDark) Color(0xFF5A2A30) else Color(0xFFFFD8DD)) else (if (isDark) Color(0xFF2E5E43) else Color(0xFFBBF7D0)),
                            RoundedCornerShape(50)
                        )
                        .clickable { onToggleStatusClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (item.isActive) "Deactivate" else "Activate",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isActive) (if (isDark) Color(0xFFFF8A80) else Color(0xFFD32F2F)) else (if (isDark) Color(0xFF86EFAC) else Color(0xFF166534))
                    )
                }
            }
        }
    }
}

/**
 * Detailed view dialog of a doctor.
 */
@Composable
fun DoctorDetailsDialog(
    doctor: DoctorAccountItem,
    onDismiss: () -> Unit,
    onEditClick: (DoctorAccountItem) -> Unit,
    onToggleStatusClick: (DoctorAccountItem) -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()
    val dialogBg = if (isDark) Color(0xFF1F1A24) else Color.White
    val email = doctor.email ?: doctor.emailAndLicenseText?.substringBefore(" ·")?.trim() ?: "N/A"
    val license = doctor.licenseId ?: doctor.emailAndLicenseText?.substringAfter(" ·")?.trim() ?: "N/A"
    val specialty = doctor.specialty ?: doctor.specialtyAndPatientsText.substringBefore(" ·").trim()
    val clinic = doctor.clinic ?: "Willow Maternity Clinic"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(dialogBg)
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {}
                    .padding(22.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Doctor Details",
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
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFD4B3DC) else MomoraPurpleDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) Color(0xFF28212F) else Color(0xFFF9F5FB))
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (doctor.isActive) (if (isDark) Color(0xFF1E3A2B) else MomoraMintBadgeBg) else (if (isDark) Color(0xFF331A1E) else MomoraPinkCircleBg)),
                            contentAlignment = Alignment.Center
                        ) {
                            StethoscopeIcon(
                                color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = doctor.name,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Clinician · Doctor",
                                fontSize = 12.sp,
                                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (doctor.isActive) (if (isDark) Color(0xFF1E3A2B) else MomoraMintBadgeBg) else (if (isDark) Color(0xFF331A1E) else MomoraPinkCircleBg))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (doctor.isActive) "Active" else "Inactive",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (doctor.isActive) (if (isDark) Color(0xFF86EFAC) else MomoraMintBadgeText) else (if (isDark) Color(0xFFFF8A80) else MomoraPurplePrimary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) Color(0xFF231D29) else Color(0xFFFCFBFD))
                            .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFF0E8F4), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DoctorDetailItem(label = "Full Name", value = doctor.name, isDark = isDark)
                        DoctorDetailItem(label = "Email Address", value = email, isDark = isDark)
                        DoctorDetailItem(label = "Medical Specialty", value = specialty, isDark = isDark)
                        DoctorDetailItem(label = "License ID", value = license, isDark = isDark)
                        DoctorDetailItem(label = "Registered Clinic", value = clinic, isDark = isDark)
                        DoctorDetailItem(label = "Assigned Patients", value = "${doctor.assignedPatientCount} patients assigned", isDark = isDark)
                        DoctorDetailItem(label = "Account Status", value = if (doctor.isActive) "Active · Operational" else "Inactive · Access disabled", isDark = isDark)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isDark) Color(0xFF28212F) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF3D3245) else MomoraPurplePrimary, RoundedCornerShape(50))
                                .clickable { onEditClick(doctor) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Edit details",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (doctor.isActive) Color(0xFFD32F2F) else MomoraPurplePrimary)
                                .clickable { onToggleStatusClick(doctor) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (doctor.isActive) "Deactivate" else "Activate",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Confirmation dialog before deactivating/activating a doctor.
 */
@Composable
fun DoctorToggleConfirmDialog(
    doctor: DoctorAccountItem,
    isToggling: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()
    val dialogBg = if (isDark) Color(0xFF1F1A24) else Color.White
    val isDeactivate = doctor.isActive

    Dialog(
        onDismissRequest = { if (!isToggling) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = !isToggling) { onDismiss() }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(dialogBg)
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(22.dp))
                    .clickable(enabled = false) {}
                    .padding(22.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isDeactivate) (if (isDark) Color(0xFF3D2125) else Color(0xFFFFEBEE)) else (if (isDark) Color(0xFF1E3A2B) else Color(0xFFE8F5E9))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDeactivate) "!" else "✓",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDeactivate) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isDeactivate) "Deactivate Doctor Account?" else "Activate Doctor Account?",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isDeactivate)
                            "Are you sure you want to deactivate ${doctor.name}? Deactivation removes account access to the portal but preserves clinical records."
                        else
                            "Are you sure you want to activate ${doctor.name}? Clinical access and consultations will be restored.",
                        fontSize = 13.sp,
                        color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
                                .clickable(enabled = !isToggling) { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isDeactivate) Color(0xFFD32F2F) else MomoraPurplePrimary)
                                .clickable(enabled = !isToggling) { onConfirm() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isToggling) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = if (isDeactivate) "Deactivate" else "Activate",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog for editing doctor information.
 */
@Composable
fun DoctorEditDialog(
    uiState: ManageDoctorsUiState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onSpecialtyChanged: (String) -> Unit,
    onLicenseIdChanged: (String) -> Unit,
    onClinicChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()
    val dialogBg = if (isDark) Color(0xFF1F1A24) else Color.White
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = { if (!uiState.isUpdatingDoctor) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = !uiState.isUpdatingDoctor) { onDismiss() }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(dialogBg)
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(22.dp))
                    .clickable(enabled = false) {}
                    .padding(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Edit Doctor Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
                                .clickable(enabled = !uiState.isUpdatingDoctor) { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✕",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFD4B3DC) else MomoraPurpleDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!uiState.editErrorMessage.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFEBEE))
                                .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = uiState.editErrorMessage,
                                fontSize = 12.sp,
                                color = Color(0xFFC62828)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Field: Full Name
                    DoctorEditField(label = "Full Name", value = uiState.editNameInput, onValueChange = onNameChanged, isDark = isDark)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Field: Email
                    DoctorEditField(label = "Work Email", value = uiState.editEmailInput, onValueChange = onEmailChanged, keyboardType = KeyboardType.Email, isDark = isDark)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Field: Specialty
                    DoctorEditField(label = "Specialty", value = uiState.editSpecialtyInput, onValueChange = onSpecialtyChanged, isDark = isDark)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Field: License ID
                    DoctorEditField(label = "License ID", value = uiState.editLicenseIdInput, onValueChange = onLicenseIdChanged, isDark = isDark)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Field: Clinic
                    DoctorEditField(label = "Clinic", value = uiState.editClinicInput, onValueChange = onClinicChanged, isDark = isDark)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
                                .clickable(enabled = !uiState.isUpdatingDoctor) { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MomoraPurplePrimary)
                                .clickable(enabled = !uiState.isUpdatingDoctor) { onSave() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isUpdatingDoctor) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = "Save changes",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
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
private fun DoctorDetailItem(label: String, value: String, isDark: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isDark) Color(0xFF8E8394) else Color(0xFF8C7F92)
        )
        Text(
            text = value,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun DoctorEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isDark: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color(0xFF28212F) else Color.White)
                .border(1.dp, if (isDark) Color(0xFF3D3245) else Color(0xFFEFE8F4), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                ),
                modifier = Modifier.fillMaxWidth()
            )
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
    val isDark = ThemeManager.isDarkTheme()
    val navSurface = if (isDark) Color(0xFF1F1A24) else Color.White
    val navBorder = if (isDark) Color(0xFF332A3B) else Color(0xFFF0E8F4)
    val indicatorColor = if (isDark) Color(0xFF4A3857) else Color(0xFF333333)
    val primaryColor = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
    val mutedColor = if (isDark) Color(0xFF8E8394) else MomoraBodyMuted

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(navSurface)
            .border(1.dp, navBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
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
                icon = { OverviewNavIcon(color = if (selectedIndex == 0) primaryColor else mutedColor, modifier = Modifier.size(20.dp)) },
                isDark = isDark
            )
            NavTabItem(
                label = "Doctors",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { StethoscopeIcon(color = if (selectedIndex == 1) primaryColor else mutedColor, modifier = Modifier.size(20.dp)) },
                isDark = isDark
            )
            NavTabItem(
                label = "Patients",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { PeopleNavIcon(color = if (selectedIndex == 2) primaryColor else mutedColor, modifier = Modifier.size(20.dp)) },
                isDark = isDark
            )
            NavTabItem(
                label = "Settings",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { SettingsNavIcon(color = if (selectedIndex == 3) primaryColor else mutedColor, modifier = Modifier.size(20.dp)) },
                isDark = isDark
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

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
    icon: @Composable () -> Unit,
    isDark: Boolean = false
) {
    val activeColor = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
    val inactiveColor = if (isDark) Color(0xFF8E8394) else MomoraBodyMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) (if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8)) else Color.Transparent)
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
