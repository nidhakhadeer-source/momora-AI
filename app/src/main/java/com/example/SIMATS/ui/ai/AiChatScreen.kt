package com.example.SIMATS.ui.ai

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.ChatMessage
import com.example.SIMATS.data.model.MessageSender
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraSubTitleText
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AiChatScreen(
    viewModel: AiChatViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 2,
    onNavigateBack: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    AiChatScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onRetryClick = { viewModel.onEvent(AiChatUiEvent.OnRetryClicked) },
        onInputQueryChanged = { q -> viewModel.onEvent(AiChatUiEvent.OnInputQueryChanged(q)) },
        onSendMessageClick = { viewModel.onEvent(AiChatUiEvent.OnSendMessageClicked) },
        onSuggestionClick = { s -> viewModel.onEvent(AiChatUiEvent.OnSuggestionClicked(s)) },
        onBackClick = {
            viewModel.onEvent(AiChatUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(AiChatUiEvent.OnNotificationClicked) },
        onNavTabSelected = { index ->
            viewModel.onEvent(AiChatUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun AiChatScreenContent(
    modifier: Modifier = Modifier,
    uiState: AiChatUiState = AiChatUiState(
        messages = listOf(
            ChatMessage(
                id = "1",
                sender = MessageSender.USER,
                text = "What can I try for heartburn after lunch?",
                timestampText = "You · 08:20"
            ),
            ChatMessage(
                id = "2",
                sender = MessageSender.AI,
                text = "That sounds uncomfortable, Mama. Here are a few gentle things that can help relieve heartburn:",
                bulletPoints = listOf(
                    "Eat smaller, more frequent meals rather than large portions.",
                    "Stay upright for at least 30–60 minutes after eating.",
                    "Sip a little warm water or cold milk to soothe acidity."
                ),
                footerNote = "Momora AI educational guidance · Consult your doctor for clinical advice.",
                timestampText = "Momora AI · 08:20"
            )
        )
    ),
    onRetryClick: () -> Unit = {},
    onInputQueryChanged: (String) -> Unit = {},
    onSendMessageClick: () -> Unit = {},
    onSuggestionClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    selectedNavIndex: Int = 2,
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    // Smooth two-phase auto-scroll when message count or loading status changes
    LaunchedEffect(uiState.messages.size, uiState.isLoading) {
        if (uiState.messages.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
            delay(150)
            if (scrollState.value < scrollState.maxValue) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Premium Compact Header
        AiChatHeader(
            onBackClick = onBackClick,
            onNotificationClick = onNotificationClick
        )

        // 2. Chat Area: Either Welcome Empty State OR Scrollable Conversation
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (uiState.messages.isEmpty() && !uiState.isLoading) {
                WelcomeEmptyState(
                    errorMessage = uiState.errorMessage,
                    onRetryClick = onRetryClick,
                    onSuggestionClick = onSuggestionClick
                )
            } else {
                ConversationScrollableList(
                    uiState = uiState,
                    scrollState = scrollState,
                    onRetryClick = onRetryClick
                )
            }
        }

        // 3. Quick Suggestions Chips Row (only shown above input if messages exist)
        if (uiState.messages.isNotEmpty() && uiState.quickSuggestions.isNotEmpty()) {
            QuickSuggestionChipsBar(
                suggestions = uiState.quickSuggestions,
                onSuggestionClick = onSuggestionClick
            )
        }

        // 4. Modern Docked Input Bar
        ChatInputBar(
            inputQuery = uiState.inputQuery,
            isLoading = uiState.isLoading,
            onInputQueryChanged = onInputQueryChanged,
            onSendMessageClick = onSendMessageClick
        )

        // 5. Bottom Navigation Bar
        DashboardBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

// =============================================================================
// 1. HEADER COMPONENT
// =============================================================================
@Composable
private fun AiChatHeader(
    onBackClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF9F5FA))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MomoraPurpleDark
                )
            }

            // Center: AI Avatar & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFBF1F6), Color(0xFFF0E4F2))
                            )
                        )
                        .border(1.dp, Color(0xFFE9D8EA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    SparklesIcon(
                        color = MomoraPurplePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Momora AI",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Subtle Active Indicator Dot
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )
                    }
                    Text(
                        text = "Your pregnancy companion",
                        fontSize = 11.5.sp,
                        color = MomoraBodyMuted
                    )
                }
            }

            // Notification Bell Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF9F5FA))
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                BellIcon(
                    color = MomoraPurplePrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Hairline divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFF2EAF3))
        )
    }
}

