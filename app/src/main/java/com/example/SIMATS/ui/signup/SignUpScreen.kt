package com.example.SIMATS.ui.signup

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
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraIconBtnBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onRegistrationSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isRegistrationSuccess) {
        onRegistrationSuccess()
    }

    SignUpScreenContent(
        modifier = modifier,
        uiState = uiState,
        onFullNameChanged = { name -> viewModel.onEvent(SignUpUiEvent.OnFullNameChanged(name)) },
        onEmailChanged = { email -> viewModel.onEvent(SignUpUiEvent.OnEmailChanged(email)) },
        onDateOfBirthChanged = { dob -> viewModel.onEvent(SignUpUiEvent.OnDateOfBirthChanged(dob)) },
        onPasswordChanged = { pass -> viewModel.onEvent(SignUpUiEvent.OnPasswordChanged(pass)) },
        onConfirmPasswordChanged = { pass -> viewModel.onEvent(SignUpUiEvent.OnConfirmPasswordChanged(pass)) },
        onTogglePasswordVisibility = { viewModel.onEvent(SignUpUiEvent.OnTogglePasswordVisibility) },
        onToggleConfirmPasswordVisibility = { viewModel.onEvent(SignUpUiEvent.OnToggleConfirmPasswordVisibility) },
        onTermsAgreedToggled = { agreed -> viewModel.onEvent(SignUpUiEvent.OnTermsAgreedToggled(agreed)) },
        onRegisterClick = { viewModel.onEvent(SignUpUiEvent.OnRegisterClicked) },
        onLoginClick = {
            viewModel.onEvent(SignUpUiEvent.OnLoginClicked)
            onNavigateToLogin()
        },
        onBackClick = {
            viewModel.onEvent(SignUpUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onHelpClick = { viewModel.onEvent(SignUpUiEvent.OnHelpClicked) }
    )
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState = SignUpUiState(),
    onFullNameChanged: (String) -> Unit = {},
    onEmailChanged: (String) -> Unit = {},
    onDateOfBirthChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onConfirmPasswordChanged: (String) -> Unit = {},
    onTogglePasswordVisibility: () -> Unit = {},
    onToggleConfirmPasswordVisibility: () -> Unit = {},
    onTermsAgreedToggled: (Boolean) -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
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

        // 2. Scrollable Body Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Begin your journey",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Create your personal patient account.",
                fontSize = 13.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Field 1: Full name
            FormFieldLabel(text = "Full name")
            CustomInputField(
                value = uiState.fullName,
                onValueChange = onFullNameChanged,
                placeholder = "Enter your full name"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Field 2: Email address
            FormFieldLabel(text = "Email address")
            CustomInputField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                placeholder = "Enter your email address",
                keyboardType = KeyboardType.Email,
                trailingIcon = { MailIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Field 3: Date of birth
            FormFieldLabel(text = "Date of birth")
            CustomInputField(
                value = uiState.dateOfBirth,
                onValueChange = onDateOfBirthChanged,
                placeholder = "DD Month YYYY",
                trailingIcon = { CalendarIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Field 4: Password
            FormFieldLabel(text = "Password")
            CustomInputField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                placeholder = "Enter password",
                keyboardType = KeyboardType.Password,
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Box(modifier = Modifier.clickable { onTogglePasswordVisibility() }) {
                        EyeSlashIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                    }
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "12+ characters, with a number and symbol.",
                fontSize = 11.sp,
                color = MomoraBodyMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Field 5: Confirm password
            FormFieldLabel(text = "Confirm password")
            CustomInputField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChanged,
                placeholder = "Re-enter password",
                keyboardType = KeyboardType.Password,
                visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Box(modifier = Modifier.clickable { onToggleConfirmPasswordVisibility() }) {
                        EyeSlashIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Terms Agreement Checkbox Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTermsAgreedToggled(!uiState.isTermsAgreed) },
                verticalAlignment = Alignment.Top
            ) {
                // Checkbox Box
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (uiState.isTermsAgreed) MomoraPurplePrimary else Color.White)
                        .border(1.5.dp, MomoraPurplePrimary, RoundedCornerShape(5.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isTermsAgreed) {
                        Text(
                            text = "✓",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "I agree to the Terms and Privacy Notice, including processing sensitive health data for my pregnancy care.",
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF43524E)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Care Team Privacy Pill Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF4ECF8))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEBE0F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        ShieldIcon(
                            color = MomoraPurplePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Sharing with your care team is optional and can be managed in your profile.",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    fontSize = 12.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // 3. Bottom Action Controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Create patient account Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MomoraPurplePrimary)
                    .clickable { if (!uiState.isLoading) onRegisterClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.isLoading) "Creating account..." else "Create patient account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Already registered? Log in
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already registered? ",
                    fontSize = 12.5.sp,
                    color = MomoraBodyMuted
                )
                Text(
                    text = "Log in",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurplePrimary,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Home Bar Line Indicator
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

@Composable
private fun FormFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MomoraPurpleText
    )
    Spacer(modifier = Modifier.height(5.dp))
}

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
 * Calendar Icon Drawing.
 */
@Composable
private fun CalendarIcon(
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
                    rect = Rect(w * 0.12f, h * 0.20f, w * 0.88f, h * 0.85f),
                    cornerRadius = CornerRadius(w * 0.08f, h * 0.08f)
                )
            )
        }
        drawPath(rectPath, color = color, style = Stroke(width = strokeWidth))

        // Top bar line
        val linePath = Path().apply {
            moveTo(w * 0.12f, h * 0.40f)
            lineTo(w * 0.88f, h * 0.40f)
        }
        drawPath(linePath, color = color, style = Stroke(width = strokeWidth))

        // Grid dots
        drawCircle(color = color, radius = strokeWidth * 0.8f, center = Offset(w * 0.35f, h * 0.60f))
        drawCircle(color = color, radius = strokeWidth * 0.8f, center = Offset(w * 0.65f, h * 0.60f))
    }
}

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
private fun ShieldIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
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

        drawPath(
            path = shieldPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val checkPath = Path().apply {
            moveTo(w * 0.38f, h * 0.50f)
            lineTo(w * 0.48f, h * 0.60f)
            lineTo(w * 0.64f, h * 0.40f)
        }
        drawPath(
            path = checkPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
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
fun SignUpScreenPreview() {
    SignUpScreenContent()
}
