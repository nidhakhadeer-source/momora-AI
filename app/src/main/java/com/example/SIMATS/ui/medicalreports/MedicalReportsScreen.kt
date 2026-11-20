package com.example.SIMATS.ui.medicalreports

import android.content.Context
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.MedicalReportItem
import com.example.SIMATS.data.model.ReportCategory
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraLavenderCircleBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun MedicalReportsScreen(
    viewModel: MedicalReportsViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 3,
    onNavigateBack: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.onEvent(MedicalReportsUiEvent.OnFilePicked(uri, context))
        }
    }

    MedicalReportsScreenContent(
        modifier = modifier,
        uiState = uiState,
        onCategorySelected = { cat -> viewModel.onEvent(MedicalReportsUiEvent.OnCategorySelected(cat)) },
        onReportCardClick = { report -> viewModel.onEvent(MedicalReportsUiEvent.OnReportCardClicked(report)) },
        onOpenReport = { report -> viewModel.onEvent(MedicalReportsUiEvent.OnOpenReport(report, context)) },
        onShareReport = { report -> viewModel.onEvent(MedicalReportsUiEvent.OnShareReport(report, context)) },
        onChooseFileClick = {
            documentPickerLauncher.launch(
                arrayOf(
                    "application/pdf",
                    "image/jpeg",
                    "image/png",
                    "image/jpg"
                )
            )
        },
        onUploadTitleChanged = { title -> viewModel.onEvent(MedicalReportsUiEvent.OnUploadTitleChanged(title)) },
        onUploadCategoryChanged = { cat -> viewModel.onEvent(MedicalReportsUiEvent.OnUploadCategoryChanged(cat)) },
        onUploadDescriptionChanged = { desc -> viewModel.onEvent(MedicalReportsUiEvent.OnUploadDescriptionChanged(desc)) },
        onConfirmUpload = { viewModel.onEvent(MedicalReportsUiEvent.OnConfirmUpload(context)) },
        onDismissUploadDialog = { viewModel.onEvent(MedicalReportsUiEvent.OnDismissUploadDialog) },
        onDismissReportDetailSheet = { viewModel.onEvent(MedicalReportsUiEvent.OnDismissReportDetailSheet) },
        onDismissMessage = { viewModel.onEvent(MedicalReportsUiEvent.OnDismissMessage) },
        onRefreshClick = { viewModel.onEvent(MedicalReportsUiEvent.OnRefresh) },
        onRetryClick = { viewModel.onEvent(MedicalReportsUiEvent.OnRetryClicked) },
        onBackClick = {
            viewModel.onEvent(MedicalReportsUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(MedicalReportsUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(MedicalReportsUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        },
        selectedNavIndex = selectedNavIndex
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalReportsScreenContent(
    modifier: Modifier = Modifier,
    uiState: MedicalReportsUiState = MedicalReportsUiState(),
    onCategorySelected: (ReportCategory) -> Unit = {},
    onReportCardClick: (MedicalReportItem) -> Unit = {},
    onOpenReport: (MedicalReportItem) -> Unit = {},
    onShareReport: (MedicalReportItem) -> Unit = {},
    onChooseFileClick: () -> Unit = {},
    onUploadTitleChanged: (String) -> Unit = {},
    onUploadCategoryChanged: (ReportCategory) -> Unit = {},
    onUploadDescriptionChanged: (String) -> Unit = {},
    onConfirmUpload: () -> Unit = {},
    onDismissUploadDialog: () -> Unit = {},
    onDismissReportDetailSheet: () -> Unit = {},
    onDismissMessage: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    selectedNavIndex: Int = 3,
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFEFE8F4), CircleShape)
                        .clickable(enabled = !uiState.isRefreshing && !uiState.isLoading) { onRefreshClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isRefreshing) {
                        CircularProgressIndicator(
                            color = MomoraPurplePrimary,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        RefreshIcon(
                            color = MomoraPurplePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFEFE8F4), CircleShape)
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BellIcon(
                        color = MomoraPurplePrimary,
                        modifier = Modifier.size(18.dp)
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
                text = "Your medical reports",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Keep your care information in one private place.",
                fontSize = 13.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Success Message Banner
            if (!uiState.successMessage.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MomoraMintBadgeBg)
                        .border(1.dp, Color(0xFFB5E2D5), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
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
                            Text(
                                text = "✓",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraMintBadgeText
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.successMessage,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MomoraMintBadgeText
                            )
                        }
                        Text(
                            text = "✕",
                            fontSize = 14.sp,
                            color = MomoraMintBadgeText,
                            modifier = Modifier
                                .clickable { onDismissMessage() }
                                .padding(4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // General Error Banner
            if (!uiState.errorMessage.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFDE8E8))
                        .border(1.dp, Color(0xFFF8B4B4), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatErrorMessage(uiState.errorMessage),
                            fontSize = 12.5.sp,
                            color = Color(0xFF9B1C1C),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✕",
                            fontSize = 14.sp,
                            color = Color(0xFF9B1C1C),
                            modifier = Modifier
                                .clickable { onDismissMessage() }
                                .padding(4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Upload Card with Dashed Border
            DashedUploadCard(
                isUploading = uiState.isUploading,
                onChooseFileClick = onChooseFileClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter Tabs
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
                    val categories = listOf(
                        ReportCategory.ALL to "All reports",
                        ReportCategory.ULTRASOUND to "Ultrasound",
                        ReportCategory.LAB_RESULTS to "Lab results"
                    )

                    categories.forEach { (category, label) ->
                        val isSelected = uiState.selectedCategory == category
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { onCategorySelected(category) },
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

            Spacer(modifier = Modifier.height(18.dp))

            // "Recent uploads" Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent uploads",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (uiState.isRefreshing) {
                        CircularProgressIndicator(
                            color = MomoraPurplePrimary,
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 1.5.dp
                        )
                        Text(
                            text = "Refreshing…",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MomoraPurplePrimary
                        )
                    } else {
                        Text(
                            text = "${uiState.reports.size} reports",
                            fontSize = 12.sp,
                            color = MomoraBodyMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reports List Card Container
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    if (uiState.isLoading && uiState.reports.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MomoraPurplePrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    } else if (uiState.errorMessage != null && uiState.reports.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = formatErrorMessage(uiState.errorMessage),
                                    fontSize = 13.5.sp,
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
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    } else {
                        val filteredReports = when (uiState.selectedCategory) {
                            ReportCategory.ALL -> uiState.reports
                            ReportCategory.ULTRASOUND -> uiState.reports.filter { it.category == ReportCategory.ULTRASOUND }
                            ReportCategory.LAB_RESULTS -> uiState.reports.filter { it.category == ReportCategory.LAB_RESULTS }
                        }

                        if (filteredReports.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF4ECF8)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        DocumentIcon(
                                            color = MomoraPurplePrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = if (uiState.reports.isEmpty()) "No medical reports uploaded yet" else "No reports in this category",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MomoraPurpleDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Upload lab results or ultrasound scans above.",
                                        fontSize = 12.sp,
                                        color = MomoraBodyMuted
                                    )
                                }
                            }
                        } else {
                            filteredReports.forEachIndexed { index, report ->
                                ReportRowItem(
                                    report = report,
                                    isDownloading = uiState.downloadingReportId == report.id,
                                    onCardClick = { onReportCardClick(report) },
                                    onViewClick = { onOpenReport(report) }
                                )
                                if (index < (filteredReports.size - 1)) {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(Color(0xFFF7F1FA))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Care-Team Sharing Privacy Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MomoraMintBadgeBg)
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "Care-team sharing is on",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2C28)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Only you and your assigned doctor can securely access these reports.",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = Color(0xFF43524E)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Check names and dates before uploading sensitive documents.",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8984)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 3. Bottom Dashboard Navigation Bar
        DashboardBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }

    // 4. Upload Report Bottom Sheet
    if (uiState.showUploadDialog) {
        ModalBottomSheet(
            onDismissRequest = onDismissUploadDialog,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Upload Medical Report",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Selected File Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF4ECF8))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            DocumentIcon(
                                color = MomoraPurplePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.uploadFileName,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MomoraPurpleDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (uiState.uploadFileSizeFormatted.isNotBlank()) {
                            Text(
                                text = uiState.uploadFileSizeFormatted,
                                fontSize = 11.sp,
                                color = MomoraBodyMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Report Title Input
                Text(
                    text = "Report title",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MomoraPurpleDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.uploadTitle,
                    onValueChange = onUploadTitleChanged,
                    placeholder = { Text("e.g. Anatomy Ultrasound Scan", fontSize = 13.sp, color = MomoraBodyMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = MomoraPurplePrimary,
                        unfocusedIndicatorColor = Color(0xFFD6C8DE)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category Selection Chips
                Text(
                    text = "Category",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MomoraPurpleDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isUltrasound = uiState.uploadCategory == ReportCategory.ULTRASOUND
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isUltrasound) MomoraPurplePrimary else Color(0xFFF4ECF8))
                            .clickable { onUploadCategoryChanged(ReportCategory.ULTRASOUND) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ultrasound",
                            fontSize = 12.5.sp,
                            fontWeight = if (isUltrasound) FontWeight.Bold else FontWeight.Normal,
                            color = if (isUltrasound) Color.White else MomoraPurpleDark
                        )
                    }

                    val isLab = uiState.uploadCategory == ReportCategory.LAB_RESULTS
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isLab) MomoraPurplePrimary else Color(0xFFF4ECF8))
                            .clickable { onUploadCategoryChanged(ReportCategory.LAB_RESULTS) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lab Results",
                            fontSize = 12.5.sp,
                            fontWeight = if (isLab) FontWeight.Bold else FontWeight.Normal,
                            color = if (isLab) Color.White else MomoraPurpleDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description Input (Optional)
                Text(
                    text = "Description / Notes (optional)",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MomoraPurpleDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.uploadDescription,
                    onValueChange = onUploadDescriptionChanged,
                    placeholder = { Text("Add any notes or context from doctor", fontSize = 13.sp, color = MomoraBodyMuted) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = MomoraPurplePrimary,
                        unfocusedIndicatorColor = Color(0xFFD6C8DE)
                    )
                )

                if (!uiState.uploadErrorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.uploadErrorMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFD32F2F)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFF4ECF8))
                            .clickable(enabled = !uiState.isUploading) { onDismissUploadDialog() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MomoraPurpleDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (uiState.isUploading) MomoraPurplePrimary.copy(alpha = 0.6f) else MomoraPurplePrimary)
                            .clickable(enabled = !uiState.isUploading) { onConfirmUpload() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isUploading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Upload",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // 5. Report Detail Bottom Sheet
    val selectedReport = uiState.selectedReportForDetails
    if (uiState.showReportDetailSheet && selectedReport != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissReportDetailSheet,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Report Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )

                    val catBadgeBg = when (selectedReport.category) {
                        ReportCategory.ULTRASOUND -> Color(0xFFF4ECF8)
                        ReportCategory.LAB_RESULTS -> MomoraMintBadgeBg
                        else -> Color(0xFFEDE4F0)
                    }
                    val catBadgeColor = when (selectedReport.category) {
                        ReportCategory.LAB_RESULTS -> MomoraMintBadgeText
                        else -> MomoraPurplePrimary
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(catBadgeBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (selectedReport.category == ReportCategory.ULTRASOUND) "Ultrasound" else "Lab Results",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = catBadgeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = selectedReport.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = selectedReport.displaySubtitle,
                    fontSize = 12.5.sp,
                    color = MomoraBodyMuted
                )

                if (!selectedReport.originalFilename.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "File: ${selectedReport.originalFilename}",
                        fontSize = 12.sp,
                        color = MomoraPurpleDark
                    )
                }

                if (!selectedReport.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9F5FB))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = selectedReport.description,
                            fontSize = 12.5.sp,
                            lineHeight = 16.sp,
                            color = MomoraPurpleDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons: Open & Share
                val isDownloadingThis = uiState.downloadingReportId == selectedReport.id
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MomoraPurplePrimary)
                            .clickable(enabled = !isDownloadingThis) { onOpenReport(selectedReport) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDownloadingThis) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Open Document",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFF4ECF8))
                            .clickable(enabled = !isDownloadingThis) { onShareReport(selectedReport) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Share / Save",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MomoraPurpleDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable { onDismissReportDetailSheet() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Close",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MomoraBodyMuted
                    )
                }
            }
        }
    }
}

/**
 * Upload Card with Dashed Border.
 */
@Composable
private fun DashedUploadCard(
    isUploading: Boolean,
    onChooseFileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF4ECF8))
            .border(
                width = 1.dp,
                color = Color(0xFFCBBAD2),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onChooseFileClick() }
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEBE0F2)),
                contentAlignment = Alignment.Center
            ) {
                UploadCloudIcon(
                    color = MomoraPurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Upload a medical report",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "PDF, JPG or PNG · up to 10 MB",
                fontSize = 11.5.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(50))
                    .clickable { onChooseFileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUploading) "Uploading..." else "Choose a file",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )
            }
        }
    }
}

