package com.example.SIMATS.data.model

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val bulletPoints: List<String> = emptyList(),
    val footerNote: String? = null,
    val timestampText: String
)

enum class MessageSender {
    USER,
    AI
}
