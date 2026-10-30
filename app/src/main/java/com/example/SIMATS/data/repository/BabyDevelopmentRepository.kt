package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.BabyDevelopmentData
import com.example.SIMATS.data.model.BabyDevelopmentHighlight
import com.example.SIMATS.data.model.HighlightIconType
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface BabyDevelopmentRepository {
    fun getDevelopmentData(week: Int): Flow<BabyDevelopmentData>
}

class BabyDevelopmentRepositoryImpl : BabyDevelopmentRepository {
    private val apiService = RetrofitClient.apiService

    override fun getDevelopmentData(week: Int): Flow<BabyDevelopmentData> = flow {
        try {
            val response = apiService.getBabyDevelopment(week)
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            BabyDevelopmentData(
                currentWeek = week,
                weekSubtitle = "Week $week · Your baby's weekly development.",
                lengthCmText = "30 cm",
                lengthSubText = "Head to heel · approximate",
                weightGramText = "600 g",
                weightSubText = "educational estimate",
                highlights = listOf(
                    BabyDevelopmentHighlight(
                        id = 1,
                        title = "A world of sound",
                        subtitle = "Hearing continues to develop this week.",
                        iconType = HighlightIconType.SOUND
                    ),
                    BabyDevelopmentHighlight(
                        id = 2,
                        title = "Little movements",
                        subtitle = "You may notice your baby's own rhythm.",
                        iconType = HighlightIconType.MOVEMENT
                    )
                )
            )
        )
    }
}
