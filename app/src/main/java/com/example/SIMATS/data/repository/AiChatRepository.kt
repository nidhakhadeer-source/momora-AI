package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.ChatMessage
import com.example.SIMATS.data.model.SendChatMessageData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

interface AiChatRepository {
    fun getInitialChatMessages(conversationId: String? = null): Flow<Result<List<ChatMessage>>>
    fun sendMessage(
        userText: String,
        conversationId: String? = null,
        imageBytes: ByteArray? = null,
        imageMimeType: String? = null,
        fileName: String? = null
    ): Flow<Result<SendChatMessageData>>
}

class AiChatRepositoryImpl : AiChatRepository {
    private val apiService = RetrofitClient.apiService

    override fun getInitialChatMessages(conversationId: String?): Flow<Result<List<ChatMessage>>> = flow {
        try {
            val response = apiService.getChatMessages(SessionManager.getUserId(), conversationId)
            if (response.success) {
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

    override fun sendMessage(
        userText: String,
        conversationId: String?,
        imageBytes: ByteArray?,
        imageMimeType: String?,
        fileName: String?
    ): Flow<Result<SendChatMessageData>> = flow {
        try {
            val response = if (imageBytes != null && imageBytes.isNotEmpty()) {
                val mediaType = MediaType.parse(imageMimeType ?: "image/jpeg")
                val requestFile = RequestBody.create(mediaType, imageBytes)
                val safeFileName = fileName ?: "chat_image.jpg"
                val imagePart = MultipartBody.Part.createFormData("image", safeFileName, requestFile)
                val textType = MediaType.parse("text/plain")
                val messagePart = RequestBody.create(textType, userText)
                val convPart = conversationId?.let { RequestBody.create(textType, it) }
                apiService.sendChatMessageWithImage(messagePart, convPart, imagePart)
            } else {
                apiService.sendChatMessage(userText, conversationId)
            }
            if (response.success) {
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
