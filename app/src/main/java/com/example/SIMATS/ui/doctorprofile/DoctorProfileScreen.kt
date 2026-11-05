package com.example.SIMATS.ui.doctorprofile

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorProfileData
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraIconBtnBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun DoctorProfileScreen(
    viewModel: DoctorProfileViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    DoctorProfileScreenContent(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onNavigateBack,
        onToggleEditClick = { viewModel.onEvent(DoctorProfileUiEvent.OnToggleEditMode) },
        onCancelEditClick = { viewModel.onEvent(DoctorProfileUiEvent.OnCancelEditClicked) },
        onSaveClick = { viewModel.onEvent(DoctorProfileUiEvent.OnSaveClicked) },
        onRetryClick = { viewModel.onEvent(DoctorProfileUiEvent.OnRetryClicked) },
        onLogoutClick = { viewModel.onEvent(DoctorProfileUiEvent.OnLogoutClicked) },
        onFullNameChanged = { viewModel.onEvent(DoctorProfileUiEvent.OnFullNameChanged(it)) },
        onSpecialtyChanged = { viewModel.onEvent(DoctorProfileUiEvent.OnSpecialtyChanged(it)) },
        onLicenseIdChanged = { viewModel.onEvent(DoctorProfileUiEvent.OnLicenseIdChanged(it)) },
        onClinicChanged = { viewModel.onEvent(DoctorProfileUiEvent.OnClinicChanged(it)) },
        onGenderChanged = { viewModel.onEvent(DoctorProfileUiEvent.OnGenderChanged(it)) },
        onPhoneChanged = { viewModel.onEvent(DoctorProfileUiEvent.OnPhoneChanged(it)) },
        onAddressChanged = { viewModel.onEvent(DoctorProfileUiEvent.OnAddressChanged(it)) },
        onQualificationChanged = { viewModel.onEvent(DoctorProfileUiEvent.OnQualificationChanged(it)) },
        onExperienceYearsChanged = { viewModel.onEvent(DoctorProfileUiEvent.OnExperienceYearsChanged(it)) },
        onPhotoUrlChanged = { viewModel.onEvent(DoctorProfileUiEvent.OnPhotoUrlChanged(it)) },
        onNavTabSelected = { index ->
            viewModel.onEvent(DoctorProfileUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun DoctorProfileScreenContent(
    modifier: Modifier = Modifier,
    uiState: DoctorProfileUiState = DoctorProfileUiState(
        profileData = DoctorProfileData(
            fullName = "Dr Amelia Chen",
            email = "amelia.chen@willow.example",
            specialty = "Obstetrics & Gynecology",
            licenseId = "OBS-19382",
            clinic = "Willow Maternity Clinic",
            gender = "Female",
            phone = "+1 (555) 234-5678",
            address = "456 Healthcare Blvd, Suite 200, Medical Center",
            qualification = "MD, FACOG",
            experienceYears = 12
        ),
        fullName = "Dr Amelia Chen",
        email = "amelia.chen@willow.example",
        specialty = "Obstetrics & Gynecology",
        licenseId = "OBS-19382",
        clinic = "Willow Maternity Clinic",
        gender = "Female",
        phone = "+1 (555) 234-5678",
        address = "456 Healthcare Blvd, Suite 200, Medical Center",
        qualification = "MD, FACOG",
        experienceYears = "12"
    ),
    onBackClick: () -> Unit = {},
    onToggleEditClick: () -> Unit = {},
    onCancelEditClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onFullNameChanged: (String) -> Unit = {},
    onSpecialtyChanged: (String) -> Unit = {},
    onLicenseIdChanged: (String) -> Unit = {},
    onClinicChanged: (String) -> Unit = {},
    onGenderChanged: (String) -> Unit = {},
    onPhoneChanged: (String) -> Unit = {},
    onAddressChanged: (String) -> Unit = {},
    onQualificationChanged: (String) -> Unit = {},
    onExperienceYearsChanged: (String) -> Unit = {},
    onPhotoUrlChanged: (String) -> Unit = {},
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
                    .background(MomoraIconBtnBg)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurplePrimary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Text(
                text = "Doctor Profile",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (uiState.isEditing) MomoraMintBadgeBg else Color(0xFFF3EDF6))
                    .clickable { onToggleEditClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.isEditing) "Viewing" else "Edit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isEditing) MomoraMintBadgeText else MomoraPurplePrimary
                )
            }
        }

        // 2. Main Body Content
        if (uiState.isLoading && uiState.profileData == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MomoraPurplePrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading doctor profile...",
                        fontSize = 13.sp,
                        color = MomoraBodyMuted
                    )
                }
            }
        } else if (uiState.errorMessage != null && uiState.profileData == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFF2DEDE), RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Failed to load doctor profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA94442)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage ?: "Please check your network and try again.",
                        fontSize = 13.sp,
                        color = Color(0xFFA94442),
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
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Success / Error banner
                if (uiState.successMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MomoraMintBadgeBg)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = uiState.successMessage ?: "Changes saved",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MomoraMintBadgeText
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (uiState.errorMessage != null && uiState.profileData != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFF0F0))
                            .border(1.dp, Color(0xFFFFD2D2), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "An error occurred",
                            fontSize = 13.sp,
                            color = Color(0xFFD32F2F)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Doctor Avatar & Header Card
                DoctorProfileHeader(
                    fullName = uiState.fullName,
                    email = uiState.email,
                    specialty = uiState.specialty,
                    clinic = uiState.clinic,
                    photoUrl = uiState.photoUrl
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.isEditing) "Edit Professional Details" else "Professional Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )

                    if (uiState.isEditing) {
                        Text(
                            text = "Editable",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurplePrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Doctor Profile Fields (View or Edit mode)
                if (uiState.isEditing) {
                    EditableField(
                        label = "Full Name",
                        value = uiState.fullName,
                        onValueChange = onFullNameChanged,
                        placeholder = "Dr Amelia Chen"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    EditableField(
                        label = "Specialty",
                        value = uiState.specialty,
                        onValueChange = onSpecialtyChanged,
                        placeholder = "Obstetrics & Gynecology"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    EditableField(
                        label = "Medical License ID",
                        value = uiState.licenseId,
                        onValueChange = onLicenseIdChanged,
                        placeholder = "OBS-19382"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    EditableField(
                        label = "Clinic / Hospital",
                        value = uiState.clinic,
                        onValueChange = onClinicChanged,
                        placeholder = "Willow Maternity Clinic"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    EditableField(
                        label = "Gender",
                        value = uiState.gender,
                        onValueChange = onGenderChanged,
                        placeholder = "Female"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    EditableField(
                        label = "Phone Number",
                        value = uiState.phone,
                        onValueChange = onPhoneChanged,
                        placeholder = "+1 (555) 234-5678",
                        keyboardType = KeyboardType.Phone
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    EditableField(
                        label = "Clinic Address",
                        value = uiState.address,
                        onValueChange = onAddressChanged,
                        placeholder = "456 Healthcare Blvd, Suite 200"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    EditableField(
                        label = "Qualification",
                        value = uiState.qualification,
                        onValueChange = onQualificationChanged,
                        placeholder = "MD, FACOG"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    EditableField(
                        label = "Years of Experience",
                        value = uiState.experienceYears,
                        onValueChange = onExperienceYearsChanged,
                        placeholder = "12",
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    EditableField(
                        label = "Photo URL (Optional)",
                        value = uiState.photoUrl ?: "",
                        onValueChange = onPhotoUrlChanged,
                        placeholder = "https://example.com/photo.jpg"
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Save and Cancel Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF3EDF6))
                                .clickable { onCancelEditClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurpleDark
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MomoraPurplePrimary)
                                .clickable { if (!uiState.isSaving) onSaveClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
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
                } else {
                    // View Mode Information Cards
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFF0E8F4), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        ProfileDetailRow(label = "Specialty", value = uiState.specialty.ifBlank { "Obstetrics" })
                        ProfileDivider()
                        ProfileDetailRow(label = "Medical License", value = uiState.licenseId.ifBlank { "Not provided" })
                        ProfileDivider()
                        ProfileDetailRow(label = "Clinic / Hospital", value = uiState.clinic.ifBlank { "Willow Maternity Clinic" })
                        ProfileDivider()
                        ProfileDetailRow(label = "Gender", value = uiState.gender.ifBlank { "Not specified" })
                        ProfileDivider()
                        ProfileDetailRow(label = "Phone Number", value = uiState.phone.ifBlank { "Not specified" })
                        ProfileDivider()
                        ProfileDetailRow(label = "Clinic Address", value = uiState.address.ifBlank { "Not specified" })
                        ProfileDivider()
                        ProfileDetailRow(label = "Qualification", value = uiState.qualification.ifBlank { "Not specified" })
                        ProfileDivider()
                        ProfileDetailRow(
                            label = "Experience",
                            value = if (uiState.experienceYears.isNotBlank()) "${uiState.experienceYears} years" else "Not specified"
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Edit Profile Primary Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MomoraPurplePrimary)
                            .clickable { onToggleEditClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Edit Profile Details",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Account / Log Out Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFF0E8F4), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Account Session",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Signed in as ${uiState.email}",
                        fontSize = 11.5.sp,
                        color = MomoraBodyMuted
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFF0F0))
                            .border(1.dp, Color(0xFFFFD2D2), RoundedCornerShape(12.dp))
                            .clickable { onLogoutClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Log out from Doctor Portal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // 3. Bottom Navigation Bar (4 Doctor Tabs)
        DoctorBottomNavBar(
            selectedIndex = uiState.selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

@Composable
private fun DoctorProfileHeader(
    fullName: String,
    email: String,
    specialty: String,
    clinic: String,
    photoUrl: String?
) {
    val initials = remember(fullName) {
        val words = fullName.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val raw = words.take(2).map { it.first().uppercaseChar() }.joinToString("")
        raw.ifBlank { "DR" }
    }

    var imageBitmap by remember(photoUrl) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(photoUrl) {
        if (!photoUrl.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val conn = URL(photoUrl).openConnection() as HttpURLConnection
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
            .background(Color(0xFFF9F5FA))
            .border(1.dp, Color(0xFFF0E8F4), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE4F0))
                    .border(2.dp, MomoraPurplePrimary.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = "Doctor Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = initials,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurplePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fullName.ifBlank { "Dr Amelia Chen" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = specialty.ifBlank { "Obstetrics" },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MomoraPurplePrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = clinic.ifBlank { "Willow Maternity Clinic" },
                    fontSize = 11.5.sp,
                    color = MomoraBodyMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MomoraMintBadgeBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Verified Clinician",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraMintBadgeText
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MomoraBodyMuted
        )
        Text(
            text = value,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = MomoraPurpleText,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ProfileDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFF7F2F8))
    )
}

@Composable
private fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MomoraPurpleDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE8DFED), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    color = Color(0xFFBAAFC0)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = MomoraPurpleText
                ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

/**
 * 4 Doctor Bottom Tabs: Home (0), Patients (1), Schedule (2), Advice (3)
 */
@Composable
private fun DoctorBottomNavBar(
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
                label = "Patients",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { PeopleNavIcon(color = if (selectedIndex == 1) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Schedule",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { CalendarNavIcon(color = if (selectedIndex == 2) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Advice",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { ChatNavIcon(color = if (selectedIndex == 3) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
        }
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
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MomoraPurplePrimary else MomoraBodyMuted
        )
    }
}

@Composable
private fun HomeNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val roofPath = Path().apply {
            moveTo(w * 0.15f, h * 0.45f)
            lineTo(w * 0.50f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.45f)
        }
        drawPath(roofPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val bodyPath = Path().apply {
            moveTo(w * 0.25f, h * 0.42f)
            lineTo(w * 0.25f, h * 0.85f)
            lineTo(w * 0.75f, h * 0.85f)
            lineTo(w * 0.75f, h * 0.42f)
        }
        drawPath(bodyPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
private fun PeopleNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(color = color, radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.30f), style = Stroke(width = strokeWidth))

        val bodyPath = Path().apply {
            moveTo(w * 0.20f, h * 0.85f)
            cubicTo(w * 0.20f, h * 0.60f, w * 0.80f, h * 0.60f, w * 0.80f, h * 0.85f)
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

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun DoctorProfileScreenPreview() {
    DoctorProfileScreenContent()
}