// =============================================================================
// 2. WELCOME EMPTY STATE
// =============================================================================
@Composable
private fun WelcomeEmptyState(
    errorMessage: String?,
    onRetryClick: () -> Unit,
    onSuggestionClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Illustrated Glowing Maternal Badge
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFFFF2F6), Color(0xFFF2E4F2), Color(0xFFEBE0EC))
                    )
                )
                .border(1.5.dp, Color(0xFFECDDEB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            SparklesIcon(
                color = MomoraPurplePrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Hello, Mama ✨",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MomoraPurpleDark
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "I'm Momora, your warm and gentle pregnancy wellness companion. Ask me anything about nutrition, comfort, body changes, or your baby's milestones.",
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = MomoraBodyMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // Error Banner if initial load failed
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFEBEE))
                    .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatErrorMessage(errorMessage),
                        fontSize = 12.sp,
                        color = Color(0xFFC62828),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Retry",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.clickable { onRetryClick() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Suggested Topics Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SparklesIcon(color = MomoraPurplePrimary, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Suggested topics to explore",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4 Curated Prompt Cards in a sleek layout
        val promptItems = listOf(
            PromptItem("🥗", "Healthy meal ideas", "Nutritious snack choices for pregnancy energy"),
            PromptItem("🤰", "Pregnancy symptoms", "Gentle comfort tips for nausea & heartburn"),
            PromptItem("👶", "Baby development", "Milestones & fetal growth expectations"),
            PromptItem("🌸", "Prenatal wellness", "Safe gentle stretches & restorative sleep")
        )

        promptItems.forEach { item ->
            PromptCard(
                icon = item.icon,
                title = item.title,
                subtitle = item.subtitle,
                onClick = { onSuggestionClick(item.title) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Calming Educational Footer Notice
        Text(
            text = "Momora AI provides educational guidance · Always consult your obstetrician for clinical advice.",
            fontSize = 11.sp,
            lineHeight = 15.sp,
            color = MomoraFooterText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private data class PromptItem(
    val icon: String,
    val title: String,
    val subtitle: String
)

@Composable
private fun PromptCard(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFF1E7F3), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
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
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFAF2F7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MomoraPurpleDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        color = MomoraBodyMuted
                    )
                }
            }

            Text(
                text = "→",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MomoraPurplePrimary
            )
        }
    }
}

// =============================================================================
// 3. CONVERSATION SCROLLABLE LIST
// =============================================================================
@Composable
private fun ConversationScrollableList(
    uiState: AiChatUiState,
    scrollState: androidx.compose.foundation.ScrollState,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Formatted Today Date Banner
        val todayStr = remember {
            try {
                "TODAY · " + SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date()).uppercase()
            } catch (e: Exception) {
                "TODAY"
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFF8F3FA))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = todayStr,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraFooterText
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Message stream
        uiState.messages.forEach { msg ->
            if (msg.sender == MessageSender.USER) {
                UserChatBubble(msg = msg)
            } else {
                AiChatCard(msg = msg)
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Animated Typing Indicator
        if (uiState.isLoading) {
            TypingIndicatorBubble()
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Error banner if any
        if (uiState.errorMessage != null) {
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
                        color = MomoraPurplePrimary,
                        modifier = Modifier.clickable { onRetryClick() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// =============================================================================
// 4. USER CHAT BUBBLE
// =============================================================================
@Composable
private fun UserChatBubble(msg: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 4.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 18.dp
                    )
                )
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFF8EFF5), Color(0xFFF1E2EF))
                    )
                )
                .border(
                    1.dp,
                    Color(0xFFE9D8E7),
                    RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = msg.text,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MomoraPurpleDark
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = msg.timestampText,
            fontSize = 10.5.sp,
            color = MomoraBodyMuted,
            modifier = Modifier.padding(end = 4.dp)
        )
    }
}

