package com.example.SIMATS.ui.adminsettings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.SIMATS.data.local.LocalNotificationHelper
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AdminAuditEvent
import com.example.SIMATS.data.model.AdminProfileData
import com.example.SIMATS.data.model.AdminSessionItem
import com.example.SIMATS.data.model.AdminSettingsData
import com.example.SIMATS.data.repository.NotificationStateManager
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.theme.InterFontFamily
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    viewModel: AdminSettingsViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 3,
    onNavigateBack: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoggedOutSuccess) {
        onLogoutSuccess()
    }

    AdminSettingsScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onChangePasswordClick = { viewModel.onEvent(AdminSettingsUiEvent.OnChangePasswordClicked) },
        onMfaClick = { viewModel.onEvent(AdminSettingsUiEvent.OnMfaClicked) },
        onSessionTimeoutClick = { viewModel.onEvent(AdminSettingsUiEvent.OnSessionTimeoutClicked) },
        onActiveSessionsClick = { viewModel.onEvent(AdminSettingsUiEvent.OnActiveSessionsClicked) },
        onAdminProfileClick = { viewModel.onEvent(AdminSettingsUiEvent.OnAdminProfileClicked) },
        onAccountAccessAuditClick = { viewModel.onEvent(AdminSettingsUiEvent.OnAccountAccessAuditClicked) },
        onLogoutClick = { viewModel.onEvent(AdminSettingsUiEvent.OnLogoutClicked) },
        onRetryClick = { viewModel.onEvent(AdminSettingsUiEvent.OnRetryClicked) },
        onBackClick = {
            viewModel.onEvent(AdminSettingsUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = {
            viewModel.onEvent(AdminSettingsUiEvent.OnNotificationClicked)
            onNotificationClick()
        },
        onNavTabSelected = { index ->
            viewModel.onEvent(AdminSettingsUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )

    // ModalBottomSheets for each interactive row
    if (uiState.activeSheet != AdminSettingsSheet.NONE) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val isDark = ThemeManager.isDarkTheme()

        ModalBottomSheet(
            onDismissRequest = { viewModel.onEvent(AdminSettingsUiEvent.OnDismissSheet) },
            sheetState = sheetState,
            containerColor = if (isDark) Color(0xFF1F1A24) else Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            when (uiState.activeSheet) {
                AdminSettingsSheet.CHANGE_PASSWORD -> {
                    ChangePasswordSheetContent(
                        uiState = uiState,
                        onCurrentPasswordChanged = { viewModel.onEvent(AdminSettingsUiEvent.OnCurrentPasswordChanged(it)) },
                        onNewPasswordChanged = { viewModel.onEvent(AdminSettingsUiEvent.OnNewPasswordChanged(it)) },
                        onConfirmPasswordChanged = { viewModel.onEvent(AdminSettingsUiEvent.OnConfirmPasswordChanged(it)) },
                        onToggleCurrentPasswordVisibility = { viewModel.onEvent(AdminSettingsUiEvent.OnToggleCurrentPasswordVisibility) },
                        onToggleNewPasswordVisibility = { viewModel.onEvent(AdminSettingsUiEvent.OnToggleNewPasswordVisibility) },
                        onToggleConfirmPasswordVisibility = { viewModel.onEvent(AdminSettingsUiEvent.OnToggleConfirmPasswordVisibility) },
                        onSubmit = { viewModel.onEvent(AdminSettingsUiEvent.OnSubmitChangePassword) },
                        onClose = { viewModel.onEvent(AdminSettingsUiEvent.OnDismissSheet) }
                    )
                }
                AdminSettingsSheet.SESSION_TIMEOUT -> {
                    SessionTimeoutSheetContent(
                        uiState = uiState,
                        onSelectMinutes = { viewModel.onEvent(AdminSettingsUiEvent.OnSelectTimeoutMinutes(it)) },
                        onSubmit = { viewModel.onEvent(AdminSettingsUiEvent.OnSubmitSessionTimeout) },
                        onClose = { viewModel.onEvent(AdminSettingsUiEvent.OnDismissSheet) }
                    )
                }
                AdminSettingsSheet.ACTIVE_SESSIONS -> {
                    ActiveSessionsSheetContent(
                        uiState = uiState,
                        onRevokeSession = { viewModel.onEvent(AdminSettingsUiEvent.OnRevokeSession(it)) },
                        onRetry = { viewModel.onEvent(AdminSettingsUiEvent.OnRefreshSessions) },
                        onClose = { viewModel.onEvent(AdminSettingsUiEvent.OnDismissSheet) }
                    )
                }
                AdminSettingsSheet.ADMIN_PROFILE -> {
                    AdminProfileSheetContent(
                        uiState = uiState,
                        onRetry = { viewModel.onEvent(AdminSettingsUiEvent.OnRefreshProfile) },
                        onClose = { viewModel.onEvent(AdminSettingsUiEvent.OnDismissSheet) }
                    )
                }
                AdminSettingsSheet.AUDIT_TRAIL -> {
                    AuditTrailSheetContent(
                        uiState = uiState,
                        onRetry = { viewModel.onEvent(AdminSettingsUiEvent.OnRefreshAudit) },
                        onClose = { viewModel.onEvent(AdminSettingsUiEvent.OnDismissSheet) }
                    )
                }
                AdminSettingsSheet.NONE -> {}
            }
        }
    }
}

@Composable
fun AdminSettingsScreenContent(
    modifier: Modifier = Modifier,
    uiState: AdminSettingsUiState = AdminSettingsUiState(
        settingsData = AdminSettingsData(
            adminName = "Alex Morgan",
            adminEmailText = "admin@willow.example · Administrator",
            passwordLastUpdatedText = "Last updated 29 Oct 2026",
            isMfaEnabled = false,
            mfaStatusText = "Multi-factor authentication · Not set up yet",
            sessionTimeoutText = "Automatically sign out after 15 minutes",
            activeSessionsText = "1 active session",
            appVersionText = "Momora AI · v1.0.0"
        )
    ),
    onChangePasswordClick: () -> Unit = {},
    onMfaClick: () -> Unit = {},
    onSessionTimeoutClick: () -> Unit = {},
    onActiveSessionsClick: () -> Unit = {},
    onAdminProfileClick: () -> Unit = {},
    onAccountAccessAuditClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    selectedNavIndex: Int = 3,
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.settingsData
    val isDark = ThemeManager.isDarkTheme()
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(SessionManager.isAdminNotificationsEnabled()) }
    var showPermissionDeniedMessage by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            notificationsEnabled = true
            SessionManager.setAdminNotificationsEnabled(true)
            showPermissionDeniedMessage = false
        } else {
            notificationsEnabled = false
            SessionManager.setAdminNotificationsEnabled(false)
            showPermissionDeniedMessage = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Bar: Back Arrow only (clean header without "momora AI / ADMIN / 30")
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
            // "Admin settings" Title (no subtitle, no pre-created admin card)
            Text(
                text = "Admin settings",
                fontFamily = InterFontFamily,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (uiState.isLoading && data == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
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
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatErrorMessage(uiState.errorMessage ?: "Failed to load admin settings"),
                            fontFamily = InterFontFamily,
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
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // "Notifications" Section
            Text(
                text = "Notifications",
                fontFamily = InterFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            BellIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Enable notifications",
                                fontFamily = InterFontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Alerts for new doctors, patients and account activity",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val isGranted = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (isGranted) {
                                        notificationsEnabled = true
                                        SessionManager.setAdminNotificationsEnabled(true)
                                        showPermissionDeniedMessage = false
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    notificationsEnabled = true
                                    SessionManager.setAdminNotificationsEnabled(true)
                                    showPermissionDeniedMessage = false
                                }
                            } else {
                                notificationsEnabled = false
                                SessionManager.setAdminNotificationsEnabled(false)
                                showPermissionDeniedMessage = false
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MomoraPurplePrimary,
                            uncheckedThumbColor = if (isDark) Color(0xFFB5ACB9) else Color.White,
                            uncheckedTrackColor = if (isDark) Color(0xFF332A3B) else Color(0xFFE0E0E0)
                        )
                    )
                }

                if (showPermissionDeniedMessage) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF331A1E) else Color(0xFFFFF0F2))
                            .border(1.dp, if (isDark) Color(0xFF5A2A30) else Color(0xFFFFD8DD), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Allow notifications in phone settings",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFFFF8A80) else Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MomoraPurplePrimary)
                                    .clickable { LocalNotificationHelper.openAppNotificationSettings(context) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Open settings",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "Security" Section
            Text(
                text = "Security",
                fontFamily = InterFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Security Items List Container Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                // Change password (Tappable -> ModalBottomSheet)
                SettingsRowItem(
                    title = "Change password",
                    subtitle = data?.passwordLastUpdatedText ?: "Password set",
                    icon = { KeyIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onChangePasswordClick
                )

                // Multi-factor authentication (simple row, not tappable, no "On" badge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        ShieldIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Multi-factor authentication · Not set up yet",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                    )
                }

                // Session timeout (Tappable -> ModalBottomSheet)
                SettingsRowItem(
                    title = "Session timeout",
                    subtitle = data?.sessionTimeoutText ?: "Automatically sign out after 15 minutes",
                    icon = { ClockIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onSessionTimeoutClick
                )

                // Active sessions (Tappable -> ModalBottomSheet)
                SettingsRowItem(
                    title = "Active sessions",
                    subtitle = data?.activeSessionsText ?: "1 active session",
                    icon = { DevicesIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onActiveSessionsClick
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "Administration" Section
            Text(
                text = "Administration",
                fontFamily = InterFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Administration Container Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                // Admin profile (Tappable -> ModalBottomSheet)
                SettingsRowItem(
                    title = "Admin profile",
                    subtitle = "View administrator account details.",
                    icon = { DocumentIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onAdminProfileClick
                )

                // Account access audit (Tappable -> ModalBottomSheet)
                SettingsRowItem(
                    title = "Account access audit",
                    subtitle = "Review account changes and sign-ins.",
                    icon = { DocumentIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onAccountAccessAuditClick
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Log out securely Primary Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { if (!uiState.isLoggingOut) onLogoutClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (uiState.isLoggingOut) "Logging out..." else "Log out securely",
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🚪",
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 3. Admin Bottom Navigation Bar
        AdminBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

/**
 * Change Password Bottom Sheet Content.
 */
@Composable
private fun ChangePasswordSheetContent(
    uiState: AdminSettingsUiState,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Change password",
                fontFamily = InterFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✕", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MomoraPurpleDark)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Enter your current password and choose a new password with at least 8 characters.",
            fontFamily = InterFontFamily,
            fontSize = 12.5.sp,
            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Success / Error Feedback
        if (!uiState.changePasswordSuccess.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEBF7F0))
                    .border(1.dp, Color(0xFFD4EDDA), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = uiState.changePasswordSuccess,
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E6B47)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (!uiState.changePasswordError.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFEBEE))
                    .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = uiState.changePasswordError,
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFC62828)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Current Password Field
        PasswordFieldItem(
            label = "Current password",
            value = uiState.currentPasswordInput,
            onValueChange = onCurrentPasswordChanged,
            placeholder = "Enter current password",
            isVisible = uiState.isCurrentPasswordVisible,
            onToggleVisibility = onToggleCurrentPasswordVisibility,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        // New Password Field
        PasswordFieldItem(
            label = "New password",
            value = uiState.newPasswordInput,
            onValueChange = onNewPasswordChanged,
            placeholder = "At least 8 characters",
            isVisible = uiState.isNewPasswordVisible,
            onToggleVisibility = onToggleNewPasswordVisibility,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password Field
        PasswordFieldItem(
            label = "Confirm new password",
            value = uiState.confirmPasswordInput,
            onValueChange = onConfirmPasswordChanged,
            placeholder = "Re-enter new password",
            isVisible = uiState.isConfirmPasswordVisible,
            onToggleVisibility = onToggleConfirmPasswordVisibility,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Save Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(50))
                .background(MomoraPurplePrimary)
                .clickable(enabled = !uiState.isChangingPassword) { onSubmit() },
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isChangingPassword) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    text = "Save password",
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PasswordFieldItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    isDark: Boolean
) {
    Column {
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color(0xFF28212F) else Color(0xFFFAF7FC))
                .border(1.dp, if (isDark) Color(0xFF3D3245) else Color(0xFFEFE8F4), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = if (isDark) Color(0xFF8E8394) else Color(0xFFA098A6)
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        textStyle = TextStyle(
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleText
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onToggleVisibility() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isVisible) {
                        EyeIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                    } else {
                        EyeSlashIcon(color = if (isDark) Color(0xFF8E8394) else Color(0xFFA098A6), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

/**
 * Session Timeout Bottom Sheet Content.
 */
@Composable
private fun SessionTimeoutSheetContent(
    uiState: AdminSettingsUiState,
    onSelectMinutes: (Int) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()
    val options = listOf(
        5 to "5 minutes · Strict security",
        15 to "15 minutes · Recommended",
        30 to "30 minutes · Standard administrative session",
        60 to "60 minutes · Extended session"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Session timeout",
                fontFamily = InterFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✕", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MomoraPurpleDark)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Select how long an administrator session remains active before automatically signing out.",
            fontFamily = InterFontFamily,
            fontSize = 12.5.sp,
            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!uiState.timeoutSuccess.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEBF7F0))
                    .border(1.dp, Color(0xFFD4EDDA), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = uiState.timeoutSuccess,
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E6B47)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (!uiState.timeoutError.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFEBEE))
                    .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = uiState.timeoutError,
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFC62828)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        options.forEach { (mins, label) ->
            val isSelected = uiState.selectedTimeoutMinutes == mins
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) (if (isDark) Color(0xFF2E2436) else Color(0xFFF4ECF8)) else (if (isDark) Color(0xFF28212F) else Color(0xFFFAF7FC)))
                    .border(
                        1.dp,
                        if (isSelected) MomoraPurplePrimary else (if (isDark) Color(0xFF3D3245) else Color(0xFFEFE8F4)),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectMinutes(mins) }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$mins minutes",
                            fontFamily = InterFontFamily,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            fontFamily = InterFontFamily,
                            fontSize = 11.5.sp,
                            color = if (isSelected) (if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary) else MomoraBodyMuted
                        )
                    }

                    // Radio indicator
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSelected) MomoraPurplePrimary else Color(0xFFB5ACB9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(MomoraPurplePrimary)
                            )
                        }
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
                .clickable(enabled = !uiState.isUpdatingTimeout) { onSubmit() },
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isUpdatingTimeout) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    text = "Save timeout",
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Active Sessions Bottom Sheet Content.
 */
@Composable
private fun ActiveSessionsSheetContent(
    uiState: AdminSettingsUiState,
    onRevokeSession: (Int) -> Unit,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Active sessions",
                fontFamily = InterFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✕", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MomoraPurpleDark)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Active devices and browser sessions authenticated to your administrator account.",
            fontFamily = InterFontFamily,
            fontSize = 12.5.sp,
            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (!uiState.sessionsSuccess.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEBF7F0))
                    .border(1.dp, Color(0xFFD4EDDA), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = uiState.sessionsSuccess,
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E6B47)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (!uiState.sessionsError.isNullOrBlank()) {
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
                        text = uiState.sessionsError,
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFFC62828),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Retry",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.clickable { onRetry() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (uiState.isLoadingSessions && uiState.sessionsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MomoraPurplePrimary)
            }
        } else if (uiState.sessionsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active sessions found",
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    color = MomoraBodyMuted
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.sessionsList.forEach { session ->
                    SessionCardItem(
                        session = session,
                        isRevoking = uiState.revokingSessionId == session.id,
                        onRevoke = { onRevokeSession(session.id) },
                        isDark = isDark
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionCardItem(
    session: AdminSessionItem,
    isRevoking: Boolean,
    onRevoke: () -> Unit,
    isDark: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isDark) Color(0xFF28212F) else Color.White)
            .border(1.dp, if (isDark) Color(0xFF3D3245) else Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (session.isCurrent) MomoraMintBadgeBg else (if (isDark) Color(0xFF332A3B) else Color(0xFFF4ECF8))),
                    contentAlignment = Alignment.Center
                ) {
                    DevicesIcon(
                        color = if (session.isCurrent) MomoraMintBadgeText else (if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = session.deviceInfo ?: "Unknown Device",
                        fontFamily = InterFontFamily,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Created: ${session.createdAt}",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (session.isCurrent) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MomoraMintBadgeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Current session",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraMintBadgeText
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isDark) Color(0xFF3D2125) else Color(0xFFFFF0F2))
                        .border(1.dp, if (isDark) Color(0xFF5A2A30) else Color(0xFFFFD8DD), RoundedCornerShape(50))
                        .clickable(enabled = !isRevoking) { onRevoke() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRevoking) {
                        CircularProgressIndicator(color = Color(0xFFD32F2F), modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = "Sign out",
                            fontFamily = InterFontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFFF8A80) else Color(0xFFD32F2F)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Admin Profile Bottom Sheet Content.
 */
@Composable
private fun AdminProfileSheetContent(
    uiState: AdminSettingsUiState,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()
    val prof = uiState.profileData

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Admin profile",
                fontFamily = InterFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✕", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MomoraPurpleDark)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Administrator credentials and identity details.",
            fontFamily = InterFontFamily,
            fontSize = 12.5.sp,
            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!uiState.profileError.isNullOrBlank()) {
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
                        text = uiState.profileError,
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFFC62828),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Retry",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.clickable { onRetry() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (uiState.isLoadingProfile && prof == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MomoraPurplePrimary)
            }
        } else {
            val name = prof?.name ?: uiState.settingsData?.adminName ?: "Alex Morgan"
            val email = prof?.email ?: uiState.settingsData?.adminEmailText?.substringBefore(" ·") ?: "admin@willow.example"
            val role = (prof?.role ?: "admin").replaceFirstChar { it.uppercase() }
            val created = prof?.createdAt ?: "29 Oct 2026"
            val status = if (prof?.isActive == 1) "Active · Normal access" else "Inactive"

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFFAF7FC))
                    .border(1.dp, if (isDark) Color(0xFF3D3245) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileDetailRow(label = "Name", value = name, isDark = isDark)
                ProfileDetailRow(label = "Email", value = email, isDark = isDark)
                ProfileDetailRow(label = "Role", value = role, isDark = isDark)
                ProfileDetailRow(label = "Created date", value = created, isDark = isDark)
                ProfileDetailRow(label = "Status", value = status, isDark = isDark)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Close",
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String, isDark: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontSize = 12.5.sp,
            color = if (isDark) Color(0xFF8E8394) else MomoraBodyMuted
        )
        Text(
            text = value,
            fontFamily = InterFontFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
        )
    }
}

/**
 * Account Access Audit Bottom Sheet Content.
 */
@Composable
private fun AuditTrailSheetContent(
    uiState: AdminSettingsUiState,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Account access audit",
                fontFamily = InterFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✕", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MomoraPurpleDark)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Latest 30 security events: user sign-ins and account activation changes.",
            fontFamily = InterFontFamily,
            fontSize = 12.5.sp,
            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (!uiState.auditError.isNullOrBlank()) {
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
                        text = uiState.auditError,
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFFC62828),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Retry",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.clickable { onRetry() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (uiState.isLoadingAudit && uiState.auditList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MomoraPurplePrimary)
            }
        } else if (uiState.auditList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No audit events recorded",
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    color = MomoraBodyMuted
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.auditList.forEach { event ->
                    AuditEventCardItem(event = event, isDark = isDark)
                }
            }
        }
    }
}

