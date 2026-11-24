package com.example.SIMATS.ui.adminnotifications

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.NotificationItem
import com.example.SIMATS.data.model.NotificationType
import com.example.SIMATS.ui.components.formatDoctorTime
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.notifications.NotificationsUiEvent
import com.example.SIMATS.ui.notifications.NotificationsUiState
import com.example.SIMATS.ui.notifications.NotificationsViewModel
import com.example.SIMATS.ui.theme.InterFontFamily
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.ThemeManager

@Composable
fun AdminNotificationsScreen(
    viewModel: NotificationsViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 0,
    onNavigateBack: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    AdminNotificationsScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onFilterTabSelected = { index -> viewModel.onEvent(NotificationsUiEvent.OnFilterTabSelected(index)) },
        onMarkAllReadClick = { viewModel.onEvent(NotificationsUiEvent.OnMarkAllReadClicked) },
        onNotificationItemClick = { id -> viewModel.onEvent(NotificationsUiEvent.OnNotificationItemClicked(id)) },
        onRetryClick = { viewModel.onEvent(NotificationsUiEvent.OnRetryClicked) },
        onBackClick = onNavigateBack,
        onNavTabSelected = onNavTabSelected
    )
}

@Composable
fun AdminNotificationsScreenContent(
    modifier: Modifier = Modifier,
    uiState: NotificationsUiState = NotificationsUiState(),
    selectedNavIndex: Int = 0,
    onFilterTabSelected: (Int) -> Unit = {},
    onMarkAllReadClick: () -> Unit = {},
    onNotificationItemClick: (String) -> Unit = {},
    onRetryClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val isDark = ThemeManager.isDarkTheme()
    val scrollState = rememberScrollState()

    val displayNotifications = when (uiState.selectedFilterTab) {
        1 -> uiState.notifications.filter { !it.isRead }
        else -> uiState.notifications
    }

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
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8))
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Notifications",
                    fontFamily = InterFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                )
                Text(
                    text = "Alerts & system activity",
                    fontFamily = InterFontFamily,
                    fontSize = 11.5.sp,
                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8)),
                contentAlignment = Alignment.Center
            ) {
                BellIcon(
                    color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
                if (uiState.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935))
                    )
                }
            }
        }

        // 2. Body Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Filter Tabs & Mark All Read
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AdminFilterChip(
                        label = "All (${uiState.notifications.size})",
                        isSelected = uiState.selectedFilterTab == 0,
                        onClick = { onFilterTabSelected(0) },
                        isDark = isDark
                    )
                    AdminFilterChip(
                        label = "Unread (${uiState.unreadCount})",
                        isSelected = uiState.selectedFilterTab == 1,
                        onClick = { onFilterTabSelected(1) },
                        isDark = isDark
                    )
                }

                if (uiState.unreadCount > 0) {
                    Text(
                        text = "Mark all read",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onMarkAllReadClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.isLoading && uiState.notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MomoraPurplePrimary)
                }
            } else if (uiState.errorMessage != null && uiState.notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                        .border(1.dp, Color(0xFFF2DEDE), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatErrorMessage(uiState.errorMessage ?: "Failed to load notifications"),
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFFA94442),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MomoraPurplePrimary)
                                .clickable { onRetryClick() }
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Retry",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else if (displayNotifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                        .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF28212F) else Color(0xFFF4ECF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            BellIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (uiState.selectedFilterTab == 1) "No unread notifications" else "No notifications yet",
                            fontFamily = InterFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "New alerts regarding doctors, patients and account access will appear here.",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    displayNotifications.forEach { item ->
                        AdminNotificationCard(
                            item = item,
                            onClick = { onNotificationItemClick(item.id) },
                            isDark = isDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 3. Admin Bottom Navigation Bar (4 Admin Tabs)
        AdminBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

@Composable
private fun AdminFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (isSelected) MomoraPurplePrimary
                else (if (isDark) Color(0xFF1F1A24) else Color.White)
            )
            .border(
                1.dp,
                if (isSelected) MomoraPurplePrimary
                else (if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4)),
                RoundedCornerShape(50)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else (if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark)
        )
    }
}

@Composable
private fun AdminNotificationCard(
    item: NotificationItem,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val cardBg = if (isDark) Color(0xFF1F1A24) else Color.White
    val borderColor = if (!item.isRead) MomoraPurplePrimary.copy(alpha = 0.4f)
    else (if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            val iconBg = if (!item.isRead) (if (isDark) Color(0xFF2E1A33) else Color(0xFFF4ECF8))
            else (if (isDark) Color(0xFF28212F) else Color(0xFFF9F6FA))

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                BellIcon(color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontFamily = InterFontFamily,
                        fontSize = 13.5.sp,
                        fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark,
                        modifier = Modifier.weight(1f)
                    )

                    if (!item.isRead) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(MomoraPurplePrimary)
                        )
                    }
                }

                if (item.subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.subtitle,
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                    )
                }

                if (item.detailText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.detailText,
                        fontFamily = InterFontFamily,
                        fontSize = 11.5.sp,
                        color = if (isDark) Color(0xFF9E92A4) else Color(0xFF7E7284)
                    )
                }

                if (!item.createdAt.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.createdAt.formatDoctorTime(),
                        fontFamily = InterFontFamily,
                        fontSize = 10.5.sp,
                        color = if (isDark) Color(0xFF8E8394) else Color(0xFFA098A6)
                    )
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
    val isDark = ThemeManager.isDarkTheme()
    val navBg = if (isDark) Color(0xFF1F1A24) else Color.White
    val topBorderColor = if (isDark) Color(0xFF332A3B) else Color(0xFFEFE8F4)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(navBg)
            .border(1.dp, topBorderColor)
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdminBottomNavItem(
                icon = { OverviewNavIcon(color = if (selectedIndex == 0) MomoraPurplePrimary else (if (isDark) Color(0xFF8E8394) else MomoraBodyMuted), modifier = Modifier.size(20.dp)) },
                label = "Overview",
                isSelected = selectedIndex == 0,
                onClick = { onTabSelected(0) }
            )

            AdminBottomNavItem(
                icon = { StethoscopeNavIcon(color = if (selectedIndex == 1) MomoraPurplePrimary else (if (isDark) Color(0xFF8E8394) else MomoraBodyMuted), modifier = Modifier.size(20.dp)) },
                label = "Doctors",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) }
            )

            AdminBottomNavItem(
                icon = { PeopleNavIcon(color = if (selectedIndex == 2) MomoraPurplePrimary else (if (isDark) Color(0xFF8E8394) else MomoraBodyMuted), modifier = Modifier.size(20.dp)) },
                label = "Patients",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) }
            )

            AdminBottomNavItem(
                icon = { SettingsNavIcon(color = if (selectedIndex == 3) MomoraPurplePrimary else (if (isDark) Color(0xFF8E8394) else MomoraBodyMuted), modifier = Modifier.size(20.dp)) },
                label = "Settings",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
private fun AdminBottomNavItem(
    icon: @Composable () -> Unit,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 2.dp)
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
            fontFamily = InterFontFamily,
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
private fun StethoscopeNavIcon(color: Color, modifier: Modifier = Modifier) {
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