/**
 * Report Row Item with full-card clickability, chevron, and direct View action.
 */
@Composable
private fun ReportRowItem(
    report: MedicalReportItem,
    isDownloading: Boolean = false,
    onCardClick: () -> Unit,
    onViewClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCardClick() }
            .padding(vertical = 10.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            val iconBg = when (report.category) {
                ReportCategory.ULTRASOUND -> Color(0xFFEDE4F0)
                ReportCategory.LAB_RESULTS -> MomoraMintBadgeBg
                else -> MomoraPinkCircleBg
            }

            val iconColor = when (report.category) {
                ReportCategory.LAB_RESULTS -> MomoraMintBadgeText
                else -> MomoraPurplePrimary
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                DocumentIcon(color = iconColor, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = report.displaySubtitle,
                    fontSize = 11.5.sp,
                    color = MomoraBodyMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val btnBg = when (report.category) {
                ReportCategory.LAB_RESULTS -> MomoraMintBadgeBg
                ReportCategory.ULTRASOUND -> Color(0xFFF4ECF8)
                else -> MomoraPinkCircleBg
            }

            val btnTextColor = when (report.category) {
                ReportCategory.LAB_RESULTS -> MomoraMintBadgeText
                else -> MomoraPurplePrimary
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(btnBg)
                    .clickable { onViewClick() }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        color = btnTextColor,
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 1.5.dp
                    )
                } else {
                    Text(
                        text = "View",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = btnTextColor
                    )
                }
            }

            Text(
                text = "›",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8C7F92)
            )
        }
    }
}

