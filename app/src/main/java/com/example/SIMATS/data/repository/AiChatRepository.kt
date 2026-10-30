package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.ChatMessage
import com.example.SIMATS.data.model.MessageSender
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AiChatRepository {
    fun getInitialChatMessages(): Flow<List<ChatMessage>>
    fun sendMessage(userText: String): Flow<ChatMessage>
}

class AiChatRepositoryImpl : AiChatRepository {
    private val apiService = RetrofitClient.apiService

    override fun getInitialChatMessages(): Flow<List<ChatMessage>> = flow {
        try {
            val response = apiService.getChatMessages(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            listOf(
                ChatMessage(
                    id = "1",
                    sender = MessageSender.USER,
                    text = "What can I try for heartburn after lunch?",
                    timestampText = "You · 08:20"
                ),
                ChatMessage(
                    id = "2",
                    sender = MessageSender.AI,
                    text = "That sounds uncomfortable, Maya. You could try:",
                    bulletPoints = listOf(
                        "Smaller meals, eaten slowly.",
                        "Sitting upright after eating.",
                        "Noticing foods that trigger discomfort."
                    ),
                    footerNote = "Check with your pharmacist or maternity team before trying medicines or supplements.",
                    timestampText = "Momora AI · 08:20"
                )
            )
        )
    }

    override fun sendMessage(userText: String): Flow<ChatMessage> = flow {
        try {
            val response = apiService.sendChatMessage(userText)
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        // Fallback response with explicit educational disclaimer
        emit(
            ChatMessage(
                id = System.currentTimeMillis().toString(),
                sender = MessageSender.AI,
                text = "Thank you for asking. Here is some guidance regarding '$userText':",
                bulletPoints = listOf(
                    "Stay hydrated with small sips of water.",
                    "Rest comfortably and avoid strenuous activity.",
                    "Always consult ${SessionManager.getDoctorName()} if you experience unusual symptoms."
                ),
                footerNote = "Check with your maternity team for clinical advice.",
                timestampText = "Momora AI · Just now"
            )
        )
    }
}
