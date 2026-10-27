package com.example.SIMATS.ui.ai

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AiChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    fun onEvent(event: AiChatUiEvent) {
        when (event) {
            is AiChatUiEvent.OnInputTextChanged -> {
                _uiState.update { it.copy(currentInputText = event.text) }
            }
            AiChatUiEvent.OnSendMessageClicked -> {
                val input = _uiState.value.currentInputText.trim()
                if (input.isNotEmpty()) {
                    val userMessage = ChatMessage(
                        id = System.currentTimeMillis().toString(),
                        text = input,
                        sender = "You",
                        timestamp = "2:00 PM"
                    )
                    val updatedList = _uiState.value.chatMessages + userMessage
                    _uiState.update { it.copy(chatMessages = updatedList, currentInputText = "") }
                }
            }
            is AiChatUiEvent.OnSuggestedPromptClicked -> {
                val promptMessage = ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    text = event.prompt,
                    sender = "You",
                    timestamp = "2:01 PM"
                )
                val updatedList = _uiState.value.chatMessages + promptMessage
                _uiState.update { it.copy(chatMessages = updatedList) }
            }
            is AiChatUiEvent.OnFeedbackThumbUp -> {
                // Action for positive feedback
            }
            is AiChatUiEvent.OnFeedbackThumbDown -> {
                // Action for negative feedback
            }
            is AiChatUiEvent.OnCopyMessage -> {
                // Action for copy message
            }
            is AiChatUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            AiChatUiEvent.OnHistoryClicked -> {
                // Action for viewing chat history
            }
            AiChatUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            AiChatUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
