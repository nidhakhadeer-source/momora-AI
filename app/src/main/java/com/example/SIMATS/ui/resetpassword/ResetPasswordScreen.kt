package com.example.SIMATS.ui.resetpassword

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.R
import com.example.SIMATS.ui.theme.MomoraAITheme
import com.example.SIMATS.ui.theme.MomoraLavenderCard
import com.example.SIMATS.ui.theme.MomoraMintLight
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraTealPrimary
import com.example.SIMATS.ui.theme.MomoraTextBody
import com.example.SIMATS.ui.theme.MomoraTextHeadline
import com.example.SIMATS.ui.theme.MomoraTextMuted

@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute, uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.onEvent(ResetPasswordUiEvent.OnNavigationHandled)
        } else {
            uiState.navigateToRoute?.let { route ->
                onNavigateTo(route)
                viewModel.onEvent(ResetPasswordUiEvent.OnNavigationHandled)
            }
        }
    }

    ResetPasswordScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun ResetPasswordScreenContent(
    uiState: ResetPasswordUiState,
    onEvent: (ResetPasswordUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Bar: Back Arrow | momora AI | More Options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onEvent(ResetPasswordUiEvent.OnBackClicked) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_arrow),
                        contentDescription = "Back",
                        tint = MomoraTextHeadline,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "momora AI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraTealPrimary
                )

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more_dots),
                        contentDescription = "More Options",
                        tint = MomoraTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Headline: Let's get you back in
            Text(
                text = "Let's get you back in",
                style = MaterialTheme.typography.headlineLarge,
                color = MomoraTextHeadline,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Sub-caption
            Text(
                text = "It happens. We'll help you reset your password.",
                style = MaterialTheme.typography.bodyLarge,
                color = MomoraTextBody
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Lavender Inbox Check Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MomoraLavenderCard)
                    .padding(20.dp)
            ) {
                Column {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email_check),
                        contentDescription = null,
                        tint = MomoraTealPrimary,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Check your inbox",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraTextHeadline,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "If an account exists for this email, we've sent a secure password reset link.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MomoraTextBody,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Field 1: Email address
            Text(
                text = "Email address",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MomoraTextHeadline
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { onEvent(ResetPasswordUiEvent.OnEmailChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = MomoraTealPrimary,
                    unfocusedBorderColor = Color(0xFFE5E7E6),
                    focusedTextColor = MomoraTextHeadline,
                    unfocusedTextColor = MomoraTextHeadline
                ),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = null,
                        tint = MomoraTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Button: Resend reset link
            Button(
                onClick = { onEvent(ResetPasswordUiEvent.OnResendResetLinkClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MomoraTealPrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Resend reset link",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mint Green Security Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MomoraMintLight)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shield_check),
                        contentDescription = null,
                        tint = MomoraTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "A link just for you",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraTealPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "The reset link expires in 30 minutes. Don't share it with anyone, including Momora support.",
                            fontSize = 12.sp,
                            color = MomoraTextBody,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Help Text Paragraph
            Text(
                text = "Can't find it? Check your spam folder or make sure you've entered the email used to sign up.",
                style = MaterialTheme.typography.bodySmall,
                color = MomoraTextMuted,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Secondary Button: Back to log in
            Button(
                onClick = { onEvent(ResetPasswordUiEvent.OnBackToLoginClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MomoraMintLight,
                    contentColor = MomoraTealPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Back to log in",
                    style = MaterialTheme.typography.labelLarge,
                    color = MomoraTealPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer Link: Need help? Contact support
            Text(
                text = "Need help? Contact support",
                style = MaterialTheme.typography.bodySmall,
                color = MomoraTextMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(ResetPasswordUiEvent.OnContactSupportClicked) },
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPasswordScreenPreview() {
    MomoraAITheme {
        ResetPasswordScreenContent(
            uiState = ResetPasswordUiState(),
            onEvent = {}
        )
    }
}
