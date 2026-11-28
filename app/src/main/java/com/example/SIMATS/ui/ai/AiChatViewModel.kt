package com.example.SIMATS.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.ChatMessage
import com.example.SIMATS.data.model.MessageSender
import com.example.SIMATS.data.repository.AiChatRepository
import com.example.SIMATS.data.repository.AiChatRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AiChatViewModel(
    private val repository: AiChatRepository = AiChatRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    init {
        loadInitialMessages()
    }

    private fun generateConversationId(): String {
        val ts = System.currentTimeMillis()
        val randomSuffix = UUID.randomUUID().toString().replace("-", "").take(6)
        return "conv_${ts}_$randomSuffix"
    }

    fun onEvent(event: AiChatUiEvent) {
        when (event) {
            is AiChatUiEvent.OnInputQueryChanged -> {
                _uiState.value = _uiState.value.copy(inputQuery = event.query)
            }
            AiChatUiEvent.OnSendMessageClicked -> {
                sendUserMessage(_uiState.value.inputQuery)
            }
            is AiChatUiEvent.OnSuggestionClicked -> {
                _uiState.value = _uiState.value.copy(inputQuery = event.suggestion)
                sendUserMessage(event.suggestion)
            }
            is AiChatUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            AiChatUiEvent.OnRetryClicked -> {
                loadInitialMessages()
            }
            AiChatUiEvent.OnBackClicked -> {}
            AiChatUiEvent.OnNotificationClicked -> {}

            // ChatGPT-Style features
            AiChatUiEvent.OnNewChatClicked -> {
                val newConvId = generateConversationId()
                _uiState.value = _uiState.value.copy(
                    currentConversationId = newConvId,
                    selectedSessionId = null,
                    messages = emptyList(),
                    isHistoryDrawerOpen = false,
                    errorMessage = null
                )
            }
            AiChatUiEvent.OnOpenHistoryClicked -> {
                _uiState.value = _uiState.value.copy(isHistoryDrawerOpen = true)
            }
            AiChatUiEvent.OnDismissHistoryClicked -> {
                _uiState.value = _uiState.value.copy(isHistoryDrawerOpen = false)
            }
            is AiChatUiEvent.OnSelectSession -> {
                val selected = _uiState.value.sessions.find { it.id == event.sessionId }
                if (selected != null) {
                    _uiState.value = _uiState.value.copy(
                        currentConversationId = selected.id,
                        selectedSessionId = selected.id,
                        messages = selected.messages,
                        isHistoryDrawerOpen = false,
                        errorMessage = null
                    )
                }
            }
            AiChatUiEvent.OnClearChatClicked -> {
                _uiState.value = _uiState.value.copy(showClearConfirmDialog = true)
            }
            AiChatUiEvent.OnConfirmClearChat -> {
                val newConvId = generateConversationId()
                _uiState.value = _uiState.value.copy(
                    currentConversationId = newConvId,
                    messages = emptyList(),
                    selectedSessionId = null,
                    showClearConfirmDialog = false
                )
            }
            AiChatUiEvent.OnDismissClearDialog -> {
                _uiState.value = _uiState.value.copy(showClearConfirmDialog = false)
            }
            is AiChatUiEvent.OnImageSelected -> {
                _uiState.value = _uiState.value.copy(
                    selectedImageUri = event.uri,
                    selectedImageBytes = event.bytes,
                    selectedImageMimeType = event.mimeType,
                    selectedImageName = event.fileName,
                    errorMessage = null
                )
            }
            AiChatUiEvent.OnRemoveImageClicked -> {
                _uiState.value = _uiState.value.copy(
                    selectedImageUri = null,
                    selectedImageBytes = null,
                    selectedImageMimeType = null,
                    selectedImageName = null
                )
            }
        }
    }

    private fun loadInitialMessages() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getInitialChatMessages().collect { result ->
                result.onSuccess { rawList ->
                    val cleanList = rawList.map { sanitizeChatMessage(it) }
                    val sessions = groupMessagesIntoSessions(cleanList)
                    val initialConvId = generateConversationId()

                    _uiState.value = _uiState.value.copy(
                        allHistoryMessages = cleanList,
                        sessions = sessions,
                        currentConversationId = initialConvId,
                        selectedSessionId = null,
                        messages = emptyList(), // Fresh chat on opening
                        isLoading = false,
                        errorMessage = null
                    )
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = err.message ?: "Failed to load chat history"
                    )
                }
            }
        }
    }

    private fun sendUserMessage(queryText: String) {
        val trimmed = queryText.trim()
        val attachedUri = _uiState.value.selectedImageUri
        val attachedBytes = _uiState.value.selectedImageBytes
        val attachedMime = _uiState.value.selectedImageMimeType
        val attachedName = _uiState.value.selectedImageName

        if ((trimmed.isBlank() && attachedBytes == null) || _uiState.value.isLoading) return

        val displayText = if (trimmed.isNotBlank()) trimmed else "Please analyze this image"
        val activeConvId = _uiState.value.currentConversationId ?: generateConversationId()
        val tempId = "temp_${System.currentTimeMillis()}"
        val userMsg = ChatMessage(
            id = tempId,
            conversationId = activeConvId,
            sender = MessageSender.USER,
            text = displayText,
            imageUrl = attachedUri?.toString(),
            timestampText = "You · Just now",
            created_at = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        // 1. Immediately display the user's message and loading indicator
        val updatedCurrentMessages = _uiState.value.messages + userMsg
        val updatedAllMessages = _uiState.value.allHistoryMessages + userMsg
        val updatedSessions = groupMessagesIntoSessions(updatedAllMessages)

        _uiState.value = _uiState.value.copy(
            currentConversationId = activeConvId,
            selectedSessionId = activeConvId,
            messages = updatedCurrentMessages,
            allHistoryMessages = updatedAllMessages,
            sessions = updatedSessions,
            inputQuery = "",
            selectedImageUri = null,
            selectedImageBytes = null,
            selectedImageMimeType = null,
            selectedImageName = null,
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            repository.sendMessage(displayText, activeConvId, attachedBytes, attachedMime, attachedName).collect { result ->
                result.onSuccess { sendData ->
                    // 2. Map response IDs & fields correctly and clean markdown
                    val rawAiMessage = sendData.toChatMessage()
                    val resolvedConvId = sendData.conversation_id ?: activeConvId
                    val aiMessage = sanitizeChatMessage(rawAiMessage).copy(conversationId = resolvedConvId)
                    val officialQuestionId = sendData.question_id?.toString()
                    val serverImageUrl = sendData.imageUrl

                    val currentMessages = _uiState.value.messages
                    val reconciledCurrent = currentMessages.map { msg ->
                        if (msg.id == tempId) {
                            msg.copy(
                                id = officialQuestionId ?: msg.id,
                                conversationId = resolvedConvId,
                                imageUrl = serverImageUrl ?: msg.imageUrl
                            )
                        } else {
                            msg
                        }
                    } + aiMessage

                    val allMessages = _uiState.value.allHistoryMessages
                    val reconciledAll = allMessages.map { msg ->
                        if (msg.id == tempId) {
                            msg.copy(
                                id = officialQuestionId ?: msg.id,
                                conversationId = resolvedConvId,
                                imageUrl = serverImageUrl ?: msg.imageUrl
                            )
                        } else {
                            msg
                        }
                    } + aiMessage

                    val newSessions = groupMessagesIntoSessions(reconciledAll)

                    // 3. Immediately append the AI response to the displayed list and stop loading
                    _uiState.value = _uiState.value.copy(
                        currentConversationId = resolvedConvId,
                        selectedSessionId = resolvedConvId,
                        messages = reconciledCurrent,
                        allHistoryMessages = reconciledAll,
                        sessions = newSessions,
                        isLoading = false,
                        errorMessage = null
                    )
                }.onFailure { err ->
                    // 4. On failure, stop loading and display error without clearing messages
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = err.message ?: "Failed to send message"
                    )
                }
            }
        }
    }

    private fun sanitizeChatMessage(msg: ChatMessage): ChatMessage {
        val cleanText = stripMarkdownSymbols(msg.text)
        val cleanBullets = msg.bulletPoints.map { stripMarkdownSymbols(it) }
        val cleanFooter = msg.footerNote?.let { stripMarkdownSymbols(it) }
        return msg.copy(
            text = cleanText,
            bulletPoints = cleanBullets,
            footerNote = cleanFooter
        )
    }

    private fun stripMarkdownSymbols(raw: String): String {
        var s = raw.trim()
        s = s.replace(Regex("^#{1,6}\\s*", RegexOption.MULTILINE), "")
        s = s.replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
        s = s.replace(Regex("\\*([^*]+)\\*"), "$1")
        s = s.replace(Regex("__([^_]+)__"), "$1")
        s = s.replace(Regex("_([^_]+)_"), "$1")
        s = s.replace(Regex("`([^`]+)`"), "$1")
        // Normalize bullet markers (- or * to •) while preserving bullets
        s = s.replace(Regex("^[\\s]*[-*]\\s+", RegexOption.MULTILINE), "• ")
        return s.trim()
    }

    private fun groupMessagesIntoSessions(messages: List<ChatMessage>): List<ChatSession> {
        if (messages.isEmpty()) return emptyList()

        // Group messages strictly by conversationId.
        // For historical legacy messages without conversationId, group by date.
        val sessionMap = LinkedHashMap<String, MutableList<ChatMessage>>()

        for (msg in messages) {
            val convId = if (!msg.conversationId.isNullOrBlank()) {
                msg.conversationId
            } else {
                val dayKey = msg.created_at?.take(10)?.replace("-", "") ?: "legacy"
                "conv_legacy_$dayKey"
            }
            sessionMap.getOrPut(convId) { mutableListOf() }.add(msg)
        }

        val sessions = mutableListOf<ChatSession>()
        for ((convId, msgs) in sessionMap) {
            sessions.add(buildSession(convId, msgs))
        }

        return sessions
    }

    private fun buildSession(id: String, msgs: List<ChatMessage>): ChatSession {
        val firstUserMsg = msgs.firstOrNull { it.sender == MessageSender.USER }
        val rawTitle = firstUserMsg?.text ?: msgs.firstOrNull()?.text ?: "Pregnancy Wellness Chat"
        val title = if (rawTitle.length > 38) rawTitle.take(38) + "..." else rawTitle

        val lastMsg = msgs.lastOrNull()
        val rawPreview = lastMsg?.text ?: ""
        val preview = if (rawPreview.length > 65) rawPreview.take(65) + "..." else rawPreview

        val dateLabel = computeDateLabel(msgs.lastOrNull()?.created_at ?: msgs.firstOrNull()?.created_at)

        return ChatSession(
            id = id,
            title = title,
            dateLabel = dateLabel,
            previewText = preview,
            messageCount = msgs.size,
            messages = msgs
        )
    }

    private fun computeDateLabel(createdAt: String?): String {
        if (createdAt.isNullOrBlank()) return "Recent"
        return try {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val msgDate = df.parse(createdAt.substring(0, minOf(10, createdAt.length)))
            val today = df.parse(df.format(Date()))

            if (msgDate != null && today != null) {
                val diffDays = (today.time - msgDate.time) / (1000 * 60 * 60 * 24)
                when (diffDays) {
                    0L -> "Today"
                    1L -> "Yesterday"
                    in 2..6 -> "${diffDays} days ago"
                    else -> SimpleDateFormat("d MMM", Locale.getDefault()).format(msgDate)
                }
            } else "Recent"
        } catch (e: Exception) {
            "Recent"
        }
    }

    class Factory(private val repository: AiChatRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AiChatViewModel(repository) as T
        }
    }
}
