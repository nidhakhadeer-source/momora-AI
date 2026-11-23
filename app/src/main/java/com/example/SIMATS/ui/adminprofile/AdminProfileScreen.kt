package com.example.SIMATS.ui.adminprofile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
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
fun AdminProfileScreen(
    viewModel: AdminProfileViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 0,
    onNavigateBack: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    BackHandler {
        onNavigateBack()
    }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onEvent(AdminProfileUiEvent.OnPhotoSelected(uri, context))
        }
    }

    AdminProfileScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onBackClick = onNavigateBack,
        onToggleEditClick = { viewModel.onEvent(AdminProfileUiEvent.OnToggleEditMode) },
        onFullNameChanged = { viewModel.onEvent(AdminProfileUiEvent.OnFullNameChanged(it)) },
        onPhoneChanged = { viewModel.onEvent(AdminProfileUiEvent.OnPhoneChanged(it)) },
        onPhotoClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onCancelEditClick = { viewModel.onEvent(AdminProfileUiEvent.OnCancelEditClicked) },
        onSaveClick = { viewModel.onEvent(AdminProfileUiEvent.OnSaveClicked) },
        onChangePasswordClick = { viewModel.onEvent(AdminProfileUiEvent.OnOpenChangePasswordDialog) },
        onDismissChangePasswordDialog = { viewModel.onEvent(AdminProfileUiEvent.OnDismissChangePasswordDialog) },
        onCurrentPasswordChanged = { viewModel.onEvent(AdminProfileUiEvent.OnCurrentPasswordChanged(it)) },
        onNewPasswordChanged = { viewModel.onEvent(AdminProfileUiEvent.OnNewPasswordChanged(it)) },
        onConfirmPasswordChanged = { viewModel.onEvent(AdminProfileUiEvent.OnConfirmPasswordChanged(it)) },
        onSubmitChangePassword = { viewModel.onEvent(AdminProfileUiEvent.OnSubmitChangePassword) },
        onLogoutClick = { viewModel.onEvent(AdminProfileUiEvent.OnOpenLogoutDialog) },
        onDismissLogoutDialog = { viewModel.onEvent(AdminProfileUiEvent.OnDismissLogoutDialog) },
        onConfirmLogout = { viewModel.onEvent(AdminProfileUiEvent.OnLogoutConfirmed) },
        onThemeSelected = { viewModel.onEvent(AdminProfileUiEvent.OnThemeSelected(it)) },
        onDismissMessages = { viewModel.onEvent(AdminProfileUiEvent.OnDismissMessages) },
        onNavTabSelected = onNavTabSelected
    )
}

