package com.example.SIMATS.ui.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.ChatMessage
import com.example.SIMATS.data.model.MessageSender
import com.example.SIMATS.data.remote.RetrofitClient
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraFooterText
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraSubTitleText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val cr = context.contentResolver
            val mime = cr.getType(uri) ?: "image/jpeg"
            val bytes = try {
                cr.openInputStream(uri)?.use { it.readBytes() }
            } catch (e: Exception) {
                null
            }
            var fileName = "photo_${System.currentTimeMillis()}.jpg"
            try {
                cr.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIdx >= 0) {
                        fileName = cursor.getString(nameIdx)
                    }
                }
            } catch (_: Exception) {}

            viewModel.onEvent(
                AiChatUiEvent.OnImageSelected(
                    uri = uri,
                    bytes = bytes,
                    mimeType = mime,
                    fileName = fileName
                )
            )
        }
    }

    AiChatScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onRetryClick = { viewModel.onEvent(AiChatUiEvent.OnRetryClicked) },
        onInputQueryChanged = { q -> viewModel.onEvent(AiChatUiEvent.OnInputQueryChanged(q)) },
        onAttachImageClick = { galleryLauncher.launch("image/*") },
        onRemoveImageClick = { viewModel.onEvent(AiChatUiEvent.OnRemoveImageClicked) },
        onSendMessageClick = { viewModel.onEvent(AiChatUiEvent.OnSendMessageClicked) },
        onSuggestionClick = { s -> viewModel.onEvent(AiChatUiEvent.OnSuggestionClicked(s)) },
        onBackClick = {
            viewModel.onEvent(AiChatUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = { viewModel.onEvent(AiChatUiEvent.OnNotificationClicked) },
        onNewChatClick = { viewModel.onEvent(AiChatUiEvent.OnNewChatClicked) },
        onOpenHistoryClick = { viewModel.onEvent(AiChatUiEvent.OnOpenHistoryClicked) },
        onDismissHistory = { viewModel.onEvent(AiChatUiEvent.OnDismissHistoryClicked) },
        onSelectSession = { id -> viewModel.onEvent(AiChatUiEvent.OnSelectSession(id)) },
        onClearChatClick = { viewModel.onEvent(AiChatUiEvent.OnClearChatClicked) },
        onConfirmClear = { viewModel.onEvent(AiChatUiEvent.OnConfirmClearChat) },
        onDismissClearDialog = { viewModel.onEvent(AiChatUiEvent.OnDismissClearDialog) },
        onNavTabSelected = { index ->
            viewModel.onEvent(AiChatUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiChatScreenContent(
    modifier: Modifier = Modifier,
    uiState: AiChatUiState = AiChatUiState(),
    onRetryClick: () -> Unit = {},
    onInputQueryChanged: (String) -> Unit = {},
    onAttachImageClick: () -> Unit = {},
    onRemoveImageClick: () -> Unit = {},
    onSendMessageClick: () -> Unit = {},
    onSuggestionClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNewChatClick: () -> Unit = {},
    onOpenHistoryClick: () -> Unit = {},
    onDismissHistory: () -> Unit = {},
    onSelectSession: (String) -> Unit = {},
    onClearChatClick: () -> Unit = {},
    onConfirmClear: () -> Unit = {},
    onDismissClearDialog: () -> Unit = {},
    selectedNavIndex: Int = 2,
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val isKeyboardOpen = WindowInsets.isImeVisible

    // Smooth auto-scroll when message count, loading status, or keyboard opens
    LaunchedEffect(isKeyboardOpen, uiState.messages.size, uiState.isLoading) {
        if (uiState.messages.isNotEmpty()) {
            delay(100)
            scrollState.animateScrollTo(scrollState.maxValue)
            delay(150)
            if (scrollState.value < scrollState.maxValue) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }

    // Clear Chat Confirmation Dialog
    if (uiState.showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = onDismissClearDialog,
            title = {
                Text(
                    text = "Clear Current Chat?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MomoraPurpleDark
                )
            },
            text = {
                Text(
                    text = "This clears the active chat screen so you can start a fresh topic. All past conversations remain safely saved in Chat History.",
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    color = MomoraPurpleText
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE57373))
                        .clickable { onConfirmClear() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Clear Chat",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF3EDF5))
                        .clickable { onDismissClearDialog() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = MomoraPurpleDark,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Chat History Modal Bottom Sheet
    if (uiState.isHistoryDrawerOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        ModalBottomSheet(
            onDismissRequest = onDismissHistory,
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            ChatHistorySheetContent(
                sessions = uiState.sessions,
                selectedSessionId = uiState.selectedSessionId,
                onNewChatClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onNewChatClick()
                    }
                },
                onSelectSession = { sessionId ->
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onSelectSession(sessionId)
                    }
                },
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onDismissHistory()
                    }
                }
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
            .statusBarsPadding()
            .then(
                if (isKeyboardOpen) {
                    Modifier.imePadding()
                } else {
                    Modifier.navigationBarsPadding()
                }
            ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. ChatGPT-Style Premium Header
        AiChatHeader(
            onBackClick = onBackClick,
            onNewChatClick = onNewChatClick,
            onOpenHistoryClick = onOpenHistoryClick,
            onClearChatClick = onClearChatClick,
            hasMessages = uiState.messages.isNotEmpty()
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

        // 3. Quick Suggestions Chips Row (only visible when keyboard is closed)
        if (!isKeyboardOpen && uiState.messages.isNotEmpty() && uiState.quickSuggestions.isNotEmpty()) {
            QuickSuggestionChipsBar(
                suggestions = uiState.quickSuggestions,
                onSuggestionClick = onSuggestionClick
            )
        }

        // 4. Modern Docked Input Bar (always visible immediately above keyboard or bottom nav)
        ChatInputBar(
            inputQuery = uiState.inputQuery,
            selectedImageUri = uiState.selectedImageUri,
            selectedImageName = uiState.selectedImageName,
            isLoading = uiState.isLoading,
            onInputQueryChanged = onInputQueryChanged,
            onAttachImageClick = onAttachImageClick,
            onRemoveImageClick = onRemoveImageClick,
            onSendMessageClick = onSendMessageClick
        )

        // 5. Bottom Navigation Bar (only visible when keyboard is closed)
        if (!isKeyboardOpen) {
            DashboardBottomNavBar(
                selectedIndex = selectedNavIndex,
                onTabSelected = onNavTabSelected
            )
        }
    }
}

// =============================================================================
// 1. CHATGPT-STYLE PREMIUM HEADER
// =============================================================================
@Composable
private fun AiChatHeader(
    onBackClick: () -> Unit,
    onNewChatClick: () -> Unit,
    onOpenHistoryClick: () -> Unit,
    onClearChatClick: () -> Unit,
    hasMessages: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Back Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF9F5FA))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MomoraPurpleDark
                )
            }

            // Center: AI Avatar & Titles
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
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
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Momora AI",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurpleDark
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )
                    }
                    Text(
                        text = "Your pregnancy companion",
                        fontSize = 11.sp,
                        color = MomoraBodyMuted
                    )
                }
            }

            // Right Action Controls: History, New Chat, Clear
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Chat History Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF9F5FA))
                        .clickable { onOpenHistoryClick() },
                    contentAlignment = Alignment.Center
                ) {
                    HistoryIcon(
                        color = MomoraPurplePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // New Chat Button (+)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFAF0F6))
                        .border(1.dp, Color(0xFFF0DEEB), CircleShape)
                        .clickable { onNewChatClick() },
                    contentAlignment = Alignment.Center
                ) {
                    PlusIcon(
                        color = MomoraPurplePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Clear Chat Button (only active if messages exist)
                if (hasMessages) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF4F4))
                            .clickable { onClearChatClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        ClearChatIcon(
                            color = Color(0xFFE57373),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
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
// 2. CHAT HISTORY SHEET CONTENT
// =============================================================================
@Composable
private fun ChatHistorySheetContent(
    sessions: List<ChatSession>,
    selectedSessionId: String?,
    onNewChatClick: () -> Unit,
    onSelectSession: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Sheet Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HistoryIcon(color = MomoraPurplePrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Chat History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5EEF6))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✕",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // + Start New Conversation Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFBF1F6), Color(0xFFF3E5F4))
                    )
                )
                .border(1.dp, Color(0xFFE7D6E9), RoundedCornerShape(14.dp))
                .clickable { onNewChatClick() }
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                PlusIcon(color = MomoraPurplePrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start a New Chat",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurplePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Previous Conversations",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MomoraBodyMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No previous conversations found.\nStart your first chat with Momora AI!",
                    fontSize = 13.sp,
                    color = MomoraBodyMuted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sessions.reversed()) { session ->
                    val isSelected = session.id == selectedSessionId
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFFFAF3FB) else Color.White)
                            .border(
                                1.dp,
                                if (isSelected) MomoraPurplePrimary else Color(0xFFEFE8F3),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectSession(session.id) }
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = session.title,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MomoraPurpleDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color(0xFFF2EAF3))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = session.dateLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MomoraPurplePrimary
                                    )
                                }
                            }

                            if (session.previewText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = session.previewText,
                                    fontSize = 12.sp,
                                    color = MomoraBodyMuted,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${session.messageCount} messages",
                                    fontSize = 10.5.sp,
                                    color = MomoraFooterText
                                )
                                if (isSelected) {
                                    Text(
                                        text = "Active",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MomoraPurplePrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// 3. WELCOME EMPTY STATE
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
            text = "I'm Momora, your warm and gentle pregnancy wellness companion. Ask me anything about Indian prenatal meals, comforting remedies, or your baby's weekly growth.",
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
            PromptItem("🥗", "Healthy Indian meals", "Idli with sambar, dosa, dal & paneer ideas"),
            PromptItem("🤰", "Morning sickness relief", "Gentle tips for nausea, acidity & hydration"),
            PromptItem("🌾", "Ragi & iron nutrition", "Wholesome grains, lentils & spinach benefits"),
            PromptItem("🌸", "Safe pregnancy stretches", "Pelvic comfort & gentle maternal movement")
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
// 4. CONVERSATION SCROLLABLE LIST
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
        val todayStr = remember {
            try {
                "CONVERSATION · " + SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date()).uppercase()
            } catch (e: Exception) {
                "CONVERSATION"
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
// 5. USER CHAT BUBBLE
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
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                if (!msg.imageUrl.isNullOrBlank()) {
                    NetworkOrUriImage(
                        urlOrUri = msg.imageUrl,
                        contentDescription = "Attached Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    if (msg.text.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                if (msg.text.isNotBlank()) {
                    Text(
                        text = msg.text,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MomoraPurpleDark
                    )
                }
            }
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
// 6. AI RESPONSE CARD & STRUCTURED LAYOUT
// =============================================================================
private data class FormattedAnswerBlock(
    val title: String? = null,
    val icon: String? = null,
    val paragraphs: List<String> = emptyList(),
    val bullets: List<String> = emptyList(),
    val isDoctorWarning: Boolean = false,
    val isIndianFood: Boolean = false
)

private fun parseAiResponseIntoBlocks(rawText: String, legacyBullets: List<String>): List<FormattedAnswerBlock> {
    val clean = rawText.trim()
    if (clean.isBlank() && legacyBullets.isEmpty()) return emptyList()

    val lines = clean.lines()
    val blocks = mutableListOf<FormattedAnswerBlock>()

    var currentTitle: String? = null
    var currentIcon: String? = null
    var currentIsWarning = false
    var currentIsFood = false
    var currentParagraphs = mutableListOf<String>()
    var currentBullets = mutableListOf<String>()

    fun flushCurrent() {
        if (currentTitle != null || currentParagraphs.isNotEmpty() || currentBullets.isNotEmpty()) {
            blocks.add(
                FormattedAnswerBlock(
                    title = currentTitle,
                    icon = currentIcon,
                    paragraphs = currentParagraphs.toList(),
                    bullets = currentBullets.toList(),
                    isDoctorWarning = currentIsWarning,
                    isIndianFood = currentIsFood
                )
            )
            currentTitle = null
            currentIcon = null
            currentIsWarning = false
            currentIsFood = false
            currentParagraphs = mutableListOf()
            currentBullets = mutableListOf()
        }
    }

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) {
            continue
        }

        val lower = trimmed.lowercase().trimEnd(':')

        // Detect section title
        val isSectionHeader = when {
            lower.startsWith("what you should know") -> true
            lower.startsWith("helpful suggestions") || lower.startsWith("practical suggestions") -> true
            lower.contains("indian food") || lower.startsWith("food suggestions") || lower.startsWith("diet suggestions") -> true
            lower.contains("contact your doctor") || lower.contains("call your doctor") || lower.contains("seek medical") || lower.contains("medical warning") || lower.contains("urgent medical notice") -> true
            trimmed.endsWith(":") && trimmed.length in 4..45 && !trimmed.startsWith("•") && !trimmed.startsWith("-") -> true
            else -> false
        }

        if (isSectionHeader) {
            flushCurrent()
            val headerTitle = trimmed.trimEnd(':').trim()
            currentTitle = headerTitle

            when {
                lower.contains("doctor") || lower.contains("medical") || lower.contains("warning") || lower.contains("urgent") -> {
                    currentIsWarning = true
                    currentIcon = "🩺"
                }
                lower.contains("food") || lower.contains("indian") || lower.contains("diet") || lower.contains("nutrition") -> {
                    currentIsFood = true
                    currentIcon = "🥗"
                }
                lower.contains("know") -> {
                    currentIcon = "📋"
                }
                lower.contains("suggestion") || lower.contains("tip") -> {
                    currentIcon = "💡"
                }
                else -> {
                    currentIcon = "✨"
                }
            }
        } else if (trimmed.startsWith("•") || trimmed.startsWith("-") || trimmed.startsWith("*")) {
            val bulletContent = trimmed.replace(Regex("^[•\\-*\\s]+"), "").trim()
            if (bulletContent.isNotBlank()) {
                currentBullets.add(bulletContent)
            }
        } else {
            currentParagraphs.add(trimmed)
        }
    }

    flushCurrent()

    // Only append legacyBullets if no blocks already contain bullets and legacy bullets are not in rawText
    if (legacyBullets.isNotEmpty() && blocks.none { it.bullets.isNotEmpty() }) {
        val nonDuplicateBullets = legacyBullets.filter { b -> !clean.contains(b, ignoreCase = true) }
        if (nonDuplicateBullets.isNotEmpty()) {
            blocks.add(
                FormattedAnswerBlock(
                    title = "Helpful suggestions",
                    icon = "💡",
                    bullets = nonDuplicateBullets,
                    isDoctorWarning = false,
                    isIndianFood = false
                )
            )
        }
    }

    return blocks
}

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

            // Main Structured Content
            StructuredAiAnswerContent(msg = msg)

            // Image Preview (if image URL is present)
            if (!msg.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5EEF6))
                        .border(1.dp, Color(0xFFE9DFEC), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📷 Image Attachment Preview",
                        fontSize = 12.sp,
                        color = MomoraPurplePrimary
                    )
                }
            }

            // Footer Note / Clinical Disclaimer (if provided)
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

            // Action & Timestamp Bar
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

