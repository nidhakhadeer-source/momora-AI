package com.example.SIMATS.ui.adddoctor

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.SIMATS.data.model.AddDoctorData
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.ThemeManager
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.example.SIMATS.data.repository.NotificationStateManager
import com.example.SIMATS.ui.admindashboard.AdminNotificationsDialog
import kotlinx.coroutines.delay

@Composable
fun AddDoctorScreen(
    viewModel: AddDoctorViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 1,
    onNavigateBack: () -> Unit = {},
    onDoctorCreated: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Reset previous submission state whenever Add Doctor screen is opened
    LaunchedEffect(Unit) {
        viewModel.onEvent(AddDoctorUiEvent.OnResetForm)
    }

    // Only trigger navigation when doctor creation has freshly succeeded
    LaunchedEffect(uiState.isCreatedSuccess) {
        if (uiState.isCreatedSuccess) {
            delay(1200)
            viewModel.onEvent(AddDoctorUiEvent.OnSuccessNavigationConsumed)
            onDoctorCreated()
        }
    }

    AddDoctorScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onNameChanged = { n -> viewModel.onEvent(AddDoctorUiEvent.OnNameChanged(n)) },
        onEmailChanged = { e -> viewModel.onEvent(AddDoctorUiEvent.OnEmailChanged(e)) },
        onSpecialtyChanged = { s -> viewModel.onEvent(AddDoctorUiEvent.OnSpecialtyChanged(s)) },
        onLicenseIdChanged = { l -> viewModel.onEvent(AddDoctorUiEvent.OnLicenseIdChanged(l)) },
        onClinicChanged = { c -> viewModel.onEvent(AddDoctorUiEvent.OnClinicChanged(c)) },
        onClinicDropdownToggled = { exp -> viewModel.onEvent(AddDoctorUiEvent.OnClinicDropdownToggled(exp)) },
        onSendActivationToggled = { a -> viewModel.onEvent(AddDoctorUiEvent.OnSendActivationToggled(a)) },
        onCreateAccountClick = { viewModel.onEvent(AddDoctorUiEvent.OnCreateAccountClicked) },
        onRetryClick = { viewModel.onEvent(AddDoctorUiEvent.OnRetryClicked) },
        onBackClick = {
            viewModel.onEvent(AddDoctorUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = {
            viewModel.onEvent(AddDoctorUiEvent.OnNotificationClicked)
            onNotificationClick()
        },
        onNavTabSelected = { index ->
            viewModel.onEvent(AddDoctorUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )

    // Notifications Dialog
    if (uiState.showNotificationsDialog) {
        AdminNotificationsDialog(
            notifications = uiState.notificationsList,
            isLoading = uiState.isLoadingNotifications,
            onDismiss = { viewModel.onEvent(AddDoctorUiEvent.OnDismissNotificationsDialog) }
        )
    }
}

@Composable
fun AddDoctorScreenContent(
    modifier: Modifier = Modifier,
    uiState: AddDoctorUiState = AddDoctorUiState(),
    onNameChanged: (String) -> Unit = {},
    onEmailChanged: (String) -> Unit = {},
    onSpecialtyChanged: (String) -> Unit = {},
    onLicenseIdChanged: (String) -> Unit = {},
    onClinicChanged: (String) -> Unit = {},
    onClinicDropdownToggled: (Boolean) -> Unit = {},
    onSendActivationToggled: (Boolean) -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
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
                text = "Add a doctor",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Admin Only Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "ADMIN-ONLY ACCOUNT CREATION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.isLoading && uiState.formData == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MomoraPurplePrimary)
                }
            } else if (uiState.errorMessage != null && uiState.formData == null) {
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
                            text = formatErrorMessage(uiState.errorMessage ?: "Failed to load doctor form defaults"),
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
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Doctor's Full Name Field
            Text(
                text = "Doctor's full name",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                if (uiState.nameInput.isEmpty()) {
                    Text(
                        text = "e.g. Dr Sarah Jenkins",
                        fontSize = 13.sp,
                        color = if (isDark) Color(0xFF7D7382) else Color(0xFFA098A6)
                    )
                }
                BasicTextField(
                    value = uiState.nameInput,
                    onValueChange = onNameChanged,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Work Email Field
            Text(
                text = "Work email",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.emailInput.isEmpty()) {
                            Text(
                                text = "e.g. s.jenkins@clinic.example",
                                fontSize = 13.sp,
                                color = if (isDark) Color(0xFF7D7382) else Color(0xFFA098A6)
                            )
                        }
                        BasicTextField(
                            value = uiState.emailInput,
                            onValueChange = onEmailChanged,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    MailIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specialty & License ID Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Specialty
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Specialty",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                            .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        if (uiState.specialtyInput.isEmpty()) {
                            Text(
                                text = "e.g. Obstetrics",
                                fontSize = 13.sp,
                                color = if (isDark) Color(0xFF7D7382) else Color(0xFFA098A6)
                            )
                        }
                        BasicTextField(
                            value = uiState.specialtyInput,
                            onValueChange = onSpecialtyChanged,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // License ID
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "License ID",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                            .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        if (uiState.licenseIdInput.isEmpty()) {
                            Text(
                                text = "e.g. OBS-12345",
                                fontSize = 13.sp,
                                color = if (isDark) Color(0xFF7D7382) else Color(0xFFA098A6)
                            )
                        }
                        BasicTextField(
                            value = uiState.licenseIdInput,
                            onValueChange = onLicenseIdChanged,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Clinic Dropdown Field
            Text(
                text = "Clinic",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                        .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
                        .clickable { onClinicDropdownToggled(!uiState.isClinicDropdownExpanded) }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.clinicInput.ifBlank { "Select clinic" },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (uiState.clinicInput.isBlank()) MomoraBodyMuted else (if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark)
                        )

                        Text(
                            text = if (uiState.isClinicDropdownExpanded) "∧" else "∨",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                        )
                    }
                }

                DropdownMenu(
                    expanded = uiState.isClinicDropdownExpanded,
                    onDismissRequest = { onClinicDropdownToggled(false) },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                ) {
                    val clinics = uiState.formData?.availableClinics ?: emptyList()
                    if (clinics.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "No registered clinics available",
                                    fontSize = 13.sp,
                                    color = MomoraBodyMuted
                                )
                            },
                            onClick = { onClinicDropdownToggled(false) }
                        )
                    } else {
                        clinics.forEach { clinicName ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = clinicName,
                                        fontSize = 13.sp,
                                        fontWeight = if (clinicName == uiState.clinicInput) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                                    )
                                },
                                onClick = {
                                    onClinicChanged(clinicName)
                                    onClinicDropdownToggled(false)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Error Message Alert Box
            if (!uiState.errorMessage.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFDF2F2))
                        .border(1.dp, Color(0xFFF2DEDE), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        fontSize = 12.5.sp,
                        color = Color(0xFFA94442),
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Success Message Alert Box
            if (!uiState.successMessage.isNullOrBlank() || uiState.isCreatedSuccess) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEBF7F0))
                        .border(1.dp, Color(0xFFD4EDDA), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = uiState.successMessage ?: "Doctor account created successfully",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E6B47),
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Create account & send invitation Primary Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (uiState.isCreatedSuccess) Color(0xFF3D7B59)
                        else if (uiState.isCreating) MomoraPurplePrimary.copy(alpha = 0.7f)
                        else MomoraPurplePrimary
                    )
                    .clickable(enabled = !uiState.isCreating && !uiState.isCreatedSuccess) {
                        onCreateAccountClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.isCreating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Creating account...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else if (uiState.isCreatedSuccess) {
                        Text(
                            text = "Account Created ✓",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Create account & send invitation",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        MailIcon(color = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Admin Bottom Navigation Bar
        AdminBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
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
private fun MailIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRect(color = color, style = Stroke(width = strokeWidth))
        val flapPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * 0.50f, h * 0.50f)
            lineTo(w, 0f)
        }
        drawPath(flapPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
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
fun AddDoctorScreenPreview() {
    AddDoctorScreenContent()
}
