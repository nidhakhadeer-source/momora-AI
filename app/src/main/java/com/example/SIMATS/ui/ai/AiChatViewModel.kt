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

class AiChatViewModel(
    private val repository: AiChatRepository = AiChatRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    init {
        loadInitialMessages()
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
        }
    }

    private fun loadInitialMessages() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getInitialChatMessages().collect { result ->
                result.onSuccess { list ->
                    _uiState.value = _uiState.value.copy(
                        messages = list,
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
        if (trimmed.isBlank() || _uiState.value.isLoading) return

        val tempId = "temp_${System.currentTimeMillis()}"
        val userMsg = ChatMessage(
            id = tempId,
            sender = MessageSender.USER,
            text = trimmed,
            timestampText = "You · Just now"
        )

        // 1. Immediately display the user's message and loading indicator
        val updatedList = _uiState.value.messages + userMsg
        _uiState.value = _uiState.value.copy(
            messages = updatedList,
            inputQuery = "",
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            repository.sendMessage(trimmed).collect { result ->
                result.onSuccess { sendData ->
                    // 2. Map response IDs & fields correctly
                    val aiMessage = sendData.toChatMessage()
                    val officialQuestionId = sendData.question_id?.toString()

                    val currentMessages = _uiState.value.messages
                    val reconciledMessages = currentMessages.map { msg ->
                        if (msg.id == tempId && !officialQuestionId.isNullOrBlank()) {
                            msg.copy(id = officialQuestionId)
                        } else {
                            msg
                        }
                    }

                    // 3. Immediately append the AI response to the displayed list and stop loading
                    _uiState.value = _uiState.value.copy(
                        messages = reconciledMessages + aiMessage,
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

    class Factory(private val repository: AiChatRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AiChatViewModel(repository) as T
        }
    }
}
