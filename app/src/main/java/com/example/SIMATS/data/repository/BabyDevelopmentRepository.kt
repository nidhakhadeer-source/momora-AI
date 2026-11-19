package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.BabyDevelopmentData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface BabyDevelopmentRepository {
    fun getDevelopmentData(week: Int): Flow<Result<BabyDevelopmentData>>
}

class BabyDevelopmentRepositoryImpl : BabyDevelopmentRepository {
    private val apiService = RetrofitClient.apiService

    override fun getDevelopmentData(week: Int): Flow<Result<BabyDevelopmentData>> = flow {
        try {
            val response = apiService.getBabyDevelopment(week)
            if (response.success && response.data != null) {
                val cleaned = response.data.let { d ->
                    d.copy(
                        weekSubtitle = d.weekSubtitle.replace(Regex("""\s*\?\?\s*"""), " · "),
                        lengthCmText = d.lengthCmText.replace(Regex("""\s*\?\?\s*"""), " · "),
                        lengthSubText = d.lengthSubText.replace(Regex("""\s*\?\?\s*"""), " · "),
                        weightGramText = d.weightGramText.replace(Regex("""\s*\?\?\s*"""), " · "),
                        weightSubText = d.weightSubText.replace(Regex("""\s*\?\?\s*"""), " · "),
                        highlights = d.highlights.map { h ->
                            h.copy(
                                title = h.title.replace(Regex("""\s*\?\?\s*"""), " · "),
                                subtitle = h.subtitle.replace(Regex("""\s*\?\?\s*"""), " · "),
                                detail = h.detail.replace(Regex("""\s*\?\?\s*"""), " · "),
                                tip = h.tip.replace(Regex("""\s*\?\?\s*"""), " · ")
                            )
                        }
                    )
                }
                emit(Result.success(cleaned))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load baby development data")))
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
