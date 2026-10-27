package com.example.SIMATS.ui.ai

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.R
import com.example.SIMATS.ui.home.BottomNavBar
import com.example.SIMATS.ui.theme.MomoraAITheme
import com.example.SIMATS.ui.theme.MomoraMintLight
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraSubheaderPurple
import com.example.SIMATS.ui.theme.MomoraTealPrimary
import com.example.SIMATS.ui.theme.MomoraTextBody
import com.example.SIMATS.ui.theme.MomoraTextHeadline
import com.example.SIMATS.ui.theme.MomoraTextMuted

@Composable
fun AiChatScreen(
    viewModel: AiChatViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute, uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.onEvent(AiChatUiEvent.OnNavigationHandled)
        } else {
            uiState.navigateToRoute?.let { route ->
                onNavigateTo(route)
                viewModel.onEvent(AiChatUiEvent.OnNavigationHandled)
            }
        }
    }

    AiChatScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun AiChatScreenContent(
    uiState: AiChatUiState,
    onEvent: (AiChatUiEvent) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { onEvent(AiChatUiEvent.OnTabSelected(it)) }
            )
        },
        containerColor = MomoraScreenBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Header Bar: Back Arrow | momora AI | History
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onEvent(AiChatUiEvent.OnBackClicked) },
                        modifier = Modifier.size(32.dp)
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
                        onClick = { onEvent(AiChatUiEvent.OnHistoryClicked) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_history),
                            contentDescription = "Chat History",
                            tint = MomoraTextHeadline,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Headline: A little guidance, anytime
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MomoraTextHeadline,
                    fontSize = 26.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MomoraTextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Date Header Divider
                Text(
                    text = uiState.dateHeader,
                    style = MaterialTheme.typography.labelMedium,
                    color = MomoraSubheaderPurple,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Messages Flow
                uiState.chatMessages.forEach { msg ->
                    if (msg.sender == "You") {
                        // User Bubble Right
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = 16.dp,
                                            bottomEnd = 4.dp
                                        )
                                    )
                                    .background(MomoraMintLight)
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = msg.text,
                                        fontSize = 14.sp,
                                        color = MomoraTextHeadline,
                                        lineHeight = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "${msg.sender} · ${msg.timestamp}",
                                        fontSize = 11.sp,
                                        color = MomoraTextMuted
                                    )
                                }
                            }
                        }
                    } else {
                        // Momora AI Card Left
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White)
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_sparkles),
                                        contentDescription = null,
                                        tint = MomoraTealPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = msg.sender,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MomoraTealPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = msg.text,
                                    fontSize = 14.sp,
                                    color = MomoraTextHeadline,
                                    lineHeight = 20.sp
                                )

                                if (msg.bulletPoints.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    msg.bulletPoints.forEach { point ->
                                        Text(
                                            text = "• $point",
                                            fontSize = 14.sp,
                                            color = MomoraTextHeadline,
                                            lineHeight = 20.sp,
                                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                                        )
                                    }
                                }

                                if (msg.footerNote != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = msg.footerNote,
                                        fontSize = 14.sp,
                                        color = MomoraTextHeadline,
                                        lineHeight = 20.sp
                                    )
                                }

                                if (msg.citationText != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = msg.citationText,
                                        fontSize = 11.sp,
                                        color = MomoraTextMuted
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Feedback Action Icons Row
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { onEvent(AiChatUiEvent.OnFeedbackThumbUp(msg.id)) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_thumb_up),
                                            contentDescription = "Thumb Up",
                                            tint = MomoraTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onEvent(AiChatUiEvent.OnFeedbackThumbDown(msg.id)) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_thumb_down),
                                            contentDescription = "Thumb Down",
                                            tint = MomoraTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onEvent(AiChatUiEvent.OnCopyMessage(msg.id)) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_copy),
                                            contentDescription = "Copy Message",
                                            tint = MomoraTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Suggested Prompts Chips Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    uiState.suggestedPrompts.forEach { prompt ->
                        Box(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { onEvent(AiChatUiEvent.OnSuggestedPromptClicked(prompt)) }
                                .padding(horizontal = 18.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = prompt,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MomoraTextHeadline
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AI Disclaimer Note
                Text(
                    text = uiState.aiDisclaimer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MomoraTextMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input Message Box
                Text(
                    text = "Your message",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MomoraTextHeadline
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.currentInputText,
                    onValueChange = { onEvent(AiChatUiEvent.OnInputTextChanged(it)) },
                    placeholder = { Text(text = "Ask what's on your mind...", color = MomoraTextMuted) },
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
                        IconButton(onClick = { onEvent(AiChatUiEvent.OnSendMessageClicked) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send),
                                contentDescription = "Send Message",
                                tint = MomoraTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AiChatScreenPreview() {
    MomoraAITheme {
        AiChatScreenContent(
            uiState = AiChatUiState(),
            onEvent = {}
        )
    }
}
