package com.example.SIMATS.ui.menu

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.navigation.Screen
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraScreenBackground

data class MenuItemData(
    val screenNumber: String,
    val title: String,
    val subtitle: String,
    val screen: Screen,
    val isImplemented: Boolean = true
)

@Composable
fun ScreenMenuScreen(
    onNavigateToScreen: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val patientScreens = listOf(
        MenuItemData("01", "Splash Screen", "Initial branding and entry", Screen.Splash),
        MenuItemData("02", "Onboarding Welcome", "Welcome and feature showcase", Screen.Onboarding),
        MenuItemData("03", "Pregnancy Insights", "Feature highlights & getting started", Screen.Onboarding),
        MenuItemData("04", "Role-Based Login", "Patient, Doctor, Admin sign-in", Screen.Login),
        MenuItemData("05", "Patient Registration", "New account registration form", Screen.SignUp),
        MenuItemData("06", "Patient Dashboard", "Main overview, daily progress & actions", Screen.Home),
        MenuItemData("07", "Patient Profile", "Personal info, due date & preferences", Screen.Profile),
        MenuItemData("08", "Pregnancy Details", "Trimester, weeks & due date tracker", Screen.DueDate),
        MenuItemData("09", "Health Monitoring", "Vitals summary & health reports", Screen.HealthReport),
        MenuItemData("10", "Weight Tracker", "Weekly weight logging & chart", Screen.WeightTracker),
        MenuItemData("11", "Blood Pressure Tracker", "Systolic/Diastolic logs & trend chart", Screen.BloodPressure),
        MenuItemData("12", "Baby Growth Tracker", "Fetal development size & milestones", Screen.BabyDevelopment),
        MenuItemData("13", "AI Pregnancy Chat", "AI assistant Q&A and advice", Screen.AiChat),
        MenuItemData("14", "Symptom Checker", "Log symptoms and intensity", Screen.Symptom),
        MenuItemData("15", "Nutrition Recommendations", "Trimester meal ideas & guidelines", Screen.Nutrition),
        MenuItemData("16", "Medication Reminders", "Daily routine & pill tracking", Screen.Medication),
        MenuItemData("17", "Appointment Booking", "Select doctor, date & time slot", Screen.AppointmentsCalendar),
        MenuItemData("18", "Medical Reports", "Upload and view lab/ultrasound files", Screen.MedicalReports),
        MenuItemData("19", "Notifications & Alerts", "Reminders and care team updates", Screen.Notifications)
    )

    val doctorScreens = listOf(
        MenuItemData("20", "Doctor Dashboard", "Assigned patients & daily visit summary", Screen.DoctorDashboard),
        MenuItemData("21", "Assigned Patient List", "Filter and search patient accounts", Screen.AssignedPatients),
        MenuItemData("22", "Patient Medical History", "Clinical background & recent records", Screen.PatientHistory),
        MenuItemData("23", "Patient Health Monitoring", "Home vitals trends & 7-day BP graph", Screen.PatientHealthMonitoring),
        MenuItemData("24", "Doctor Advice & Consultation", "Write and send secure advice", Screen.DoctorAdvice),
        MenuItemData("25", "Doctor Appointment Book", "Daily schedule & clinic availability", Screen.DoctorSchedule)
    )

    val adminScreens = listOf(
        MenuItemData("26", "Admin Dashboard", "System stats, registrations & accounts", Screen.AdminDashboard),
        MenuItemData("27", "Add Doctor", "Admin doctor account creation form", Screen.AddDoctor),
        MenuItemData("28", "Manage Doctors", "List, edit & deactivate doctor accounts", Screen.ManageDoctors),
        MenuItemData("29", "Manage Patients", "List, search & manage patient access", Screen.ManagePatients),
        MenuItemData("30", "Admin Settings", "Security, MFA, audit log & session control", Screen.AdminSettings)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Color(0xFFEFE8F4))
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Column {
                Text(
                    text = "Momora AI",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Centralized Navigation · 30 Figma Screens",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MomoraPurplePrimary
                )
            }
        }

        // Scrollable Screen Categories
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Category 1: Patient Experience
            CategorySection(
                title = "Patient Experience (Screens 01 – 19)",
                badgeColor = MomoraPinkCircleBg,
                items = patientScreens,
                onNavigateToScreen = onNavigateToScreen
            )

            // Category 2: Doctor Portal
            CategorySection(
                title = "Doctor Portal (Screens 20 – 25)",
                badgeColor = MomoraMintBadgeBg,
                items = doctorScreens,
                onNavigateToScreen = onNavigateToScreen
            )

            // Category 3: Admin Portal
            CategorySection(
                title = "Admin Portal (Screens 26 – 30)",
                badgeColor = Color(0xFFEDE4F0),
                items = adminScreens,
                onNavigateToScreen = onNavigateToScreen
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun CategorySection(
    title: String,
    badgeColor: Color,
    items: List<MenuItemData>,
    onNavigateToScreen: (Screen) -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(badgeColor)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                ScreenMenuItemCard(
                    item = item,
                    onClick = {
                        if (item.isImplemented) {
                            onNavigateToScreen(item.screen)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ScreenMenuItemCard(
    item: MenuItemData,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
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
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF4ECF8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.screenNumber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurplePrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.subtitle,
                        fontSize = 11.sp,
                        color = MomoraBodyMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (item.isImplemented) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MomoraMintBadgeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Open ➔",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraMintBadgeText
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFFFEAEA))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Not Implemented",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun ScreenMenuScreenPreview() {
    ScreenMenuScreen(onNavigateToScreen = {})
}
