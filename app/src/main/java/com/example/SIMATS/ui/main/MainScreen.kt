package com.example.SIMATS.ui.main

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorDashboardData
import com.example.SIMATS.data.model.DoctorNextAppointment
import com.example.SIMATS.data.model.DoctorReviewItem
import com.example.SIMATS.navigation.SelectedPatientHolder
import com.example.SIMATS.ui.components.formatDoctorTime
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.components.sanitizeDoctorText
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraIconBtnBg
import com.example.SIMATS.ui.theme.MomoraLavenderCircleBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 0,
    onLogoutSuccess: () -> Unit = {},
    onViewAssignedPatientsClick: () -> Unit = {},
    onScheduleClick: () -> Unit = {},
    onNextAppointmentClick: () -> Unit = {},
    onReviewPatientClick: (String, String) -> Unit = { _, _ -> },
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshNotifications()
        viewModel.refreshDoctorPhoto()
    }

    MainScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onNavTabSelected = { index ->
            viewModel.onEvent(MainUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        },
        onReviewPatientClick = { id, name ->
            viewModel.onEvent(MainUiEvent.OnReviewPatientClicked(id, name))
            onReviewPatientClick(id, name)
        },
        onViewAssignedPatientsClick = {
            viewModel.onEvent(MainUiEvent.OnViewAssignedPatientsClicked)
            onViewAssignedPatientsClick()
        },
        onScheduleClick = {
            viewModel.onEvent(MainUiEvent.OnScheduleClicked)
            onScheduleClick()
        },
        onNextAppointmentClick = {
            viewModel.onEvent(MainUiEvent.OnNextAppointmentClicked)
            onNextAppointmentClick()
        },
        onRetryClick = { viewModel.onEvent(MainUiEvent.OnRetryClicked) },
        onProfileClick = onProfileClick,
        onLogoutClick = { viewModel.onEvent(MainUiEvent.OnLogoutClicked) },
        onNotificationClick = {
            viewModel.onEvent(MainUiEvent.OnNotificationClicked)
            onNotificationClick()
        }
    )
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    uiState: MainUiState = MainUiState(
        dashboardData = DoctorDashboardData(
            doctorName = "Dr Chen",
            clinicName = "Willow Maternity Clinic",
            dateText = "Tuesday, 27 Oct 2026",
            assignedPatientsCount = 18,
            visitsTodayCount = 6,
            upcomingVisitsCount = 4,
            completedVisitsCount = 2,
            toReviewCount = 2,
            reviewItems = listOf(
                DoctorReviewItem(
                    id = "1",
                    patientName = "Laila Ahmed",
                    gestationalAge = "30w 2d",
                    reportType = "New ultrasound report"
                ),
                DoctorReviewItem(
                    id = "2",
                    patientName = "Sofia Reyes",
                    gestationalAge = "18w 5d",
                    reportType = "New lab report"
                )
            ),
            nextAppointment = DoctorNextAppointment(
                patientName = "Nora Williams",
                timeText = "Today, 11:30",
                visitType = "Routine antenatal",
                mode = "Video",
                futureNote = "Maya Patel's next visit · 29 Oct at 10:30"
            )
        )
    ),
    selectedNavIndex: Int = 0,
    onNavTabSelected: (Int) -> Unit = {},
    onReviewPatientClick: (String, String) -> Unit = { _, _ -> },
    onViewAssignedPatientsClick: () -> Unit = {},
    onScheduleClick: () -> Unit = {},
    onNextAppointmentClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.dashboardData
    val isDark = ThemeManager.isDarkTheme()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) MaterialTheme.colorScheme.background else Color(0xFFFCF9FC))
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(elevation = 2.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF241C2A) else Color(0xFFF1E7F5)),
                    contentAlignment = Alignment.Center
                ) {
                    TopBarHeartIcon(
                        color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "MOMORA CARE",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76)
                    )
                    Text(
                        text = "Doctor Portal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFB5AAB9) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Notification bell icon with soft pastel circular background & unread dot
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(elevation = 1.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF241C2A) else Color(0xFFFAF0F3))
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BellIcon(
                        color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76),
                        modifier = Modifier.size(18.dp)
                    )
                    if (uiState.unreadNotificationCount > 0) {
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

                // Redesigned Logout button: modern soft rose pill with logout icon
                Row(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isDark) Color(0xFF331A1E) else Color(0xFFFFF1F3))
                        .border(1.dp, if (isDark) Color(0xFF5A262E) else Color(0xFFFFDCE2), RoundedCornerShape(18.dp))
                        .clickable { onLogoutClick() }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LogoutIcon(
                        color = if (isDark) Color(0xFFFF8A80) else Color(0xFFD32F2F),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Log out",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFFF8A80) else Color(0xFFD32F2F)
                    )
                }

                // Profile Avatar: attractive circular avatar displaying profile photo or "Dr" initials
                val doctorPhoto = uiState.doctorPhoto ?: SessionManager.getDoctorPhoto()
                var doctorAvatarBitmap by remember(doctorPhoto) { mutableStateOf<ImageBitmap?>(null) }
                val context = LocalContext.current

                LaunchedEffect(doctorPhoto) {
                    if (!doctorPhoto.isNullOrBlank()) {
                        withContext(Dispatchers.IO) {
                            try {
                                val file = File(doctorPhoto)
                                if (file.exists()) {
                                    val bmp = BitmapFactory.decodeFile(file.absolutePath)
                                    doctorAvatarBitmap = bmp?.asImageBitmap()
                                } else if (doctorPhoto.startsWith("http://") || doctorPhoto.startsWith("https://")) {
                                    val conn = URL(doctorPhoto).openConnection() as HttpURLConnection
                                    conn.connectTimeout = 8000
                                    conn.readTimeout = 8000
                                    conn.setRequestProperty("ngrok-skip-browser-warning", "true")
                                    SessionManager.getToken()?.let {
                                        conn.setRequestProperty("Authorization", "Bearer $it")
                                    }
                                    conn.doInput = true
                                    conn.connect()
                                    if (conn.responseCode in 200..299) {
                                        conn.inputStream.use { stream ->
                                            val bmp = BitmapFactory.decodeStream(stream)
                                            doctorAvatarBitmap = bmp?.asImageBitmap()
                                        }
                                    }
                                }
                            } catch (_: Exception) {}
                        }
                    } else {
                        doctorAvatarBitmap = null
                    }
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(elevation = 2.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .border(
                            width = 1.5.dp,
                            color = if (isDark) Color(0xFF4A345B) else Color(0xFFE2D2EB),
                            shape = CircleShape
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF6E4F76), Color(0xFF5A3D63))
                            )
                        )
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (doctorAvatarBitmap != null) {
                        Image(
                            bitmap = doctorAvatarBitmap!!,
                            contentDescription = "Doctor Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "Dr",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 2. Scrollable Doctor Dashboard Body
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
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.errorMessage != null && data == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, Color(0xFFF2DEDE), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatErrorMessage(uiState.errorMessage ?: "Failed to load doctor dashboard"),
                            fontSize = 14.sp,
                            color = Color(0xFFA94442),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable { onRetryClick() }
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Retry",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            } else {
                if (uiState.errorMessage != null && data != null) {
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
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { onRetryClick() }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                // Welcome Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDark) Color(0xFF281E32) else Color(0xFFF1E7F5))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = (data?.clinicName ?: "Willow Maternity Clinic").sanitizeDoctorText(),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76)
                        )
                    }
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = (data?.dateText ?: "Tuesday, 27 Oct 2026").sanitizeDoctorText(),
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Good morning, ${(data?.doctorName ?: "Dr Chen").sanitizeDoctorText()}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Assigned Patients Banner Card (Beautiful Lavender Card)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isDark) {
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF291E32), Color(0xFF352542))
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFF2EAF7), Color(0xFFE9DCF0))
                                )
                            }
                        )
                        .border(1.dp, if (isDark) Color(0xFF433053) else Color(0xFFE2D2EB), RoundedCornerShape(20.dp))
                        .clickable { onViewAssignedPatientsClick() }
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0xFF3B2A48) else Color.White)
                                    .border(1.dp, if (isDark) Color(0xFF4E3760) else Color(0xFFDFCFE8), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                MedicalPersonIcon(
                                    color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "ASSIGNED PATIENTS",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${data?.assignedPatientsCount ?: 18}",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color(0xFFEDE7EF) else Color(0xFF3B2544)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "expectant mothers",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDark) Color(0xFFC7B6CE) else Color(0xFF5A3D63),
                                        modifier = Modifier.padding(bottom = 3.dp)
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isDark) Color(0xFF433053) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF553D68) else Color(0xFFDFCFE8), RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "View",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFEDE7EF) else Color(0xFF6E4F76)
                                )
                                Text(
                                    text = "›",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFEDE7EF) else Color(0xFF6E4F76)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Visits & Reviews Summary Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Visits Today Card (Pastel Lavender)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(elevation = 1.5.dp, shape = RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isDark) {
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF221A28), Color(0xFF1B1420))
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFF8F3FB), Color(0xFFF1E8F6))
                                    )
                                }
                            )
                            .border(1.dp, if (isDark) Color(0xFF382D42) else Color(0xFFEADCEE), RoundedCornerShape(20.dp))
                            .clickable { onScheduleClick() }
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Visits today",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF5A3D63)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF2E2235) else Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CalendarClockIcon(
                                        color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "${data?.visitsTodayCount ?: 6}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFEDE7EF) else Color(0xFF382340)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${data?.upcomingVisitsCount ?: 4} upcoming · ${data?.completedVisitsCount ?: 2} done",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFFB5ACB9) else Color(0xFF7D7584)
                            )
                        }
                    }

                    // To Review Card (Pastel Pink)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(elevation = 1.5.dp, shape = RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isDark) {
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF2A1B24), Color(0xFF20131B))
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFFCF2F5), Color(0xFFF8E7ED))
                                    )
                                }
                            )
                            .border(1.dp, if (isDark) Color(0xFF452A38) else Color(0xFFF3DDE5), RoundedCornerShape(20.dp))
                            .clickable { onViewAssignedPatientsClick() }
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "To review",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) Color(0xFFE5A8C2) else Color(0xFF703D51)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF35202D) else Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ClipboardCheckIcon(
                                        color = if (isDark) Color(0xFFE5A8C2) else Color(0xFF944B68),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "${data?.toReviewCount ?: 2}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFEDE7EF) else Color(0xFF4C2233)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "lab & ultrasound updates",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFFB5ACB9) else Color(0xFF8C717C)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Grouped Review Items
                val groupedReviews = (data?.reviewItems.orEmpty())
                    .groupBy { it.patientName }
                    .entries
                    .toList()

                // "Needs your review" Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Needs your review",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (groupedReviews.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isDark) Color(0xFF35202D) else Color(0xFFFAF0F3))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${groupedReviews.size} pending",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFE5A8C2) else Color(0xFF944B68)
                                )
                            }
                        }
                    }

                    Text(
                        text = "All reviews",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76),
                        modifier = Modifier.clickable { onViewAssignedPatientsClick() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (groupedReviews.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isDark) Color(0xFF1E1A24) else Color.White)
                            .border(1.dp, if (isDark) Color(0xFF382E40) else Color(0xFFEFE8F2), RoundedCornerShape(20.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) Color(0xFF281E32) else Color(0xFFF1E7F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                ClipboardCheckIcon(
                                    color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All reports reviewed",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "New lab and ultrasound reports requiring your review will appear here.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 1.5.dp, shape = RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isDark) Color(0xFF1E1A24) else Color.White)
                            .border(1.dp, if (isDark) Color(0xFF382E40) else Color(0xFFEFE8F2), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Column {
                            val displayedGroups = groupedReviews.take(3)
                            displayedGroups.forEachIndexed { index, entry ->
                                val patientName = entry.key
                                val items = entry.value
                                val firstItem = items.first()
                                val patientId = firstItem.id
                                val gestationalAge = firstItem.gestationalAge
                                val count = items.size
                                val countText = if (count == 1) "1 report to review" else "$count reports to review"

                                DoctorPatientReviewRow(
                                    patientName = patientName,
                                    gestationalAge = gestationalAge,
                                    countText = countText,
                                    isDark = isDark,
                                    onReviewClick = {
                                        SelectedPatientHolder.initialTab = 1
                                        onReviewPatientClick(patientId, patientName)
                                    }
                                )

                                if (index < (displayedGroups.size - 1)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(if (isDark) Color(0xFF2D2535) else Color(0xFFF7F3F9))
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // "Next appointment" Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Next appointment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Schedule ›",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76),
                        modifier = Modifier.clickable { onScheduleClick() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Next Appointment Card
                val nextAppt = data?.nextAppointment
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDark) Color(0xFF1E1A24) else Color.White)
                        .border(1.dp, if (isDark) Color(0xFF382E40) else Color(0xFFEADBEE), RoundedCornerShape(20.dp))
                        .clickable { onNextAppointmentClick() }
                        .padding(16.dp)
                ) {
                    Column {
                        // Tag row: Consultation type and mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0xFF281E32) else Color(0xFFF1E7F5))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = (nextAppt?.visitType ?: "Routine antenatal").sanitizeDoctorText(),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0xFF1E3A2B) else Color(0xFFE2F4EB))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = (nextAppt?.mode ?: "Video").sanitizeDoctorText(),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) Color(0xFF86EFAC) else Color(0xFF3D7B59)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Patient details and time row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF281E32) else Color(0xFFFAF0F3))
                                        .border(1.dp, if (isDark) Color(0xFF3E2D4B) else Color(0xFFF5D8E4), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ConsultationIcon(
                                        mode = nextAppt?.mode ?: "Video",
                                        color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = (nextAppt?.patientName ?: "Nora Williams").sanitizeDoctorText(),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = (nextAppt?.timeText ?: "Today, 11:30").sanitizeDoctorText(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) Color(0xFF281E32) else Color(0xFFF7F2FA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "›",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76)
                                )
                            }
                        }

                        val patientForNote = SelectedPatientHolder.selectedPatientName?.takeIf { it.isNotBlank() } ?: "Patient"
                        val rawFutureNote = nextAppt?.futureNote?.takeIf { it.isNotBlank() } ?: "$patientForNote's next visit · Scheduled"

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(if (isDark) Color(0xFF2D2535) else Color(0xFFF5EEF7))
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CalendarClockIcon(
                                color = if (isDark) Color(0xFF887D90) else Color(0xFF9C93A3),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = rawFutureNote.sanitizeDoctorText(),
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // 3. Doctor Bottom Navigation Bar (4 Doctor Tabs)
        DoctorBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

@Composable
private fun DoctorPatientReviewRow(
    patientName: String,
    gestationalAge: String,
    countText: String,
    isDark: Boolean = false,
    onReviewClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReviewClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF2E2435) else Color(0xFFF1E7F5))
                    .border(1.dp, if (isDark) Color(0xFF45364E) else Color(0xFFE4D5EC), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                DocumentIcon(color = if (isDark) Color(0xFFD4B3DC) else Color(0xFF6E4F76), modifier = Modifier.size(18.dp))
            }

            Column {
                Text(
                    text = patientName.sanitizeDoctorText(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFE6E1E5) else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (gestationalAge.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isDark) Color(0xFF3B1E28) else Color(0xFFFAF0F3))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = gestationalAge.sanitizeDoctorText(),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFFFB2CB) else Color(0xFF944B68)
                            )
                        }
                    }
                    Text(
                        text = countText,
                        fontSize = 11.5.sp,
                        color = if (isDark) Color(0xFFCAC4D0) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(if (isDark) Color(0xFF9370A3) else Color(0xFF6E4F76))
                .clickable { onReviewClick() }
                .padding(horizontal = 16.dp, vertical = 7.dp)
        ) {
            Text(
                text = "Review",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Doctor Bottom Navigation Bar with 4 Tabs (Home, Patients, Schedule, Advice).
 */
@Composable
private fun DoctorBottomNavBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val isDark = ThemeManager.isDarkTheme()
    val navSurface = if (isDark) Color(0xFF1E1A22) else Color.White
    val navBorder = if (isDark) Color(0xFF2E2633) else Color(0xFFEFE8F2)
    val indicatorColor = if (isDark) Color(0xFF483D4F) else Color(0xFFDFD5E3)
    val activeNavColor = if (isDark) Color(0xFFE0C4E7) else Color(0xFF6E4F76)
    val inactiveNavColor = if (isDark) Color(0xFF8D8392) else Color(0xFF8E8394)

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
                icon = { HomeNavIcon(color = if (selectedIndex == 0) activeNavColor else inactiveNavColor, modifier = Modifier.size(22.dp)) }
            )
            NavTabItem(
                label = "Patients",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { PeopleNavIcon(color = if (selectedIndex == 1) activeNavColor else inactiveNavColor, modifier = Modifier.size(22.dp)) }
            )
            NavTabItem(
                label = "Schedule",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { CalendarNavIcon(color = if (selectedIndex == 2) activeNavColor else inactiveNavColor, modifier = Modifier.size(22.dp)) }
            )
            NavTabItem(
                label = "Advice",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { ChatNavIcon(color = if (selectedIndex == 3) activeNavColor else inactiveNavColor, modifier = Modifier.size(22.dp)) }
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
    val isDark = ThemeManager.isDarkTheme()
    val activeBg = if (isDark) Color(0xFF35263B) else Color(0xFFF1E7F5)
    val activeColor = if (isDark) Color(0xFFD4B3DC) else Color(0xFF5A3D63)
    val inactiveColor = if (isDark) Color(0xFF8D8392) else Color(0xFF8E8394)

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
 * Canvas vector drawings.
 */
@Composable
private fun DocumentIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

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
private fun ConsultationIcon(mode: String, color: Color, modifier: Modifier = Modifier) {
    if (mode.contains("Video", ignoreCase = true)) {
        VideoIcon(color = color, modifier = modifier)
    } else {
        ClinicVisitIcon(color = color, modifier = modifier)
    }
}

@Composable
private fun VideoIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRect(
            color = color,
            topLeft = Offset(w * 0.15f, h * 0.28f),
            size = Size(w * 0.48f, h * 0.44f),
            style = Stroke(width = strokeWidth)
        )

        val trianglePath = Path().apply {
            moveTo(w * 0.63f, h * 0.40f)
            lineTo(w * 0.85f, h * 0.28f)
            lineTo(w * 0.85f, h * 0.72f)
            lineTo(w * 0.63f, h * 0.60f)
            close()
        }
        drawPath(trianglePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun ClinicVisitIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val bldg = Path().apply {
            moveTo(w * 0.22f, h * 0.82f)
            lineTo(w * 0.22f, h * 0.26f)
            lineTo(w * 0.78f, h * 0.26f)
            lineTo(w * 0.78f, h * 0.82f)
            close()
        }
        drawPath(bldg, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val crossLen = w * 0.12f
        val cx = w * 0.50f
        val cy = h * 0.46f
        drawLine(color = color, start = Offset(cx - crossLen, cy), end = Offset(cx + crossLen, cy), strokeWidth = strokeWidth * 1.1f, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(cx, cy - crossLen), end = Offset(cx, cy + crossLen), strokeWidth = strokeWidth * 1.1f, cap = StrokeCap.Round)

        drawLine(color = color, start = Offset(w * 0.42f, h * 0.82f), end = Offset(w * 0.42f, h * 0.68f), strokeWidth = strokeWidth)
        drawLine(color = color, start = Offset(w * 0.42f, h * 0.68f), end = Offset(w * 0.58f, h * 0.68f), strokeWidth = strokeWidth)
        drawLine(color = color, start = Offset(w * 0.58f, h * 0.68f), end = Offset(w * 0.58f, h * 0.82f), strokeWidth = strokeWidth)
    }
}

@Composable
private fun BellIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val bellPath = Path().apply {
            moveTo(w * 0.22f, h * 0.66f)
            lineTo(w * 0.78f, h * 0.66f)
            cubicTo(w * 0.74f, h * 0.42f, w * 0.68f, h * 0.26f, w * 0.50f, h * 0.26f)
            cubicTo(w * 0.32f, h * 0.26f, w * 0.26f, h * 0.42f, w * 0.22f, h * 0.66f)
            close()
        }
        drawPath(bellPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawCircle(color = color, radius = w * 0.07f, center = Offset(w * 0.50f, h * 0.20f), style = Stroke(width = strokeWidth * 0.9f))
        drawCircle(color = color, radius = w * 0.08f, center = Offset(w * 0.50f, h * 0.78f))
    }
}

@Composable
private fun LogoutIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val doorPath = Path().apply {
            moveTo(w * 0.55f, h * 0.20f)
            lineTo(w * 0.22f, h * 0.20f)
            lineTo(w * 0.22f, h * 0.80f)
            lineTo(w * 0.55f, h * 0.80f)
        }
        drawPath(doorPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = color, start = Offset(w * 0.40f, h * 0.50f), end = Offset(w * 0.82f, h * 0.50f), strokeWidth = strokeWidth, cap = StrokeCap.Round)

        val arrowHead = Path().apply {
            moveTo(w * 0.68f, h * 0.35f)
            lineTo(w * 0.84f, h * 0.50f)
            lineTo(w * 0.68f, h * 0.65f)
        }
        drawPath(arrowHead, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun MedicalPersonIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.17f,
            center = Offset(w * 0.42f, h * 0.30f),
            style = Stroke(width = strokeWidth)
        )

        val bodyPath = Path().apply {
            moveTo(w * 0.15f, h * 0.80f)
            cubicTo(w * 0.18f, h * 0.58f, w * 0.66f, h * 0.58f, w * 0.69f, h * 0.80f)
        }
        drawPath(bodyPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

        val plusX = w * 0.78f
        val plusY = h * 0.34f
        val plusLen = w * 0.13f
        drawLine(color = color, start = Offset(plusX - plusLen, plusY), end = Offset(plusX + plusLen, plusY), strokeWidth = strokeWidth * 1.1f, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(plusX, plusY - plusLen), end = Offset(plusX, plusY + plusLen), strokeWidth = strokeWidth * 1.1f, cap = StrokeCap.Round)
    }
}

@Composable
private fun CalendarClockIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

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

        drawCircle(color = color, radius = w * 0.04f, center = Offset(w * 0.38f, h * 0.58f))
        drawCircle(color = color, radius = w * 0.04f, center = Offset(w * 0.62f, h * 0.58f))
        drawCircle(color = color, radius = w * 0.04f, center = Offset(w * 0.38f, h * 0.72f))
    }
}

