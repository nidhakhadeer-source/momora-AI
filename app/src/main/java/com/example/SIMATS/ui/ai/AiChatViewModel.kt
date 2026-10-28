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
            AiChatUiEvent.OnBackClicked -> {}
            AiChatUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadInitialMessages() {
        viewModelScope.launch {
            repository.getInitialChatMessages().collect { list ->
                _uiState.value = _uiState.value.copy(messages = list)
            }
        }
    }

    private fun sendUserMessage(queryText: String) {
        if (queryText.isBlank()) return

        val userMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = MessageSender.USER,
            text = queryText,
            timestampText = "You · Just now"
        )

        val updatedList = _uiState.value.messages + userMsg
        _uiState.value = _uiState.value.copy(
            messages = updatedList,
            inputQuery = "",
            isLoading = true
        )

        viewModelScope.launch {
            repository.sendMessage(queryText).collect { aiResponse ->
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + aiResponse,
                    isLoading = false
                )
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