/**
 * Bottom Navigation Bar.
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

/**
 * Custom Canvas vector drawings.
 */
@Composable
private fun UploadCloudIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val cloudPath = Path().apply {
            moveTo(w * 0.30f, h * 0.65f)
            cubicTo(w * 0.15f, h * 0.65f, w * 0.15f, h * 0.45f, w * 0.30f, h * 0.40f)
            cubicTo(w * 0.35f, h * 0.20f, w * 0.65f, h * 0.20f, w * 0.70f, h * 0.40f)
            cubicTo(w * 0.85f, h * 0.45f, w * 0.85f, h * 0.65f, w * 0.70f, h * 0.65f)
            close()
        }
        drawPath(cloudPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Up arrow inside cloud
        drawLine(color = color, start = Offset(w * 0.50f, h * 0.72f), end = Offset(w * 0.50f, h * 0.48f), strokeWidth = strokeWidth)
        val arrowHead = Path().apply {
            moveTo(w * 0.40f, h * 0.56f)
            lineTo(w * 0.50f, h * 0.48f)
            lineTo(w * 0.60f, h * 0.56f)
        }
        drawPath(arrowHead, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
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
private fun RefreshIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val arcPath = Path().apply {
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(w * 0.2f, h * 0.2f, w * 0.8f, h * 0.8f),
                startAngleDegrees = 45f,
                sweepAngleDegrees = 270f,
                forceMoveTo = false
            )
        }
        drawPath(arcPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

        // Arrow tip at top
        val arrowTip = Path().apply {
            moveTo(w * 0.65f, h * 0.12f)
            lineTo(w * 0.82f, h * 0.22f)
            lineTo(w * 0.68f, h * 0.32f)
        }
        drawPath(arrowTip, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
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
fun MedicalReportsScreenPreview() {
    MedicalReportsScreenContent()
}
