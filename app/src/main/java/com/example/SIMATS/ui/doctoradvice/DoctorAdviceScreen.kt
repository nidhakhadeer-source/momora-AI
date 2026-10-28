package com.example.SIMATS.ui.doctoradvice

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
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.DoctorAdviceData
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun DoctorAdviceScreen(
    viewModel: DoctorAdviceViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    DoctorAdviceScreenContent(
        modifier = modifier,
        uiState = uiState,
        onSubjectChanged = { s -> viewModel.onEvent(DoctorAdviceUiEvent.OnSubjectChanged(s)) },
        onAdviceTextChanged = { t -> viewModel.onEvent(DoctorAdviceUiEvent.OnAdviceTextChanged(t)) },
        onSaveCopyToggled = { c -> viewModel.onEvent(DoctorAdviceUiEvent.OnSaveCopyToggled(c)) },
        onSendAdviceClick = { viewModel.onEvent(DoctorAdviceUiEvent.OnSendAdviceClicked) },
        onBackClick = {
            viewModel.onEvent(DoctorAdviceUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(DoctorAdviceUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(DoctorAdviceUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun DoctorAdviceScreenContent(
    modifier: Modifier = Modifier,
    uiState: DoctorAdviceUiState = DoctorAdviceUiState(
        adviceData = DoctorAdviceData(
            doctorName = "Dr Amelia Chen",
            patientName = "Maya Patel",
            patientInitials = "MP",
            gestationalAge = "24w 3d",
            dueDateText = "Due 13 Feb 2027",
            patientIdText = "P-13042",
            patientMessageTimeText = "PATIENT MESSAGE · 27 OCT, 08:35",
            patientMessageQuote = "\"I've had mild heartburn after lunch. What can I try?\"",
            defaultSubject = "Heartburn: gentle steps & follow-up",
            defaultAdviceText = "Hi Maya, try smaller meals and stay upright after eating. Let's review this at your 29 Oct visit. Contact the clinic sooner if symptoms persist or worsen.\n\nFor severe chest or abdominal pain, seek urgent maternity care or local emergency services."
        )
    ),
    onSubjectChanged: (String) -> Unit = {},
    onAdviceTextChanged: (String) -> Unit = {},
    onSaveCopyToggled: (Boolean) -> Unit = {},
    onSendAdviceClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.adviceData

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
                    color = MomoraPurpleText
                )
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
                text = "Consultation & advice",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${data?.doctorName ?: "Dr Amelia Chen"} · Private advice · Draft, not sent",
                fontSize = 12.5.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Patient Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF4ECF8))
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = data?.patientInitials ?: "MP",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurplePrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = data?.patientName ?: "Maya Patel",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurpleDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${data?.gestationalAge ?: "24w 3d"} · ${data?.dueDateText ?: "Due 13 Feb 2027"}",
                                fontSize = 11.5.sp,
                                color = MomoraBodyMuted
                            )
                        }
                    }

                    Text(
                        text = data?.patientIdText ?: "P-13042",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurplePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Patient Message Quote Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MomoraPinkCircleBg)
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = data?.patientMessageTimeText ?: "PATIENT MESSAGE · 27 OCT, 08:35",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraBodyMuted,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = data?.patientMessageQuote ?: "\"I've had mild heartburn after lunch. What can I try?\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MomoraPurpleDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subject Input Field
            Text(
                text = "Subject",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                BasicTextField(
                    value = uiState.subjectText,
                    onValueChange = onSubjectChanged,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MomoraPurpleDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Your Advice Input Area
            Text(
                text = "Your advice",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                BasicTextField(
                    value = uiState.adviceText,
                    onValueChange = onAdviceTextChanged,
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = TextStyle(
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                        color = MomoraPurpleDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Save Copy Checkbox Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSaveCopyToggled(!uiState.isSaveCopyChecked) }
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (uiState.isSaveCopyChecked) MomoraPurplePrimary else Color.Transparent)
                        .border(
                            1.dp,
                            if (uiState.isSaveCopyChecked) MomoraPurplePrimary else Color(0xFF8C7F92),
                            RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isSaveCopyChecked) {
                        Text(
                            text = "✓",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Save a copy in Maya's consultation record.",
                    fontSize = 12.sp,
                    color = MomoraPurpleDark
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Send advice securely Primary Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { if (!uiState.isSending) onSendAdviceClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.isSending) "Sending..." else if (uiState.isSentSuccess) "Advice Sent ✓" else "Send advice securely",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🚀",
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Non-Continuous Messaging Warning Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF4ECF8))
                    .padding(horizontal = 14.dp, vertical = 11.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEBE0F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        ShieldIcon(
                            color = MomoraPurplePrimary,
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Messaging is not monitored continuously. Do not use it for urgent or emergency care.",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Doctor Bottom Navigation Bar (4 Doctor Tabs)
        DoctorBottomNavBar(
            selectedIndex = uiState.selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

/**
 * Doctor Bottom Navigation Bar.
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
fun DoctorAdviceScreenPreview() {
    DoctorAdviceScreenContent()
}