// =============================================================================
// 5. AI RESPONSE CARD
// =============================================================================
@Suppress("DEPRECATION")
@Composable
private fun AiChatCard(msg: ChatMessage) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
            .background(Color.White)
            .border(
                1.dp,
                Color(0xFFEFE8F4),
                RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            // Header Row: Avatar, Brand Name, Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5ECF7)),
                        contentAlignment = Alignment.Center
                    ) {
                        SparklesIcon(color = MomoraPurplePrimary, modifier = Modifier.size(13.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Momora AI",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFF4ECF8))
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Wellness Guide",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MomoraPurplePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Text Content
            Text(
                text = msg.text,
                fontSize = 13.5.sp,
                lineHeight = 20.sp,
                color = MomoraPurpleText
            )

            // Bullet Points (if any)
            if (msg.bulletPoints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    msg.bulletPoints.forEach { point ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "•",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurplePrimary,
                                modifier = Modifier.padding(top = 1.dp, end = 6.dp)
                            )
                            Text(
                                text = point,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = MomoraPurpleText
                            )
                        }
                    }
                }
            }

            // Footer Note / Clinical Disclaimer Highlight
            if (!msg.footerNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFAF4F7))
                        .border(1.dp, Color(0xFFF2E6ED), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "ℹ️", fontSize = 12.sp, modifier = Modifier.padding(top = 1.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = msg.footerNote,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            color = MomoraSubTitleText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action & Disclaimer Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = msg.timestampText,
                    fontSize = 10.5.sp,
                    color = MomoraFooterText
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (copied) {
                        Text(
                            text = "Copied!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MomoraPurplePrimary
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    val fullCopy = buildString {
                                        append(msg.text)
                                        if (msg.bulletPoints.isNotEmpty()) {
                                            append("\n\n")
                                            msg.bulletPoints.forEach { append("• $it\n") }
                                        }
                                        if (!msg.footerNote.isNullOrBlank()) {
                                            append("\nNote: ${msg.footerNote}")
                                        }
                                    }
                                    clipboardManager.setText(AnnotatedString(fullCopy))
                                    copied = true
                                }
                                .padding(4.dp)
                        ) {
                            CopyIcon(color = MomoraFooterText, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// 6. ANIMATED TYPING INDICATOR BUBBLE
// =============================================================================
@Composable
private fun TypingIndicatorBubble() {
    val transition = rememberInfiniteTransition(label = "typing")
    val dot1Scale by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Scale by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Scale by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5ECF7)),
                contentAlignment = Alignment.Center
            ) {
                SparklesIcon(color = MomoraPurplePrimary, modifier = Modifier.size(10.dp))
            }

            Text(
                text = "Momora is thinking",
                fontSize = 12.5.sp,
                color = MomoraBodyMuted
            )

            // 3 Pulsing/Waving Dots
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp * dot1Scale + 2.dp)
                        .clip(CircleShape)
                        .background(MomoraPurplePrimary.copy(alpha = 0.4f + 0.6f * dot1Scale))
                )
                Box(
                    modifier = Modifier
                        .size(6.dp * dot2Scale + 2.dp)
                        .clip(CircleShape)
                        .background(MomoraPurplePrimary.copy(alpha = 0.4f + 0.6f * dot2Scale))
                )
                Box(
                    modifier = Modifier
                        .size(6.dp * dot3Scale + 2.dp)
                        .clip(CircleShape)
                        .background(MomoraPurplePrimary.copy(alpha = 0.4f + 0.6f * dot3Scale))
                )
            }
        }
    }
}

// =============================================================================
// 7. QUICK CHIPS ROW (ABOVE INPUT)
// =============================================================================
@Composable
private fun QuickSuggestionChipsBar(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suggestions) { suggestion ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFF9F3FA))
                    .border(1.dp, Color(0xFFEFE6F2), RoundedCornerShape(50))
                    .clickable { onSuggestionClick(suggestion) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = suggestion,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MomoraPurplePrimary
                )
            }
        }
    }
}

