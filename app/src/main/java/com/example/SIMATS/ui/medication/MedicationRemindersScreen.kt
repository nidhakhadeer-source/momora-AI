package com.example.SIMATS.ui.medication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.local.MedicationNotificationScheduler
import com.example.SIMATS.data.model.MedicationHistoryItem
import com.example.SIMATS.data.model.MedicationItem
import com.example.SIMATS.data.model.MedicationReminderData
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MedicationRemindersScreen(
    viewModel: MedicationRemindersViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 3,
    onNavigateBack: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onEvent(MedicationRemindersUiEvent.OnPermissionGranted)
        } else {
            viewModel.onEvent(MedicationRemindersUiEvent.OnPermissionDenied)
        }
    }

    MedicationRemindersScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onTabSelected = { index -> viewModel.onEvent(MedicationRemindersUiEvent.OnTabSelected(index)) },
        onScheduleDaySelected = { index -> viewModel.onEvent(MedicationRemindersUiEvent.OnScheduleDaySelected(index)) },
        onMarkAsTakenClick = { id -> viewModel.onEvent(MedicationRemindersUiEvent.OnMarkAsTakenClicked(id)) },
        onAddMedicationClick = { viewModel.onEvent(MedicationRemindersUiEvent.OnAddMedicationClicked) },
        onEditMedicationClick = { item -> viewModel.onEvent(MedicationRemindersUiEvent.OnEditMedicationClicked(item)) },
        onDiscontinueMedicationClick = { item -> viewModel.onEvent(MedicationRemindersUiEvent.OnDiscontinueMedicationClicked(item)) },
        onConfirmDiscontinue = { viewModel.onEvent(MedicationRemindersUiEvent.OnConfirmDiscontinue) },
        onDismissDiscontinueDialog = { viewModel.onEvent(MedicationRemindersUiEvent.OnDismissDiscontinueDialog) },
        onSaveMedication = { id, name, dosage, freq, time, instructions, start, end, doctor ->
            viewModel.onEvent(
                MedicationRemindersUiEvent.OnSaveMedication(
                    medicationId = id,
                    name = name,
                    dosage = dosage,
                    frequency = freq,
                    reminderTime = time,
                    instructions = instructions,
                    startDate = start,
                    endDate = end,
                    doctorName = doctor
                )
            )
        },
        onDismissAddEditDialog = { viewModel.onEvent(MedicationRemindersUiEvent.OnDismissAddEditDialog) },
        onNotificationClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !MedicationNotificationScheduler.isNotificationPermissionGranted(context)
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.onEvent(MedicationRemindersUiEvent.OnNotificationClicked)
            }
        },
        onToggleNotifications = { enabled ->
            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !MedicationNotificationScheduler.isNotificationPermissionGranted(context)
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.onEvent(MedicationRemindersUiEvent.OnToggleNotifications(enabled))
            }
        },
        onSendTestNotification = {
            val sampleMed = uiState.data?.medications?.firstOrNull() ?: MedicationItem(
                id = 999,
                name = "Prenatal Vitamin",
                dosageAndTime = "1 tablet · Scheduled Reminder",
                reminderTime = "Now",
                doctorPrescriptionNote = "As prescribed by your healthcare provider",
                isTaken = false
            )
            MedicationNotificationScheduler.showImmediateNotification(context, sampleMed)
        },
        onDismissNotificationDialog = { viewModel.onEvent(MedicationRemindersUiEvent.OnDismissNotificationDialog) },
        onDismissPermissionRationale = { viewModel.onEvent(MedicationRemindersUiEvent.OnDismissPermissionRationale) },
        onOpenAppSettings = {
            try {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        },
        onRetryClick = { viewModel.onEvent(MedicationRemindersUiEvent.OnRetryClicked) },
        onClearUserMessage = { viewModel.onEvent(MedicationRemindersUiEvent.OnClearUserMessage) },
        onBackClick = {
            viewModel.onEvent(MedicationRemindersUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNavTabSelected = { index ->
            viewModel.onEvent(MedicationRemindersUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun MedicationRemindersScreenContent(
    modifier: Modifier = Modifier,
    uiState: MedicationRemindersUiState = MedicationRemindersUiState(
        data = MedicationReminderData(
            dateText = "Tuesday, 27 Oct 2026",
            progressStatusText = "1 OF 2 TAKEN · 1 REMAINING TODAY",
            medications = listOf(
                MedicationItem(
                    id = 1,
                    name = "Prenatal vitamin",
                    dosageAndTime = "1 tablet · Daily at 08:00 · With food",
                    isTaken = true,
                    takenTimeNote = "Marked taken today at 08:05"
                ),
                MedicationItem(
                    id = 2,
                    name = "Vitamin D · 400 IU",
                    dosageAndTime = "1 tablet · Daily at 20:00",
                    doctorPrescriptionNote = "As prescribed by Dr. Amelia Chen.",
                    isTaken = false,
                    dueLaterText = "Due later"
                )
            )
        )
    ),
    onTabSelected: (Int) -> Unit = {},
    onScheduleDaySelected: (Int) -> Unit = {},
    onMarkAsTakenClick: (Int) -> Unit = {},
    onAddMedicationClick: () -> Unit = {},
    onEditMedicationClick: (MedicationItem) -> Unit = {},
    onDiscontinueMedicationClick: (MedicationItem) -> Unit = {},
    onConfirmDiscontinue: () -> Unit = {},
    onDismissDiscontinueDialog: () -> Unit = {},
    onSaveMedication: (Int?, String, String, String, String, String?, String?, String?, String?) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onDismissAddEditDialog: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onToggleNotifications: (Boolean) -> Unit = {},
    onSendTestNotification: () -> Unit = {},
    onDismissNotificationDialog: () -> Unit = {},
    onDismissPermissionRationale: () -> Unit = {},
    onOpenAppSettings: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onClearUserMessage: () -> Unit = {},
    selectedNavIndex: Int = 3,
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.data

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

            // Notification Bell with status indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isNotificationsEnabled) Color(0xFFF4ECF8) else Color.Transparent)
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                BellIcon(
                    color = if (uiState.isNotificationsEnabled) MomoraPurplePrimary else MomoraBodyMuted,
                    modifier = Modifier.size(20.dp)
                )

                if (uiState.isNotificationsEnabled) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .align(Alignment.TopEnd)
                            .padding(end = 6.dp, top = 6.dp)
                            .clip(CircleShape)
                            .background(MomoraMintBadgeText)
                    )
                }
            }
        }

        // 2. Body
        if (uiState.isLoading && data == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MomoraPurplePrimary)
            }
        } else if (uiState.errorMessage != null && data == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Unable to load medication reminders",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatErrorMessage(uiState.errorMessage ?: "Please check your network and try again."),
                        fontSize = 13.sp,
                        color = Color(0xFFD32F2F),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MomoraPurplePrimary)
                            .clickable { onRetryClick() }
                            .padding(horizontal = 24.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Retry",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // Scrollable Body Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "A little daily routine",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = data?.dateText ?: "Tuesday, 27 Oct 2026",
                    fontSize = 13.sp,
                    color = MomoraBodyMuted
                )

                // User Feedback Notification Message Banner
                if (!uiState.userActionMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3E5F5))
                            .border(1.dp, Color(0xFFE1BEE7), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.userActionMessage ?: "",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MomoraPurpleDark,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "✕",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurplePrimary,
                                modifier = Modifier
                                    .clickable { onClearUserMessage() }
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Segmented Tab Switcher (Today, Schedule, History)
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
                        val tabs = listOf("Today", "Schedule", "History")
                        tabs.forEachIndexed { index, label ->
                            val isSelected = uiState.selectedTab == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color.White else Color.Transparent)
                                    .clickable { onTabSelected(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MomoraPurpleDark else MomoraBodyMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (uiState.selectedTab) {
                    0 -> {
                        // TAB 0: TODAY
                        val activeMeds = data?.medications ?: emptyList()
                        val takenCount = activeMeds.count { it.isTaken }
                        val totalCount = activeMeds.size
                        val remainingCount = (totalCount - takenCount).coerceAtLeast(0)
                        val progressText = if (totalCount == 0) {
                            "NO MEDICATIONS SCHEDULED"
                        } else if (remainingCount == 0) {
                            "$totalCount OF $totalCount TAKEN · ALL COMPLETED TODAY"
                        } else {
                            "$takenCount OF $totalCount TAKEN · $remainingCount REMAINING TODAY"
                        }

                        // Progress Status Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MomoraMintBadgeBg)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = progressText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraMintBadgeText,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Today's schedule",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (activeMeds.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "No medications scheduled",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MomoraPurpleDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "You have no active medication reminders for today. Tap below to add a prescription.",
                                        fontSize = 12.sp,
                                        color = MomoraBodyMuted,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                activeMeds.forEach { item ->
                                    MedicationCard(
                                        item = item,
                                        onMarkAsTakenClick = { onMarkAsTakenClick(item.id) },
                                        onEditClick = { onEditMedicationClick(item) },
                                        onDiscontinueClick = { onDiscontinueMedicationClick(item) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Add prescribed medication Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MomoraPurplePrimary)
                                .clickable { onAddMedicationClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Add prescribed medication",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "+",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // NOTE: Unwanted bottom medical disclaimer card has been completely removed.
                    }
                    1 -> {
                        // TAB 1: SCHEDULE
                        val cal = Calendar.getInstance()
                        val scheduleDays = (0..6).map { offset ->
                            val dayCal = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, offset) }
                            val dayOfWeek = SimpleDateFormat("EEE", Locale.US).format(dayCal.time)
                            val dayOfMonth = dayCal.get(Calendar.DAY_OF_MONTH)
                            val fullDate = SimpleDateFormat("EEEE, d MMMM", Locale.US).format(dayCal.time)
                            Triple(dayOfWeek, dayOfMonth, fullDate)
                        }

                        val selectedDayInfo = scheduleDays.getOrElse(uiState.selectedScheduleDayIndex) { scheduleDays[0] }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            scheduleDays.forEachIndexed { idx, (dow, dom, _) ->
                                val isSelected = uiState.selectedScheduleDayIndex == idx
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) MomoraPurplePrimary else Color.White)
                                        .border(1.dp, if (isSelected) MomoraPurplePrimary else Color(0xFFEFE8F4), RoundedCornerShape(12.dp))
                                        .clickable { onScheduleDaySelected(idx) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = dow,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White.copy(alpha = 0.85f) else MomoraBodyMuted
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "$dom",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MomoraPurpleDark
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (uiState.selectedScheduleDayIndex == 0) "Today's Schedule" else "Schedule for ${selectedDayInfo.third}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Prescribed daily medications, dosage times & instructions",
                            fontSize = 12.sp,
                            color = MomoraBodyMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val scheduledMeds = data?.medications ?: emptyList()
                        if (scheduledMeds.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No scheduled medications for this date",
                                    fontSize = 13.sp,
                                    color = MomoraBodyMuted
                                )
                            }
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                scheduledMeds.forEach { item ->
                                    ScheduleMedicationCard(
                                        item = item,
                                        onEditClick = { onEditMedicationClick(item) },
                                        onDiscontinueClick = { onDiscontinueMedicationClick(item) }
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        // TAB 2: HISTORY (Persistent adherence log)
                        Text(
                            text = "Medication Activity & History",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Recorded doses, adherence events and discontinuation records",
                            fontSize = 12.sp,
                            color = MomoraBodyMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val historyLogs = data?.history ?: emptyList()
                        if (historyLogs.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "No medication history recorded yet",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MomoraPurpleDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Your recorded doses, timestamps and adherence history will appear here.",
                                        fontSize = 12.sp,
                                        color = MomoraBodyMuted,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                historyLogs.forEach { logItem ->
                                    HistoryLogCard(item = logItem)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // 3. Bottom Dashboard Navigation Bar
        DashboardBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }

    // DIALOGS:
    // 1. Add / Edit Prescribed Medication Dialog
    if (uiState.showAddEditDialog) {
        AddEditMedicationDialog(
            initialItem = uiState.medicationToEdit,
            isSubmitting = uiState.isSubmittingAction,
            onDismiss = onDismissAddEditDialog,
            onSave = onSaveMedication
        )
    }

    // 2. Discontinue Medication Confirmation Dialog
    if (uiState.showDiscontinueConfirmDialog && uiState.medicationToDiscontinue != null) {
        DiscontinueMedicationDialog(
            medication = uiState.medicationToDiscontinue!!,
            isSubmitting = uiState.isSubmittingAction,
            onConfirm = onConfirmDiscontinue,
            onDismiss = onDismissDiscontinueDialog
        )
    }

    // 3. Notification Settings Dialog
    if (uiState.showNotificationToggleDialog) {
        NotificationSettingsDialog(
            isEnabled = uiState.isNotificationsEnabled,
            onToggle = onToggleNotifications,
            onSendTest = onSendTestNotification,
            onOpenSettings = onOpenAppSettings,
            onDismiss = onDismissNotificationDialog
        )
    }

    // 4. Permission Rationale Dialog
    if (uiState.showPermissionRationaleDialog) {
        PermissionRationaleDialog(
            onOpenSettings = onOpenAppSettings,
            onDismiss = onDismissPermissionRationale
        )
    }
}

/**
 * Single Medication Card Composable for Today's Schedule.
 */
@Composable
private fun MedicationCard(
    item: MedicationItem,
    onMarkAsTakenClick: () -> Unit,
    onEditClick: () -> Unit,
    onDiscontinueClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (item.isTaken) Color.White else Color(0xFFF4ECF8))
            .border(1.dp, if (item.isTaken) Color(0xFFEFE8F4) else Color.Transparent, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (item.isTaken) MomoraMintBadgeBg else Color(0xFFEDE4F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        PillCapsuleIcon(
                            color = if (item.isTaken) MomoraMintBadgeText else MomoraPurplePrimary,
                            modifier = Modifier.size(16.dp)
                        )
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
                            text = item.dosageAndTime,
                            fontSize = 11.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }

                if (item.isTaken) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MomoraMintBadgeBg)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Taken",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraMintBadgeText
                        )
                    }
                } else if (!item.dueLaterText.isNullOrEmpty()) {
                    Text(
                        text = item.dueLaterText,
                        fontSize = 11.5.sp,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            if (!item.takenTimeNote.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.takenTimeNote,
                    fontSize = 11.sp,
                    color = MomoraMintBadgeText,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 48.dp)
                )
            }

            if (!item.doctorPrescriptionNote.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.doctorPrescriptionNote,
                    fontSize = 11.5.sp,
                    color = MomoraBodyMuted,
                    modifier = Modifier.padding(start = 48.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Mark as Taken or Edit/Discontinue options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!item.isTaken) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(50))
                            .clickable { onMarkAsTakenClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mark as taken",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.8f))
                        .border(1.dp, Color(0xFFE2D6EC), RoundedCornerShape(50))
                        .clickable { onEditClick() }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Edit",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MomoraPurplePrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.8f))
                        .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(50))
                        .clickable { onDiscontinueClick() }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Discontinue",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

/**
 * Schedule Medication Card with Edit and Discontinue actions.
 */
@Composable
private fun ScheduleMedicationCard(
    item: MedicationItem,
    onEditClick: () -> Unit,
    onDiscontinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
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
                            .background(Color(0xFFEDE4F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        PillCapsuleIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp))
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
                            text = item.dosageAndTime,
                            fontSize = 12.sp,
                            color = MomoraPurplePrimary,
                            fontWeight = FontWeight.Medium
                        )
                        if (!item.doctorPrescriptionNote.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.doctorPrescriptionNote,
                                fontSize = 11.sp,
                                color = MomoraBodyMuted
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFF4ECF8))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Scheduled",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurplePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Edit & Discontinue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFF8F5FB))
                        .border(1.dp, Color(0xFFE2D6EC), RoundedCornerShape(50))
                        .clickable { onEditClick() }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Edit schedule",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MomoraPurplePrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFFFEBEE))
                        .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(50))
                        .clickable { onDiscontinueClick() }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Discontinue",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

/**
 * Persistent History Log Card Composable.
 */
@Composable
private fun HistoryLogCard(
    item: MedicationHistoryItem,
    modifier: Modifier = Modifier
) {
    val isTaken = item.action.equals("TAKEN", ignoreCase = true)
    val isDiscontinued = item.action.equals("DISCONTINUED", ignoreCase = true)
    val isMissed = item.action.equals("MISSED", ignoreCase = true)

    val badgeBg = when {
        isTaken -> MomoraMintBadgeBg
        isDiscontinued -> Color(0xFFFFEBEE)
        isMissed -> Color(0xFFFDF4E7)
        else -> Color(0xFFEDE8F4)
    }

    val badgeTextColor = when {
        isTaken -> MomoraMintBadgeText
        isDiscontinued -> Color(0xFFC62828)
        isMissed -> Color(0xFFB86A1D)
        else -> MomoraPurplePrimary
    }

    val badgeLabel = when {
        isTaken -> "✓ Taken"
        isDiscontinued -> "✕ Discontinued"
        isMissed -> "⚠ Missed"
        else -> item.action
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
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
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    PillCapsuleIcon(
                        color = badgeTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.medicationName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                    if (!item.dosageAndTime.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.dosageAndTime,
                            fontSize = 11.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatHistoryTimestamp(item.actionTime),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = badgeTextColor
                    )
                    if (!item.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.notes,
                            fontSize = 10.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(badgeBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor
                )
            }
        }
    }
}

/**
 * Format database timestamp string into user-friendly date and time.
 */
private fun formatHistoryTimestamp(raw: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val date = parser.parse(raw)
        if (date != null) {
            SimpleDateFormat("d MMM yyyy, hh:mm a", Locale.US).format(date)
        } else {
            raw
        }
    } catch (_: Exception) {
        raw
    }
}

/**
 * Dialog to Add or Edit Prescribed Medication.
 */
@Composable
private fun AddEditMedicationDialog(
    initialItem: MedicationItem?,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSave: (id: Int?, name: String, dosage: String, frequency: String, reminderTime: String, instructions: String?, startDate: String?, endDate: String?, doctorName: String?) -> Unit
) {
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var dosage by remember { mutableStateOf(initialItem?.dosage ?: "") }
    var frequency by remember { mutableStateOf(initialItem?.frequency ?: "Once daily") }
    var reminderTime by remember { mutableStateOf(initialItem?.reminderTime ?: "08:00") }
    var instructions by remember { mutableStateOf(initialItem?.instructions ?: "") }
    var doctorName by remember { mutableStateOf(initialItem?.doctorName ?: "") }

    var nameError by remember { mutableStateOf(false) }
    var dosageError by remember { mutableStateOf(false) }
    var timeError by remember { mutableStateOf(false) }

    val presetTimes = listOf(
        "08:00" to "Morning 08:00",
        "13:00" to "Noon 13:00",
        "20:00" to "Evening 20:00",
        "22:00" to "Night 22:00"
    )

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = {
            Text(
                text = if (initialItem != null) "Edit Prescribed Medication" else "Add Prescribed Medication",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Medication Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Medication Name *") },
                    placeholder = { Text("e.g. Prenatal Multivitamin") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MomoraPurplePrimary,
                        unfocusedBorderColor = Color(0xFFD4C8DC)
                    )
                )
                if (nameError) {
                    Text(text = "Medication name is required", color = Color.Red, fontSize = 11.sp)
                }

                // Dosage
                OutlinedTextField(
                    value = dosage,
                    onValueChange = {
                        dosage = it
                        dosageError = false
                    },
                    label = { Text("Prescribed Dosage *") },
                    placeholder = { Text("e.g. 1 tablet (400 mcg)") },
                    isError = dosageError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MomoraPurplePrimary,
                        unfocusedBorderColor = Color(0xFFD4C8DC)
                    )
                )
                if (dosageError) {
                    Text(text = "Prescribed dosage is required", color = Color.Red, fontSize = 11.sp)
                }

                // Frequency
                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Frequency *") },
                    placeholder = { Text("e.g. Once daily, Twice daily") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MomoraPurplePrimary,
                        unfocusedBorderColor = Color(0xFFD4C8DC)
                    )
                )

                // Reminder Time
                OutlinedTextField(
                    value = reminderTime,
                    onValueChange = {
                        reminderTime = it
                        timeError = false
                    },
                    label = { Text("Reminder Time (HH:mm) *") },
                    placeholder = { Text("e.g. 08:00 or 20:00") },
                    isError = timeError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MomoraPurplePrimary,
                        unfocusedBorderColor = Color(0xFFD4C8DC)
                    )
                )
                if (timeError) {
                    Text(text = "Enter a valid time in HH:mm format (e.g. 08:00)", color = Color.Red, fontSize = 11.sp)
                }

                // Quick preset chips
                Text(
                    text = "Quick reminder presets:",
                    fontSize = 11.sp,
                    color = MomoraBodyMuted,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetTimes.forEach { (timeVal, label) ->
                        val isSelected = reminderTime == timeVal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MomoraPurplePrimary else Color(0xFFF4ECF8))
                                .clickable { reminderTime = timeVal }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = timeVal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MomoraPurpleDark
                            )
                        }
                    }
                }

                // Instructions
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions (optional)") },
                    placeholder = { Text("e.g. With food, after lunch") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MomoraPurplePrimary,
                        unfocusedBorderColor = Color(0xFFD4C8DC)
                    )
                )

                // Prescribing Doctor
                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = { Text("Prescribing Doctor (optional)") },
                    placeholder = { Text("e.g. Dr. Amelia Chen") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MomoraPurplePrimary,
                        unfocusedBorderColor = Color(0xFFD4C8DC)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val validName = name.isNotBlank()
                    val validDosage = dosage.isNotBlank()
                    val validTime = reminderTime.matches(Regex("""^([01]?[0-9]|2[0-3]):[0-5][0-9]$"""))

                    nameError = !validName
                    dosageError = !validDosage
                    timeError = !validTime

                    if (validName && validDosage && validTime) {
                        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                        onSave(
                            initialItem?.id,
                            name.trim(),
                            dosage.trim(),
                            frequency.trim().ifBlank { "Once daily" },
                            reminderTime.trim(),
                            instructions.trim().ifBlank { null },
                            initialItem?.startDate ?: todayStr,
                            initialItem?.endDate,
                            doctorName.trim().ifBlank { null }
                        )
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = MomoraPurplePrimary)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(if (initialItem != null) "Update" else "Save")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text("Cancel", color = MomoraPurpleDark)
            }
        }
    )
}

