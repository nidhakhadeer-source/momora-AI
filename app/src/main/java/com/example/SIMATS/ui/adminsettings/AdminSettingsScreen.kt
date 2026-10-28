package com.example.SIMATS.ui.adminsettings

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
import com.example.SIMATS.data.model.AdminSettingsData
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun AdminSettingsScreen(
    viewModel: AdminSettingsViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
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
        onChangePasswordClick = { viewModel.onEvent(AdminSettingsUiEvent.OnChangePasswordClicked) },
        onMfaClick = { viewModel.onEvent(AdminSettingsUiEvent.OnMfaClicked) },
        onSessionTimeoutClick = { viewModel.onEvent(AdminSettingsUiEvent.OnSessionTimeoutClicked) },
        onActiveSessionsClick = { viewModel.onEvent(AdminSettingsUiEvent.OnActiveSessionsClicked) },
        onAccountAccessAuditClick = { viewModel.onEvent(AdminSettingsUiEvent.OnAccountAccessAuditClicked) },
        onLogoutClick = { viewModel.onEvent(AdminSettingsUiEvent.OnLogoutClicked) },
        onBackClick = {
            viewModel.onEvent(AdminSettingsUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(AdminSettingsUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(AdminSettingsUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun AdminSettingsScreenContent(
    modifier: Modifier = Modifier,
    uiState: AdminSettingsUiState = AdminSettingsUiState(
        settingsData = AdminSettingsData(
            adminName = "Alex Morgan",
            adminEmailText = "admin@willow.example · Administrator",
            passwordLastUpdatedText = "Last updated 01 Oct 2026",
            isMfaEnabled = true,
            mfaStatusText = "Authenticator app connected",
            sessionTimeoutText = "Automatically sign out after 15 minutes",
            activeSessionsText = "1 device · This Android device",
            appVersionText = "Momora AI · v1.0.0 · Ends your session on this device."
        )
    ),
    onChangePasswordClick: () -> Unit = {},
    onMfaClick: () -> Unit = {},
    onSessionTimeoutClick: () -> Unit = {},
    onActiveSessionsClick: () -> Unit = {},
    onAccountAccessAuditClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.settingsData

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
            Row(
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

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "momora AI",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                    Text(
                        text = "ADMIN / 30",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraBodyMuted,
                        letterSpacing = 0.5.sp
                    )
                }
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
                text = "Admin settings",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Security and account preferences",
                fontSize = 13.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Pre-created Admin Account Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF4ECF8))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "PRE-CREATED ADMIN ACCOUNT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraBodyMuted,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = data?.adminName ?: "Alex Morgan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = data?.adminEmailText ?: "admin@willow.example · Administrator",
                        fontSize = 11.5.sp,
                        color = MomoraPurplePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Security" Section
            Text(
                text = "Security",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Security Items List Container Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                // Change password
                SettingsRowItem(
                    title = "Change password",
                    subtitle = data?.passwordLastUpdatedText ?: "Last updated 01 Oct 2026",
                    icon = { KeyIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onChangePasswordClick
                )

                // Multi-factor authentication
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMfaClick() }
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
                                .background(MomoraMintBadgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            ShieldIcon(color = MomoraMintBadgeText, modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Multi-factor authentication",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurpleText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = data?.mfaStatusText ?: "Authenticator app connected",
                                fontSize = 11.5.sp,
                                color = MomoraBodyMuted
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MomoraMintBadgeBg)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "On",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraMintBadgeText
                        )
                    }
                }

                // Session timeout
                SettingsRowItem(
                    title = "Session timeout",
                    subtitle = data?.sessionTimeoutText ?: "Automatically sign out after 15 minutes",
                    icon = { ClockIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onSessionTimeoutClick
                )

                // Active sessions
                SettingsRowItem(
                    title = "Active sessions",
                    subtitle = data?.activeSessionsText ?: "1 device · This Android device",
                    icon = { DevicesIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onActiveSessionsClick
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "Administration" Section
            Text(
                text = "Administration",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Administration Container Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                SettingsRowItem(
                    title = "Account access audit",
                    subtitle = "Review account changes and sign-ins.",
                    icon = { DocumentIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp)) },
                    onClick = onAccountAccessAuditClick
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.isLoggingOut) "Logging out..." else "Log out securely",
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = data?.appVersionText ?: "Momora AI · v1.0.0 · Ends your session on this device.",
                fontSize = 11.sp,
                color = MomoraFooterText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Admin Bottom Navigation Bar
        AdminBottomNavBar(
            selectedIndex = uiState.selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

@Composable
private fun SettingsRowItem(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
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
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE4F0)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
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
private fun KeyIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(color = color, radius = w * 0.20f, center = Offset(w * 0.35f, h * 0.35f), style = Stroke(width = strokeWidth))
        drawLine(color = color, start = Offset(w * 0.50f, h * 0.50f), end = Offset(w * 0.82f, h * 0.82f), strokeWidth = strokeWidth)
        drawLine(color = color, start = Offset(w * 0.65f, h * 0.65f), end = Offset(w * 0.75f, h * 0.55f), strokeWidth = strokeWidth)
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

        drawCircle(color = color, radius = w * 0.38f, center = Offset(w * 0.50f, h * 0.50f), style = Stroke(width = strokeWidth))
        drawLine(color = color, start = Offset(w * 0.50f, h * 0.50f), end = Offset(w * 0.50f, h * 0.25f), strokeWidth = strokeWidth)
        drawLine(color = color, start = Offset(w * 0.50f, h * 0.50f), end = Offset(w * 0.70f, h * 0.50f), strokeWidth = strokeWidth)
    }
}

@Composable
private fun DevicesIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRect(color = color, topLeft = Offset(w * 0.15f, h * 0.20f), size = Size(w * 0.50f, h * 0.60f), style = Stroke(width = strokeWidth))
        drawRect(color = color, topLeft = Offset(w * 0.55f, h * 0.40f), size = androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.40f), style = Stroke(width = strokeWidth))
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
private fun OverviewNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRect(color = color, topLeft = Offset(w * 0.15f, h * 0.15f), size = androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.30f), style = Stroke(width = strokeWidth))
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

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun AdminSettingsScreenPreview() {
    AdminSettingsScreenContent()
}