// =============================================================================
// 8. DOCKED MODERN INPUT BAR
// =============================================================================
@Composable
private fun ChatInputBar(
    inputQuery: String,
    isLoading: Boolean,
    onInputQueryChanged: (String) -> Unit,
    onSendMessageClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val canSend = inputQuery.isNotBlank() && !isLoading

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .border(1.dp, Color(0xFFE8DEEC), RoundedCornerShape(50))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (inputQuery.isEmpty()) {
                        Text(
                            text = "Ask what's on your mind...",
                            fontSize = 13.5.sp,
                            color = Color(0xFFA69EAC)
                        )
                    }
                    BasicTextField(
                        value = inputQuery,
                        onValueChange = onInputQueryChanged,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = if (canSend) ImeAction.Send else ImeAction.Default
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (canSend) {
                                    onSendMessageClick()
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        textStyle = TextStyle(
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Normal,
                            color = MomoraPurpleText
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button with animated active appearance
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (canSend) MomoraPurplePrimary else Color(0xFFF3ECF5))
                        .clickable(enabled = canSend) {
                            onSendMessageClick()
                            focusManager.clearFocus()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    SendIcon(
                        color = if (canSend) Color.White else Color(0xFFB5A9BA),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// =============================================================================
// 9. DASHBOARD BOTTOM NAVIGATION BAR
// =============================================================================
@Composable
private fun DashboardBottomNavBar(
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
                label = "Track",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { TrackNavIcon(color = if (selectedIndex == 1) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "AI",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { SparklesIcon(color = if (selectedIndex == 2) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Care",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { CareNavIcon(color = if (selectedIndex == 3) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Profile",
                isSelected = selectedIndex == 4,
                onClick = { onTabSelected(4) },
                icon = { UserNavIcon(color = if (selectedIndex == 4) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
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

// =============================================================================
// 10. CUSTOM ICONS
// =============================================================================
@Composable
private fun SendIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val sendPath = Path().apply {
            moveTo(w * 0.15f, h * 0.20f)
            lineTo(w * 0.85f, h * 0.50f)
            lineTo(w * 0.15f, h * 0.80f)
            lineTo(w * 0.35f, h * 0.50f)
            close()
        }
        drawPath(sendPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun CopyIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.5.dp.toPx()

        drawRect(
            color = color,
            topLeft = Offset(w * 0.30f, h * 0.30f),
            size = Size(w * 0.55f, h * 0.55f),
            style = Stroke(width = strokeWidth)
        )
        drawRect(
            color = color,
            topLeft = Offset(w * 0.15f, h * 0.15f),
            size = Size(w * 0.55f, h * 0.55f),
            style = Stroke(width = strokeWidth)
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
private fun TrackNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val pulsePath = Path().apply {
            moveTo(w * 0.10f, h * 0.50f)
            lineTo(w * 0.30f, h * 0.50f)
            lineTo(w * 0.40f, h * 0.25f)
            lineTo(w * 0.55f, h * 0.75f)
            lineTo(w * 0.70f, h * 0.40f)
            lineTo(w * 0.80f, h * 0.50f)
            lineTo(w * 0.90f, h * 0.50f)
        }
        drawPath(pulsePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun CareNavIcon(color: Color, modifier: Modifier = Modifier) {
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
    }
}

@Composable
private fun UserNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(color = color, radius = w * 0.20f, center = Offset(w * 0.50f, h * 0.35f), style = Stroke(width = strokeWidth))
        val bodyArc = Path().apply {
            moveTo(w * 0.20f, h * 0.80f)
            cubicTo(w * 0.30f, h * 0.60f, w * 0.70f, h * 0.60f, w * 0.80f, h * 0.80f)
        }
        drawPath(bodyArc, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
private fun SparklesIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val starPath = Path().apply {
            moveTo(w * 0.50f, h * 0.15f)
            cubicTo(w * 0.50f, h * 0.35f, w * 0.65f, h * 0.50f, w * 0.85f, h * 0.50f)
            cubicTo(w * 0.65f, h * 0.50f, w * 0.50f, h * 0.65f, w * 0.50f, h * 0.85f)
            cubicTo(w * 0.50f, h * 0.65f, w * 0.35f, h * 0.50f, w * 0.15f, h * 0.50f)
            cubicTo(w * 0.35f, h * 0.50f, w * 0.50f, h * 0.35f, w * 0.50f, h * 0.15f)
            close()
        }
        drawPath(starPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun AiChatScreenPreview() {
    AiChatScreenContent()
}