/**
 * Confirmation Dialog for Discontinuing a Prescribed Medication.
 */
@Composable
private fun DiscontinueMedicationDialog(
    medication: MedicationItem,
    isSubmitting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = {
            Text(
                text = "Discontinue Medication?",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )
        },
        text = {
            Text(
                text = "Are you sure you want to discontinue ${medication.name}? Future reminders will be stopped immediately and this discontinuation will be recorded in your adherence history.",
                fontSize = 13.5.sp,
                color = MomoraPurpleDark,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text("Discontinue", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text("Keep Medication", color = MomoraPurpleDark)
            }
        }
    )
}

/**
 * Notification Settings Dialog.
 */
@Composable
private fun NotificationSettingsDialog(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onSendTest: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Medication Notifications",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Dose Reminders",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Receive alarms at each medication's scheduled time",
                            fontSize = 11.5.sp,
                            color = MomoraBodyMuted
                        )
                    }

                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { onToggle(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MomoraPurplePrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCCCCCC)
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF4ECF8))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Channel: Prescribed Medication Reminders",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurplePrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Uses high-priority local device alarms with zero external server dependencies.",
                            fontSize = 10.5.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onSendTest,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Send Test Alert", fontSize = 11.5.sp, color = MomoraPurplePrimary)
                    }

                    OutlinedButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("App Settings", fontSize = 11.5.sp, color = MomoraPurpleDark)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MomoraPurplePrimary)
            ) {
                Text("Done")
            }
        }
    )
}

