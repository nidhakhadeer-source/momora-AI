package com.example.SIMATS.ui.ai

import com.example.SIMATS.data.model.ChatMessage

data class ChatSession(
    val id: String,
    val title: String,
    val dateLabel: String,
    val previewText: String,
    val messageCount: Int,
    val messages: List<ChatMessage>
)

data class AiChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val allHistoryMessages: List<ChatMessage> = emptyList(),
    val sessions: List<ChatSession> = emptyList(),
    val selectedSessionId: String? = null,
    val currentConversationId: String? = null,
    val isHistoryDrawerOpen: Boolean = false,
    val showClearConfirmDialog: Boolean = false,
    val inputQuery: String = "",
    val quickSuggestions: List<String> = listOf(
        "Healthy Indian meals",
        "Morning sickness relief",
        "Ragi & iron nutrition",
        "Safe pregnancy stretches"
    ),
    val selectedNavIndex: Int = 2,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
