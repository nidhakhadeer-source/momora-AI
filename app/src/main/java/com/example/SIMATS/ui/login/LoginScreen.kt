package com.example.SIMATS.ui.login

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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.UserRole
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraIconBtnBg
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoginSuccess) {
        onLoginSuccess()
    }

    LoginScreenContent(
        modifier = modifier,
        uiState = uiState,
        onRoleSelected = { role -> viewModel.onEvent(LoginUiEvent.OnRoleSelected(role)) },
        onEmailChanged = { email -> viewModel.onEvent(LoginUiEvent.OnEmailChanged(email)) },
        onPasswordChanged = { pass -> viewModel.onEvent(LoginUiEvent.OnPasswordChanged(pass)) },
        onTogglePasswordVisibility = { viewModel.onEvent(LoginUiEvent.OnTogglePasswordVisibility) },
        onForgotPasswordClick = { viewModel.onEvent(LoginUiEvent.OnForgotPasswordClicked) },
        onLoginClick = { viewModel.onEvent(LoginUiEvent.OnLoginClicked) },
        onCreateAccountClick = {
            viewModel.onEvent(LoginUiEvent.OnCreateAccountClicked)
            onCreateAccountClick()
        },
        onBackClick = {
            viewModel.onEvent(LoginUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onHelpClick = { viewModel.onEvent(LoginUiEvent.OnHelpClicked) }
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState = LoginUiState(),
    onRoleSelected: (UserRole) -> Unit = {},
    onEmailChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onTogglePasswordVisibility: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onHelpClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                    .background(MomoraIconBtnBg)
                    .clickable { onHelpClick() },
                contentAlignment = Alignment.Center
            ) {
                TopBarHelpIcon(
                    color = MomoraPurplePrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 2. Scrollable Content Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Header Title & Subtitle
            Text(
                text = "Welcome back",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "A secure space for every part of your care.",
                fontSize = 13.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Role Segmented Control (Patient, Doctor, Admin)
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
                    UserRole.entries.forEach { role ->
                        val isSelected = uiState.selectedRole == role
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { onRoleSelected(role) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = role.label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MomoraPurpleDark else MomoraBodyMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Role Info Banner Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MomoraPinkCircleBg)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column {
                    Text(
                        text = uiState.selectedRole.bannerTitle,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = uiState.selectedRole.bannerSubtitle,
                        fontSize = 12.5.sp,
                        color = MomoraBodyMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Form Field 1: Email Address
            Text(
                text = "Email address",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleText
            )

            Spacer(modifier = Modifier.height(6.dp))

            CustomInputField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                placeholder = "Enter your email address",
                keyboardType = KeyboardType.Email,
                trailingIcon = { MailIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp)) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Form Field 2: Password
            Text(
                text = "Password",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleText
            )

            Spacer(modifier = Modifier.height(6.dp))

            CustomInputField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                placeholder = "Enter your password",
                keyboardType = KeyboardType.Password,
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Box(
                        modifier = Modifier.clickable { onTogglePasswordVisibility() }
                    ) {
                        EyeSlashIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password Link
            Text(
                text = "Forgot password?",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = MomoraPurplePrimary,
                modifier = Modifier.clickable { onForgotPasswordClick() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Error Message (if any)
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    fontSize = 12.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // 3. Bottom Actions & Footer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Log in securely Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { if (!uiState.isLoading) onLoginClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (uiState.isLoading) "Logging in..." else "Log in securely",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (!uiState.isLoading) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "➔",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Create Account Prompt Link
            Text(
                text = uiState.selectedRole.createAccountText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurplePrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { onCreateAccountClick() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Privacy Notice Footer
            Text(
                text = "Your health information is private. Privacy Notice & Terms.",
                fontSize = 11.sp,
                color = MomoraFooterText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Home Bar Indicator Line
            Box(
                modifier = Modifier
                    .width(134.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF333333))
            )
        }
    }
}

/**
 * Custom Input Text Field with white background and soft purple border.
 */
@Composable
private fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(14.dp))
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
                        fontSize = 13.5.sp,
                        color = Color(0xFFA098A6)
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation = visualTransformation,
                    textStyle = TextStyle(
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = MomoraPurpleText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            trailingIcon()
        }
    }
}

/**
 * Mail Envelope Icon drawing.
 */
@Composable
private fun MailIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val rectPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(w * 0.10f, h * 0.20f, w * 0.90f, h * 0.80f),
                    cornerRadius = CornerRadius(w * 0.10f, h * 0.10f)
                )
            )
        }
        drawPath(rectPath, color = color, style = Stroke(width = strokeWidth))

        val flapPath = Path().apply {
            moveTo(w * 0.12f, h * 0.25f)
            lineTo(w * 0.50f, h * 0.55f)
            lineTo(w * 0.88f, h * 0.25f)
        }
        drawPath(flapPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * Eye Slash / Password Visibility Toggle Icon drawing.
 */
@Composable
private fun EyeSlashIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
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
private fun TopBarHelpIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = size.minDimension * 0.45f,
            style = Stroke(width = strokeWidth)
        )

        val qPath = Path().apply {
            moveTo(w * 0.38f, h * 0.38f)
            cubicTo(
                w * 0.38f, h * 0.28f,
                w * 0.62f, h * 0.28f,
                w * 0.62f, h * 0.42f
            )
            cubicTo(
                w * 0.62f, h * 0.52f,
                w * 0.50f, h * 0.52f,
                w * 0.50f, h * 0.62f
            )
        }
        drawPath(
            path = qPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawCircle(
            color = color,
            radius = strokeWidth * 0.8f,
            center = Offset(w * 0.50f, h * 0.72f)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent()
}