@Composable
private fun StructuredAiAnswerContent(msg: ChatMessage) {
    val blocks = remember(msg.text, msg.bulletPoints) {
        parseAiResponseIntoBlocks(msg.text, msg.bulletPoints)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        blocks.forEach { block ->
            val hasContent = block.paragraphs.isNotEmpty() || block.bullets.isNotEmpty()
            if (hasContent) {
                when {
                    block.isDoctorWarning -> {
                        DoctorWarningBlock(block = block)
                    }
                    block.isIndianFood -> {
                        IndianFoodBlock(block = block)
                    }
                    block.title != null -> {
                        StandardSectionBlock(block = block)
                    }
                    else -> {
                        // Intro or standard paragraphs
                        block.paragraphs.forEach { paragraph ->
                            Text(
                                text = paragraph,
                                fontSize = 13.5.sp,
                                lineHeight = 21.sp,
                                color = MomoraPurpleText
                            )
                        }
                        if (block.bullets.isNotEmpty()) {
                            BulletPointsList(bullets = block.bullets)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorWarningBlock(block: FormattedAnswerBlock) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF5F5))
            .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🩺", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = block.title ?: "When to contact your doctor",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            block.paragraphs.forEach { p ->
                Text(
                    text = p,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFFB71C1C)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            block.bullets.forEach { b ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(top = 1.dp, end = 6.dp)
                    )
                    Text(
                        text = b,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFFB71C1C)
                    )
                }
            }
        }
    }
}