@Composable
fun AdminProfileScreenContent(
    modifier: Modifier = Modifier,
    uiState: AdminProfileUiState,
    selectedNavIndex: Int = 0,
    onBackClick: () -> Unit = {},
    onToggleEditClick: () -> Unit = {},
    onFullNameChanged: (String) -> Unit = {},
    onPhoneChanged: (String) -> Unit = {},
    onPhotoClick: () -> Unit = {},
    onCancelEditClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onDismissChangePasswordDialog: () -> Unit = {},
    onCurrentPasswordChanged: (String) -> Unit = {},
    onNewPasswordChanged: (String) -> Unit = {},
    onConfirmPasswordChanged: (String) -> Unit = {},
    onSubmitChangePassword: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onDismissLogoutDialog: () -> Unit = {},
    onConfirmLogout: () -> Unit = {},
    onThemeSelected: (String) -> Unit = {},
    onDismissMessages: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val isDark = ThemeManager.isDarkTheme()

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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF28212F) else Color(0xFFF7F2FA))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                BackArrowIcon(
                    color = if (isDark) Color(0xFFEDE7EF) else Color(0xFF5A3D63),
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = "Admin Profile",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (uiState.isEditing) {
                            if (isDark) Color(0xFF1E3A2B) else MomoraMintBadgeBg
                        } else {
                            if (isDark) Color(0xFF2B2233) else Color(0xFFF3EDF6)
                        }
                    )
                    .clickable { onToggleEditClick() }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.isEditing) "Viewing" else "Edit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isEditing) {
                        if (isDark) Color(0xFF86EFAC) else MomoraMintBadgeText
                    } else {
                        if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                    }
                )
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
            // Success / Error Banner
            if (uiState.successMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF193B26) else MomoraMintBadgeBg)
                        .border(1.dp, if (isDark) Color(0xFF2A593A) else Color(0xFFB7E1CD), RoundedCornerShape(12.dp))
                        .clickable { onDismissMessages() }
                        .padding(12.dp)
                ) {
                    Text(
                        text = uiState.successMessage ?: "",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFA3E9B8) else MomoraMintBadgeText
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF3B1F24) else Color(0xFFFFEBEE))
                        .border(1.dp, if (isDark) Color(0xFF5C2C34) else Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                        .clickable { onDismissMessages() }
                        .padding(12.dp)
                ) {
                    Text(
                        text = formatErrorMessage(uiState.errorMessage ?: ""),
                        fontSize = 13.sp,
                        color = if (isDark) Color(0xFFFF8A80) else Color(0xFFC62828)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Profile Header Card with Avatar
            AdminProfileHeaderCard(
                fullName = uiState.fullName,
                email = uiState.email,
                phone = uiState.phone,
                role = uiState.role,
                photoUrl = uiState.photoUrl,
                localUri = uiState.localPhotoUri,
                onPhotoClick = onPhotoClick,
                isDark = isDark
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Section 1: Account Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (uiState.isEditing) "Edit Account Details" else "Account Information",
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                )

                if (uiState.isEditing) {
                    Text(
                        text = "Editable",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Account Details Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEADBEE), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.isEditing) {
                    AdminEditableField(
                        label = "Full Name",
                        value = uiState.fullName,
                        onValueChange = onFullNameChanged,
                        placeholder = "Administrator Name",
                        isDark = isDark
                    )

                    AdminReadOnlyField(
                        label = "Email Address (Registered)",
                        value = uiState.email.ifBlank { "admin@willow.example" },
                        badge = "System Verified",
                        isDark = isDark
                    )

                    AdminEditableField(
                        label = "Phone Number",
                        value = uiState.phone,
                        onValueChange = onPhoneChanged,
                        placeholder = "+1 (555) 000-0000",
                        keyboardType = KeyboardType.Phone,
                        isDark = isDark
                    )

                    AdminReadOnlyField(
                        label = "Role",
                        value = "Administrator (Superuser / Full Access)",
                        badge = "Admin",
                        isDark = isDark
                    )
                } else {
                    AdminInfoRow(
                        label = "Full Name",
                        value = uiState.fullName.ifBlank { "Administrator" },
                        isDark = isDark
                    )
                    AdminInfoDivider(isDark)

                    AdminInfoRow(
                        label = "Email Address",
                        value = uiState.email.ifBlank { "admin@willow.example" },
                        badge = "Verified",
                        isDark = isDark
                    )
                    AdminInfoDivider(isDark)

                    AdminInfoRow(
                        label = "Phone Number",
                        value = uiState.phone.ifBlank { "Not provided" },
                        isDark = isDark
                    )
                    AdminInfoDivider(isDark)

                    AdminInfoRow(
                        label = "Role",
                        value = "Administrator",
                        badge = "Full Access",
                        isDark = isDark
                    )
                    AdminInfoDivider(isDark)

                    AdminInfoRow(
                        label = "Account Status",
                        value = "Active",
                        badge = "Operational",
                        badgeColor = MomoraMintBadgeText,
                        badgeBg = MomoraMintBadgeBg,
                        isDark = isDark
                    )
                }
            }

            // Edit Action Buttons
            if (uiState.isEditing) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF28212F) else Color(0xFFF3EDF6))
                            .clickable { onCancelEditClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MomoraPurplePrimary)
                            .clickable { if (!uiState.isSaving) onSaveClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Save Changes",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Account Security & Settings
            Text(
                text = "Security & Settings",
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF1F1A24) else Color.White)
                    .border(1.dp, if (isDark) Color(0xFF332A3B) else Color(0xFFEADBEE), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Change Password Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChangePasswordClick() }
                        .padding(vertical = 4.dp),
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
                                .background(if (isDark) Color(0xFF2F2438) else Color(0xFFF3EDF6)),
                            contentAlignment = Alignment.Center
                        ) {
                            KeyNavIcon(
                                color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Change password",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Update your admin account password",
                                fontSize = 11.5.sp,
                                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                            )
                        }
                    }

                    Text(
                        text = "›",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFB5ACB9) else Color(0xFF9E8A9E)
                    )
                }

                AdminInfoDivider(isDark)

                // Theme Selector
                Column {
                    Text(
                        text = "App Theme",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Customize the interface appearance",
                        fontSize = 11.5.sp,
                        color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val themes = listOf(
                            "SYSTEM" to "System",
                            "LIGHT" to "Light",
                            "DARK" to "Dark"
                        )
                        themes.forEach { (key, label) ->
                            val isSelected = uiState.appTheme == key
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MomoraPurplePrimary else (if (isDark) Color(0xFF28212F) else Color(0xFFF6F0F8))
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) MomoraPurplePrimary else (if (isDark) Color(0xFF382E40) else Color(0xFFEADBEE)),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onThemeSelected(key) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else (if (isDark) Color(0xFFD4B3DC) else MomoraPurpleDark)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Logout Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF331A1E) else Color(0xFFFFF0F0))
                    .border(1.dp, if (isDark) Color(0xFF5A262E) else Color(0xFFFFD2D2), RoundedCornerShape(12.dp))
                    .clickable { onLogoutClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🚪",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log out from Admin Portal",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFFF8A80) else Color(0xFFD32F2F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // 3. Bottom Navigation Bar
        AdminBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected,
            isDark = isDark
        )
    }

    // Change Password Dialog
    if (uiState.showChangePasswordDialog) {
        ChangePasswordDialog(
            currentPassword = uiState.currentPasswordInput,
            newPassword = uiState.newPasswordInput,
            confirmPassword = uiState.confirmPasswordInput,
            errorMessage = uiState.passwordError,
            onCurrentPasswordChanged = onCurrentPasswordChanged,
            onNewPasswordChanged = onNewPasswordChanged,
            onConfirmPasswordChanged = onConfirmPasswordChanged,
            onDismiss = onDismissChangePasswordDialog,
            onSubmit = onSubmitChangePassword,
            isDark = isDark
        )
    }

    // Logout Confirmation Dialog
    if (uiState.showLogoutConfirmDialog) {
        LogoutConfirmDialog(
            onDismiss = onDismissLogoutDialog,
            onConfirm = onConfirmLogout,
            isLoggingOut = uiState.isLoggingOut,
            isDark = isDark
        )
    }
}