@Composable
private fun ClipboardCheckIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val board = Path().apply {
            moveTo(w * 0.22f, h * 0.25f)
            lineTo(w * 0.35f, h * 0.25f)
            moveTo(w * 0.65f, h * 0.25f)
            lineTo(w * 0.78f, h * 0.25f)
            lineTo(w * 0.78f, h * 0.84f)
            lineTo(w * 0.22f, h * 0.84f)
            close()
        }
        drawPath(board, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val clip = Path().apply {
            moveTo(w * 0.35f, h * 0.25f)
            lineTo(w * 0.35f, h * 0.18f)
            lineTo(w * 0.65f, h * 0.18f)
            lineTo(w * 0.65f, h * 0.25f)
        }
        drawPath(clip, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val check = Path().apply {
            moveTo(w * 0.36f, h * 0.55f)
            lineTo(w * 0.48f, h * 0.67f)
            lineTo(w * 0.66f, h * 0.45f)
        }
        drawPath(check, color = color, style = Stroke(width = strokeWidth * 1.1f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun HomeNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val housePath = Path().apply {
            moveTo(w * 0.20f, h * 0.82f)
            lineTo(w * 0.20f, h * 0.46f)
            lineTo(w * 0.50f, h * 0.20f)
            lineTo(w * 0.80f, h * 0.46f)
            lineTo(w * 0.80f, h * 0.82f)
            close()
        }
        drawPath(housePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val doorPath = Path().apply {
            moveTo(w * 0.40f, h * 0.82f)
            lineTo(w * 0.40f, h * 0.56f)
            cubicTo(w * 0.40f, h * 0.52f, w * 0.60f, h * 0.52f, w * 0.60f, h * 0.56f)
            lineTo(w * 0.60f, h * 0.82f)
        }
        drawPath(doorPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun PeopleNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(color = color, radius = w * 0.16f, center = Offset(w * 0.40f, h * 0.32f), style = Stroke(width = strokeWidth))
        val bodyPath = Path().apply {
            moveTo(w * 0.14f, h * 0.82f)
            cubicTo(w * 0.20f, h * 0.58f, w * 0.60f, h * 0.58f, w * 0.66f, h * 0.82f)
        }
        drawPath(bodyPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

        drawCircle(color = color, radius = w * 0.11f, center = Offset(w * 0.72f, h * 0.42f), style = Stroke(width = strokeWidth * 0.9f))
        val childBody = Path().apply {
            moveTo(w * 0.64f, h * 0.82f)
            cubicTo(w * 0.66f, h * 0.66f, w * 0.84f, h * 0.66f, w * 0.88f, h * 0.82f)
        }
        drawPath(childBody, color = color, style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round))
    }
}

@Composable
private fun CalendarNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val calPath = Path().apply {
            moveTo(w * 0.20f, h * 0.28f)
            lineTo(w * 0.80f, h * 0.28f)
            lineTo(w * 0.80f, h * 0.82f)
            lineTo(w * 0.20f, h * 0.82f)
            close()
        }
        drawPath(calPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = color, start = Offset(w * 0.20f, h * 0.44f), end = Offset(w * 0.80f, h * 0.44f), strokeWidth = strokeWidth)

        drawLine(color = color, start = Offset(w * 0.35f, h * 0.18f), end = Offset(w * 0.35f, h * 0.28f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.65f, h * 0.18f), end = Offset(w * 0.65f, h * 0.28f), strokeWidth = strokeWidth, cap = StrokeCap.Round)

        drawCircle(color = color, radius = w * 0.035f, center = Offset(w * 0.38f, h * 0.58f))
        drawCircle(color = color, radius = w * 0.035f, center = Offset(w * 0.50f, h * 0.58f))
        drawCircle(color = color, radius = w * 0.035f, center = Offset(w * 0.62f, h * 0.58f))
        drawCircle(color = color, radius = w * 0.035f, center = Offset(w * 0.38f, h * 0.70f))
        drawCircle(color = color, radius = w * 0.035f, center = Offset(w * 0.50f, h * 0.70f))
    }
}

@Composable
private fun ChatNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val chatPath = Path().apply {
            moveTo(w * 0.20f, h * 0.25f)
            lineTo(w * 0.80f, h * 0.25f)
            cubicTo(w * 0.84f, h * 0.25f, w * 0.84f, h * 0.29f, w * 0.84f, h * 0.29f)
            lineTo(w * 0.84f, h * 0.63f)
            lineTo(w * 0.46f, h * 0.63f)
            lineTo(w * 0.28f, h * 0.80f)
            lineTo(w * 0.28f, h * 0.63f)
            lineTo(w * 0.16f, h * 0.63f)
            lineTo(w * 0.16f, h * 0.29f)
            close()
        }
        drawPath(chatPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawCircle(color = color, radius = w * 0.035f, center = Offset(w * 0.36f, h * 0.44f))
        drawCircle(color = color, radius = w * 0.035f, center = Offset(w * 0.50f, h * 0.44f))
        drawCircle(color = color, radius = w * 0.035f, center = Offset(w * 0.64f, h * 0.44f))
    }
}

@Composable
private fun TopBarHeartIcon(color: Color, modifier: Modifier = Modifier) {
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

        val pulsePath = Path().apply {
            moveTo(w * 0.28f, h * 0.50f)
            lineTo(w * 0.42f, h * 0.50f)
            lineTo(w * 0.48f, h * 0.38f)
            lineTo(w * 0.54f, h * 0.62f)
            lineTo(w * 0.60f, h * 0.50f)
            lineTo(w * 0.72f, h * 0.50f)
        }
        drawPath(pulsePath, color = color, style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun MainScreenPreview() {
    MainScreenContent()
}