@Composable
private fun IndianFoodBlock(block: FormattedAnswerBlock) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFDF7FA))
            .border(1.dp, Color(0xFFF2E2ED), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🥗", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = block.title ?: "Indian food suggestions",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF882463)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            block.paragraphs.forEach { p ->
                Text(
                    text = p,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MomoraPurpleText
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            block.bullets.forEach { b ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurplePrimary,
                        modifier = Modifier.padding(top = 1.dp, end = 6.dp)
                    )
                    Text(
                        text = b,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MomoraPurpleText
                    )
                }
            }
        }
    }
}

@Composable
private fun StandardSectionBlock(block: FormattedAnswerBlock) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!block.icon.isNullOrBlank()) {
                Text(text = block.icon, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = block.title ?: "",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MomoraPurpleDark
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        block.paragraphs.forEach { p ->
            Text(
                text = p,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MomoraPurpleText
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        block.bullets.forEach { b ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "•",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurplePrimary,
                    modifier = Modifier.padding(top = 1.dp, end = 6.dp)
                )
                Text(
                    text = b,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MomoraPurpleText
                )
            }
        }
    }
}

@Composable
private fun BulletPointsList(bullets: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        bullets.forEach { b ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "•",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurplePrimary,
                    modifier = Modifier.padding(top = 1.dp, end = 6.dp)
                )
                Text(
                    text = b,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MomoraPurpleText
                )
            }
        }
    }
}

