package com.example.SIMATS.ui.ai

sealed interface AiChatUiEvent {
    data class OnInputTextChanged(val text: String) : AiChatUiEvent
    data object OnSendMessageClicked : AiChatUiEvent
    data class OnSuggestedPromptClicked(val prompt: String) : AiChatUiEvent
    data class OnFeedbackThumbUp(val messageId: String) : AiChatUiEvent
    data class OnFeedbackThumbDown(val messageId: String) : AiChatUiEvent
    data class OnCopyMessage(val messageId: String) : AiChatUiEvent
    data class OnTabSelected(val tabIndex: Int) : AiChatUiEvent
    data object OnHistoryClicked : AiChatUiEvent
    data object OnBackClicked : AiChatUiEvent
    data object OnNavigationHandled : AiChatUiEvent
}