@Composable
private fun AuditEventCardItem(
    event: AdminAuditEvent,
    isDark: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDark) Color(0xFF28212F) else Color(0xFFFAF7FC))
            .border(1.dp, if (isDark) Color(0xFF3D3245) else Color(0xFFEFE8F4), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isSignIn = event.type == "SIGN_IN"
            val badgeColor = if (isSignIn) (if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary) else Color(0xFF2E6B47)
            val badgeBg = if (isSignIn) (if (isDark) Color(0xFF3D2E45) else Color(0xFFF4ECF8)) else Color(0xFFEBF7F0)

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSignIn) "🔑" else "🛡️",
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.description,
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = event.time,
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                )
            }
        }
    }
}

@Composable
private fun SettingsRowItem(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()
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
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontFamily = InterFontFamily,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = InterFontFamily,
                    fontSize = 11.5.sp,
                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                )
            }
        }

        Text(
            text = "›",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) Color(0xFF8E8394) else Color(0xFFA098A6)
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
            fontFamily = InterFontFamily,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}

@Composable
private fun KeyIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.25f,
            center = Offset(w * 0.35f, h * 0.50f),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = color,
            start = Offset(w * 0.60f, h * 0.50f),
            end = Offset(w * 0.90f, h * 0.50f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.80f, h * 0.50f),
            end = Offset(w * 0.80f, h * 0.68f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.90f, h * 0.50f),
            end = Offset(w * 0.90f, h * 0.68f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
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
private fun ClockIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(color = color, radius = w * 0.40f, center = Offset(w * 0.50f, h * 0.50f), style = Stroke(width = strokeWidth))
        drawLine(
            color = color,
            start = Offset(w * 0.50f, h * 0.50f),
            end = Offset(w * 0.50f, h * 0.25f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.50f, h * 0.50f),
            end = Offset(w * 0.70f, h * 0.50f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun DevicesIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Desktop screen
        drawRect(
            color = color,
            topLeft = Offset(w * 0.10f, h * 0.18f),
            size = Size(w * 0.55f, h * 0.42f),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = color,
            start = Offset(w * 0.37f, h * 0.60f),
            end = Offset(w * 0.37f, h * 0.74f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = Offset(w * 0.25f, h * 0.74f),
            end = Offset(w * 0.50f, h * 0.74f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Mobile device
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.60f, h * 0.35f),
            size = Size(w * 0.30f, h * 0.50f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
private fun DocumentIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.20f, h * 0.15f),
            size = Size(w * 0.60f, h * 0.70f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = color,
            start = Offset(w * 0.35f, h * 0.35f),
            end = Offset(w * 0.65f, h * 0.35f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.35f, h * 0.50f),
            end = Offset(w * 0.65f, h * 0.50f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.35f, h * 0.65f),
            end = Offset(w * 0.55f, h * 0.65f),
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
private fun EyeIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val eyePath = Path().apply {
            moveTo(w * 0.15f, h * 0.50f)
            cubicTo(w * 0.30f, h * 0.25f, w * 0.70f, h * 0.25f, w * 0.85f, h * 0.50f)
            cubicTo(w * 0.70f, h * 0.75f, w * 0.30f, h * 0.75f, w * 0.15f, h * 0.50f)
        }
        drawPath(eyePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        drawCircle(color = color, radius = w * 0.12f, center = Offset(w * 0.50f, h * 0.50f))
    }
}

@Composable
private fun EyeSlashIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val eyePath = Path().apply {
            moveTo(w * 0.15f, h * 0.50f)
            cubicTo(w * 0.30f, h * 0.25f, w * 0.70f, h * 0.25f, w * 0.85f, h * 0.50f)
            cubicTo(w * 0.70f, h * 0.75f, w * 0.30f, h * 0.75f, w * 0.15f, h * 0.50f)
        }
        drawPath(eyePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        drawCircle(color = color, radius = w * 0.12f, center = Offset(w * 0.50f, h * 0.50f))

        val slashPath = Path().apply {
            moveTo(w * 0.20f, h * 0.20f)
            lineTo(w * 0.80f, h * 0.80f)
        }
        drawPath(slashPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
private fun OverviewNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRect(color = color, topLeft = Offset(w * 0.15f, h * 0.15f), size = Size(w * 0.30f, h * 0.30f), style = Stroke(width = strokeWidth))
        drawRect(color = color, topLeft = Offset(w * 0.55f, h * 0.15f), size = Size(w * 0.30f, h * 0.30f), style = Stroke(width = strokeWidth))
        drawRect(color = color, topLeft = Offset(w * 0.15f, h * 0.55f), size = Size(w * 0.30f, h * 0.30f), style = Stroke(width = strokeWidth))
        drawRect(color = color, topLeft = Offset(w * 0.55f, h * 0.55f), size = Size(w * 0.30f, h * 0.30f), style = Stroke(width = strokeWidth))
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

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun AdminSettingsScreenPreview() {
    AdminSettingsScreenContent()
}
