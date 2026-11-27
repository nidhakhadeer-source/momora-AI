package com.example.SIMATS.ui.ai

sealed interface AiChatUiEvent {
    data class OnInputQueryChanged(val query: String) : AiChatUiEvent
    data object OnSendMessageClicked : AiChatUiEvent
    data class OnSuggestionClicked(val suggestion: String) : AiChatUiEvent
    data class OnNavTabSelected(val index: Int) : AiChatUiEvent
    data object OnBackClicked : AiChatUiEvent
    data object OnNotificationClicked : AiChatUiEvent
    data object OnRetryClicked : AiChatUiEvent

    // ChatGPT-Style Conversation Management
    data object OnNewChatClicked : AiChatUiEvent
    data object OnOpenHistoryClicked : AiChatUiEvent
    data object OnDismissHistoryClicked : AiChatUiEvent
    data class OnSelectSession(val sessionId: String) : AiChatUiEvent
    data object OnClearChatClicked : AiChatUiEvent
    data object OnConfirmClearChat : AiChatUiEvent
    data object OnDismissClearDialog : AiChatUiEvent
}