@Composable
private fun AdminProfileHeaderCard(
    fullName: String,
    email: String,
    phone: String,
    role: String,
    photoUrl: String?,
    localUri: Uri?,
    onPhotoClick: () -> Unit,
    isDark: Boolean
) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val effectivePhoto = localUri?.toString() ?: photoUrl

    val initials = remember(fullName) {
        val parts = fullName.trim().split(" ").filter { it.isNotBlank() }
        when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.size == 1 && parts[0].isNotEmpty() -> parts[0].first().uppercaseChar().toString()
            else -> "A"
        }
    }

    LaunchedEffect(effectivePhoto, localUri) {
        if (localUri != null) {
            withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(localUri)?.use { stream ->
                        val bmp = BitmapFactory.decodeStream(stream)
                        imageBitmap = bmp?.asImageBitmap()
                    }
                } catch (_: Exception) {}
            }
        } else if (!effectivePhoto.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val file = File(effectivePhoto)
                    if (file.exists()) {
                        val bmp = BitmapFactory.decodeFile(file.absolutePath)
                        imageBitmap = bmp?.asImageBitmap()
                    } else if (effectivePhoto.startsWith("http://") || effectivePhoto.startsWith("https://")) {
                        val conn = URL(effectivePhoto).openConnection() as HttpURLConnection
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
                                imageBitmap = bmp?.asImageBitmap()
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
        } else {
            imageBitmap = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDark) Color(0xFF1F1A24) else Color(0xFFF9F5FA))
            .border(1.dp, if (isDark) Color(0xFF382E40) else Color(0xFFF0E8F4), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar with camera badge
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clickable { onPhotoClick() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF2B2233) else Color(0xFFEDE4F0))
                        .border(2.dp, MomoraPurplePrimary.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap!!,
                            contentDescription = "Admin Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = initials,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFD4B3DC) else MomoraPurpleDark
                        )
                    }
                }

                // Camera icon overlay badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MomoraPurplePrimary)
                        .border(1.5.dp, if (isDark) Color(0xFF1F1A24) else Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📷",
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fullName.ifBlank { "Administrator" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Role Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isDark) Color(0xFF1B3B26) else MomoraMintBadgeBg)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = role,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF86EFAC) else MomoraMintBadgeText
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email.ifBlank { "admin@willow.example" },
                    fontSize = 12.sp,
                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                )

                if (phone.isNotBlank()) {
                    Text(
                        text = phone,
                        fontSize = 11.5.sp,
                        color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminInfoRow(
    label: String,
    value: String,
    badge: String? = null,
    badgeColor: Color = MomoraPurplePrimary,
    badgeBg: Color = Color(0xFFF3EDF6),
    isDark: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )
        }

        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isDark) Color(0xFF28212F) else badgeBg)
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFD4B3DC) else badgeColor
                )
            }
        }
    }
}