/**
 * Permission Rationale Dialog when POST_NOTIFICATIONS is denied.
 */
@Composable
private fun PermissionRationaleDialog(
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Enable Medication Alerts",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )
        },
        text = {
            Text(
                text = "Momora AI requires notification permission to remind you when it is time to take your prescribed pregnancy medications. Please enable notifications in your device settings so you do not miss scheduled doses.",
                fontSize = 13.5.sp,
                color = MomoraPurpleDark,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onOpenSettings()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MomoraPurplePrimary)
            ) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now", color = MomoraPurpleDark)
            }
        }
    )
}

/**
 * Bottom Navigation Bar for Medication Screen.
 */
@Composable
private fun DashboardBottomNavBar(
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
                label = "Track",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { TrackNavIcon(color = if (selectedIndex == 1) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "AI",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { SparklesIcon(color = if (selectedIndex == 2) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Care",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { CareNavIcon(color = if (selectedIndex == 3) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Profile",
                isSelected = selectedIndex == 4,
                onClick = { onTabSelected(4) },
                icon = { UserNavIcon(color = if (selectedIndex == 4) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
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
private fun TrackNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val pulsePath = Path().apply {
            moveTo(w * 0.10f, h * 0.50f)
            lineTo(w * 0.30f, h * 0.50f)
            lineTo(w * 0.40f, h * 0.25f)
            lineTo(w * 0.55f, h * 0.75f)
            lineTo(w * 0.70f, h * 0.40f)
            lineTo(w * 0.80f, h * 0.50f)
            lineTo(w * 0.90f, h * 0.50f)
        }

        drawPath(pulsePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun CareNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val heartPath = Path().apply {
            moveTo(w * 0.50f, h * 0.82f)
            cubicTo(w * 0.20f, h * 0.62f, w * 0.05f, h * 0.42f, w * 0.15f, h * 0.24f)
            cubicTo(w * 0.25f, h * 0.10f, w * 0.45f, h * 0.14f, w * 0.50f, h * 0.28f)
            cubicTo(w * 0.55f, h * 0.14f, w * 0.75f, h * 0.10f, w * 0.85f, h * 0.24f)
            cubicTo(w * 0.95f, h * 0.42f, w * 0.80f, h * 0.62f, w * 0.50f, h * 0.82f)
        }

        drawPath(heartPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
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
private fun SparklesIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val starPath = Path().apply {
            moveTo(w * 0.50f, h * 0.15f)
            cubicTo(w * 0.50f, h * 0.35f, w * 0.65f, h * 0.50f, w * 0.85f, h * 0.50f)
            cubicTo(w * 0.65f, h * 0.50f, w * 0.50f, h * 0.65f, w * 0.50f, h * 0.85f)
            cubicTo(w * 0.50f, h * 0.65f, w * 0.35f, h * 0.50f, w * 0.15f, h * 0.50f)
            cubicTo(w * 0.35f, h * 0.50f, w * 0.50f, h * 0.35f, w * 0.50f, h * 0.15f)
            close()
        }

        drawPath(starPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun MedicationRemindersScreenPreview() {
    MedicationRemindersScreenContent()
}
