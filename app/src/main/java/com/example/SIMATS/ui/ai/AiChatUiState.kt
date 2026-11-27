package com.example.SIMATS.ui.ai

import com.example.SIMATS.data.model.ChatMessage

data class AiChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputQuery: String = "",
    val quickSuggestions: List<String> = listOf(
        "Healthy meal ideas",
        "Pregnancy symptoms",
        "Baby development",
        "Prenatal wellness"
    ),
    val selectedNavIndex: Int = 2,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
