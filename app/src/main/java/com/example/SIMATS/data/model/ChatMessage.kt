package com.example.SIMATS.data.model

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName(value = "id", alternate = ["response_id", "question_id"])
    val id: String = "",
    @SerializedName("conversation_id")
    val conversationId: String? = null,
    val sender: MessageSender = MessageSender.AI,
    val text: String = "",
    val bulletPoints: List<String> = emptyList(),
    val footerNote: String? = null,
    val timestampText: String = "",
    val created_at: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null
)

enum class MessageSender {
    @SerializedName("USER", alternate = ["user", "patient"])
    USER,
    @SerializedName("AI", alternate = ["ai", "assistant", "bot"])
    AI
}

data class SendChatMessageData(
    val question_id: Int? = null,
    val response_id: Int? = null,
    val id: Int? = null,
    val user_id: Int? = null,
    val conversation_id: String? = null,
    val sender: String? = null,
    val text: String? = null,
    val bulletPoints: List<String>? = null,
    val footerNote: String? = null,
    val timestampText: String? = null,
    val is_pregnancy_related: Boolean? = null,
    val is_emergency: Boolean? = null,
    val provider: String? = null,
    val created_at: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null
) {
    fun toChatMessage(): ChatMessage {
        val msgId = (response_id ?: id ?: question_id ?: System.currentTimeMillis()).toString()
        val parsedSender = when (sender?.trim()?.uppercase()) {
            "USER" -> MessageSender.USER
            else -> MessageSender.AI
        }
        return ChatMessage(
            id = msgId,
            conversationId = conversation_id,
            sender = parsedSender,
            text = text.orEmpty(),
            bulletPoints = bulletPoints ?: emptyList(),
            footerNote = footerNote,
            timestampText = timestampText ?: "Momora AI · Just now",
            created_at = created_at,
            imageUrl = imageUrl
        )
    }
}
