package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.ChatMessage
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface AiChatRepository {
    fun getInitialChatMessages(): Flow<Result<List<ChatMessage>>>
    fun sendMessage(userText: String): Flow<Result<ChatMessage>>
}

class AiChatRepositoryImpl : AiChatRepository {
    private val apiService = RetrofitClient.apiService

    override fun getInitialChatMessages(): Flow<Result<List<ChatMessage>>> = flow {
        try {
            val response = apiService.getChatMessages(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load chat history")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Check your connection")))
            }
        }
    }

    override fun sendMessage(userText: String): Flow<Result<ChatMessage>> = flow {
        try {
            val response = apiService.sendChatMessage(userText)
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to send message")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Check your connection")))
            }
        }
    }
}