@Composable
private fun AdminInfoDivider(isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(if (isDark) Color(0xFF2B2233) else Color(0xFFF3EEF5))
    )
}

@Composable
private fun AdminEditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isDark: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isDark) Color(0xFF28212F) else Color(0xFFF7F4F9))
                .border(1.dp, if (isDark) Color(0xFF3E3347) else Color(0xFFE2D6E7), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
                ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 13.5.sp,
                            color = if (isDark) Color(0xFF6B6070) else Color(0xFFAFA4B5)
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun AdminReadOnlyField(
    label: String,
    value: String,
    badge: String? = null,
    isDark: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
            )
            if (badge != null) {
                Text(
                    text = badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isDark) Color(0xFF231D2A) else Color(0xFFF2EDF4))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = value,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color(0xFFB5ACB9) else Color(0xFF756779)
            )
        }
    }
}

@Composable
private fun ChangePasswordDialog(
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    errorMessage: String?,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    isDark: Boolean
) {
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Change Admin Password",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter your current password and choose a secure new password (at least 6 characters).",
                    fontSize = 12.5.sp,
                    color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFD32F2F)
                    )
                }

                // Current Password
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = onCurrentPasswordChanged,
                    label = { Text("Current Password") },
                    singleLine = true,
                    visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (showCurrentPassword) "Hide" else "Show",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurplePrimary,
                            modifier = Modifier
                                .clickable { showCurrentPassword = !showCurrentPassword }
                                .padding(8.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // New Password
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChanged,
                    label = { Text("New Password") },
                    singleLine = true,
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (showNewPassword) "Hide" else "Show",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurplePrimary,
                            modifier = Modifier
                                .clickable { showNewPassword = !showNewPassword }
                                .padding(8.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChanged,
                    label = { Text("Confirm New Password") },
                    singleLine = true,
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (showConfirmPassword) "Hide" else "Show",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurplePrimary,
                            modifier = Modifier
                                .clickable { showConfirmPassword = !showConfirmPassword }
                                .padding(8.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSubmit) {
                Text("Update Password", fontWeight = FontWeight.Bold, color = MomoraPurplePrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted)
            }
        }
    )
}

