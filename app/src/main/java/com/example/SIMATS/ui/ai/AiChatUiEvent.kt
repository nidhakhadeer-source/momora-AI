package com.example.SIMATS.ui.ai

sealed interface AiChatUiEvent {
    data class OnInputQueryChanged(val query: String) : AiChatUiEvent
    data object OnSendMessageClicked : AiChatUiEvent
    data class OnSuggestionClicked(val suggestion: String) : AiChatUiEvent
    data class OnNavTabSelected(val index: Int) : AiChatUiEvent
    data object OnBackClicked : AiChatUiEvent
    data object OnNotificationClicked : AiChatUiEvent
}