// =============================================================================
// 7. ANIMATED TYPING INDICATOR BUBBLE
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
// 8. QUICK CHIPS ROW (ABOVE INPUT)
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
// 9. DOCKED MODERN INPUT BAR WITH IMAGE ATTACHMENT
// =============================================================================
@Composable
private fun ChatInputBar(
    inputQuery: String,
    selectedImageUri: Uri?,
    selectedImageName: String?,
    isLoading: Boolean,
    onInputQueryChanged: (String) -> Unit,
    onAttachImageClick: () -> Unit,
    onRemoveImageClick: () -> Unit,
    onSendMessageClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val canSend = (inputQuery.isNotBlank() || selectedImageUri != null) && !isLoading

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Image preview banner if image attached
        if (selectedImageUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFAF2F7))
                    .border(1.dp, Color(0xFFE9D8E7), RoundedCornerShape(14.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NetworkOrUriImage(
                        urlOrUri = selectedImageUri.toString(),
                        contentDescription = "Selected Image Preview",
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedImageName ?: "Photo attached",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MomoraPurpleDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Ready to send with Momora AI",
                            fontSize = 11.sp,
                            color = MomoraBodyMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEADBEC))
                            .clickable { onRemoveImageClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraPurplePrimary
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .border(1.dp, Color(0xFFE8DEEC), RoundedCornerShape(50))
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image Attachment Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (selectedImageUri != null) Color(0xFFEADBEC) else Color(0xFFF7F1F8))
                        .clickable(enabled = !isLoading) { onAttachImageClick() },
                    contentAlignment = Alignment.Center
                ) {
                    AttachImageIcon(
                        color = if (selectedImageUri != null) MomoraPurplePrimary else Color(0xFF8F7E94),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (inputQuery.isEmpty()) {
                        Text(
                            text = if (selectedImageUri != null) "Ask a question about this image..." else "Ask Momora anything...",
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
// IMAGE UNDERSTANDING UTILITIES: ASYNC IMAGE LOADER & ATTACHMENT ICON
// =============================================================================
@Composable
private fun NetworkOrUriImage(
    urlOrUri: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    if (urlOrUri.isNullOrBlank()) return
    val context = LocalContext.current
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = urlOrUri) {
        value = withContext(Dispatchers.IO) {
            try {
                if (urlOrUri.startsWith("content://") || urlOrUri.startsWith("file://")) {
                    val uri = Uri.parse(urlOrUri)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                            decoder.isMutableRequired = true
                        }
                    } else {
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            BitmapFactory.decodeStream(stream)
                        }
                    }
                } else {
                    val base = RetrofitClient.BASE_URL.trimEnd('/')
                    val cleanPath = urlOrUri.trimStart('/')
                    val fullUrl = if (urlOrUri.startsWith("http://") || urlOrUri.startsWith("https://")) {
                        urlOrUri
                    } else if (base.endsWith("momora-AI") && cleanPath.startsWith("momora-AI/")) {
                        base.substringBeforeLast("momora-AI") + cleanPath
                    } else {
                        "$base/$cleanPath"
                    }
                    val url = URL(fullUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 8000
                    connection.readTimeout = 8000
                    connection.setRequestProperty("ngrok-skip-browser-warning", "true")
                    val token = SessionManager.getToken()
                    if (!token.isNullOrBlank()) {
                        connection.setRequestProperty("Authorization", "Bearer $token")
                    }
                    connection.connect()
                    if (connection.responseCode in 200..299) {
                        connection.inputStream.use { stream ->
                            BitmapFactory.decodeStream(stream)
                        }
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    val bitmap = bitmapState.value
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .background(Color(0xFFF7F1F8), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading image...",
                fontSize = 11.sp,
                color = Color(0xFF9E8EA3)
            )
        }
    }
}

@Composable
private fun AttachImageIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.6.dp.toPx()

        // Outer rounded picture frame
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.14f, h * 0.18f),
            size = Size(w * 0.72f, h * 0.64f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.14f, h * 0.14f),
            style = Stroke(width = stroke)
        )
        // Little sun/circle in top right
        drawCircle(
            color = color,
            radius = w * 0.08f,
            center = Offset(w * 0.64f, h * 0.38f)
        )
        // Mountains/slopes
        val mountPath = Path().apply {
            moveTo(w * 0.22f, h * 0.68f)
            lineTo(w * 0.42f, h * 0.48f)
            lineTo(w * 0.54f, h * 0.60f)
            lineTo(w * 0.66f, h * 0.46f)
            lineTo(w * 0.78f, h * 0.58f)
        }
        drawPath(
            path = mountPath,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

// =============================================================================
// 10. DASHBOARD BOTTOM NAVIGATION BAR
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
// 11. CUSTOM ICONS (ZERO EXTRA ASSET DEPENDENCY)
// =============================================================================
@Composable
private fun PlusIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        drawLine(
            color = color,
            start = Offset(w * 0.5f, h * 0.22f),
            end = Offset(w * 0.5f, h * 0.78f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.22f, h * 0.5f),
            end = Offset(w * 0.78f, h * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun HistoryIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()
        drawCircle(
            color = color,
            radius = w * 0.36f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = color,
            start = Offset(w * 0.5f, h * 0.5f),
            end = Offset(w * 0.5f, h * 0.28f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.5f, h * 0.5f),
            end = Offset(w * 0.68f, h * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ClearChatIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()
        val binPath = Path().apply {
            moveTo(w * 0.28f, h * 0.36f)
            lineTo(w * 0.33f, h * 0.80f)
            lineTo(w * 0.67f, h * 0.80f)
            lineTo(w * 0.72f, h * 0.36f)
        }
        drawPath(binPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(color = color, start = Offset(w * 0.20f, h * 0.36f), end = Offset(w * 0.80f, h * 0.36f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.40f, h * 0.26f), end = Offset(w * 0.60f, h * 0.26f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    }
}

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