@Composable
private fun LogoutConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoggingOut: Boolean,
    isDark: Boolean
) {
    AlertDialog(
        onDismissRequest = { if (!isLoggingOut) onDismiss() },
        title = {
            Text(
                text = "Sign out from Admin Portal?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isDark) Color(0xFFEDE7EF) else MomoraPurpleDark
            )
        },
        text = {
            Text(
                text = "Are you sure you want to log out? You will need to enter your admin credentials to access system management again.",
                fontSize = 13.sp,
                color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isLoggingOut
            ) {
                if (isLoggingOut) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text("Log Out", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoggingOut
            ) {
                Text("Cancel", color = if (isDark) Color(0xFFB5ACB9) else MomoraBodyMuted)
            }
        }
    )
}

@Composable
private fun AdminBottomNavBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    isDark: Boolean
) {
    val navBg = if (isDark) Color(0xFF1F1A24) else Color.White
    val borderColor = if (isDark) Color(0xFF332A3B) else Color(0xFFEADBEE)
    val indicatorColor = if (isDark) Color(0xFF4A3857) else Color(0xFFDDD2E2)
    val primaryColor = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
    val mutedColor = if (isDark) Color(0xFF8E8394) else Color(0xFF8E8394)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(navBg)
            .border(1.dp, borderColor)
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdminNavTabItem(
                label = "Overview",
                isSelected = selectedIndex == 0,
                onClick = { onTabSelected(0) },
                icon = { GridNavIcon(color = if (selectedIndex == 0) primaryColor else mutedColor, modifier = Modifier.size(20.dp)) },
                isDark = isDark
            )
            AdminNavTabItem(
                label = "Doctors",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { StethoscopeNavIcon(color = if (selectedIndex == 1) primaryColor else mutedColor, modifier = Modifier.size(20.dp)) },
                isDark = isDark
            )
            AdminNavTabItem(
                label = "Patients",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { PeopleNavIcon(color = if (selectedIndex == 2) primaryColor else mutedColor, modifier = Modifier.size(20.dp)) },
                isDark = isDark
            )
            AdminNavTabItem(
                label = "Settings",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { SettingsNavIcon(color = if (selectedIndex == 3) primaryColor else mutedColor, modifier = Modifier.size(20.dp)) },
                isDark = isDark
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
private fun AdminNavTabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    isDark: Boolean
) {
    val activeBg = if (isDark) Color(0xFF382740) else MomoraPurplePrimary.copy(alpha = 0.12f)
    val activeColor = if (isDark) Color(0xFFD4B3DC) else MomoraPurplePrimary
    val inactiveColor = if (isDark) Color(0xFF8E8394) else Color(0xFF8E8394)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) activeBg else Color.Transparent)
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
private fun BackArrowIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.65f, h * 0.20f)
            lineTo(w * 0.35f, h * 0.50f)
            lineTo(w * 0.65f, h * 0.80f)
        }
        drawPath(path, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun KeyNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.28f,
            center = Offset(w * 0.40f, h * 0.40f),
            style = Stroke(width = stroke)
        )
        drawLine(
            color = color,
            start = Offset(w * 0.60f, h * 0.60f),
            end = Offset(w * 0.90f, h * 0.90f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.80f, h * 0.80f),
            end = Offset(w * 0.88f, h * 0.72f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun GridNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()

        drawRoundRect(color = color, topLeft = Offset(w * 0.15f, h * 0.15f), size = androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.30f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()), style = Stroke(stroke))
        drawRoundRect(color = color, topLeft = Offset(w * 0.55f, h * 0.15f), size = androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.30f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()), style = Stroke(stroke))
        drawRoundRect(color = color, topLeft = Offset(w * 0.15f, h * 0.55f), size = androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.30f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()), style = Stroke(stroke))
        drawRoundRect(color = color, topLeft = Offset(w * 0.55f, h * 0.55f), size = androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.30f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()), style = Stroke(stroke))
    }
}

@Composable
private fun StethoscopeNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()

        val tubePath = Path().apply {
            moveTo(w * 0.30f, h * 0.20f)
            lineTo(w * 0.30f, h * 0.45f)
            cubicTo(w * 0.30f, h * 0.75f, w * 0.70f, h * 0.75f, w * 0.70f, h * 0.45f)
            lineTo(w * 0.70f, h * 0.20f)
        }
        drawPath(tubePath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color = color, radius = stroke * 1.5f, center = Offset(w * 0.50f, h * 0.70f))
    }
}

@Composable
private fun PeopleNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()

        drawCircle(color = color, radius = w * 0.18f, center = Offset(w * 0.50f, h * 0.30f), style = Stroke(stroke))
        val bodyPath = Path().apply {
            moveTo(w * 0.20f, h * 0.85f)
            cubicTo(w * 0.20f, h * 0.60f, w * 0.80f, h * 0.60f, w * 0.80f, h * 0.85f)
        }
        drawPath(bodyPath, color = color, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}

@Composable
private fun SettingsNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()

        drawCircle(color = color, radius = w * 0.22f, center = Offset(w * 0.50f, h * 0.50f), style = Stroke(stroke))
        for (i in 0 until 6) {
            val angle = i * (Math.PI / 3.0)
            val startX = (w * 0.50f + Math.cos(angle) * (w * 0.26f)).toFloat()
            val startY = (h * 0.50f + Math.sin(angle) * (h * 0.26f)).toFloat()
            val endX = (w * 0.50f + Math.cos(angle) * (w * 0.42f)).toFloat()
            val endY = (h * 0.50f + Math.sin(angle) * (h * 0.42f)).toFloat()
            drawLine(color = color, start = Offset(startX, startY), end = Offset(endX, endY), strokeWidth = stroke * 1.5f, cap = StrokeCap.Round)
        }
    }
}
