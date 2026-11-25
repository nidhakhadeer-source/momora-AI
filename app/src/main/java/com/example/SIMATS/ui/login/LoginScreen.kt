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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.SIMATS.ui.components.formatErrorMessage
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.UserRole
import com.example.SIMATS.ui.theme.InterFontFamily
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
    role: UserRole = UserRole.PATIENT,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onLoginSuccess: (UserRole) -> Unit = {},
    onCreateAccountClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(role) {
        viewModel.setRole(role)
    }

    if (uiState.isLoginSuccess) {
        onLoginSuccess(uiState.selectedRole)
    }

    LoginScreenContent(
        modifier = modifier,
        uiState = uiState,
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
        }
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState = LoginUiState(),
    onEmailChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onTogglePasswordVisibility: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val screenTitle = when (uiState.selectedRole) {
        UserRole.PATIENT -> "Patient login"
        UserRole.DOCTOR -> "Doctor login"
        UserRole.ADMIN -> "Admin login"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Bar (Back button only; "?" help icon removed)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Start,
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
                    color = MaterialTheme.colorScheme.onBackground
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
            // Header Title (Role-specific title)
            Text(
                text = screenTitle,
                fontFamily = InterFontFamily,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Form Field 1: Email Address
            Text(
                text = "Email address",
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            CustomInputField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                placeholder = "Enter your email address",
                keyboardType = KeyboardType.Email,
                trailingIcon = { MailIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Form Field 2: Password
            Text(
                text = "Password",
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
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
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onTogglePasswordVisibility() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isPasswordVisible) {
                            EyeIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        } else {
                            EyeSlashIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            )

            // Forgot Password Link (Hidden on Admin login only)
            if (uiState.selectedRole != UserRole.ADMIN) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Forgot password?",
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onForgotPasswordClick() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error Message (if any)
            if (uiState.errorMessage != null) {
                Text(
                    text = formatErrorMessage(uiState.errorMessage),
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // 3. Bottom Actions
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
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { if (!uiState.isLoading) onLoginClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (uiState.isLoading) "Logging in..." else "Log in securely",
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    if (!uiState.isLoading) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "➔",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Role-specific bottom prompt
            when (uiState.selectedRole) {
                UserRole.PATIENT -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "New patient? Create a patient account",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { onCreateAccountClick() }
                    )
                }
                UserRole.DOCTOR -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Request provider access",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { /* clinician access info */ }
                    )
                }
                UserRole.ADMIN -> {
                    // No registration link for admin
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Home Bar Indicator Line
            Box(
                modifier = Modifier
                    .width(134.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            )
        }
    }
}

/**
 * Custom Input Text Field with theme-aware surface background and outline border.
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
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
                        color = MaterialTheme.colorScheme.onSurface
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
 * Eye Open / Password Visible Icon drawing.
 */
@Composable
private fun EyeIcon(
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
    }
}

/**
 * Eye Slash / Password Hidden Toggle Icon drawing.
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

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent()
}
