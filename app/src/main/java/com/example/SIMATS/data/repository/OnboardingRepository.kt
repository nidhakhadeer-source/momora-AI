package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.OnboardingFeature
import com.example.SIMATS.data.model.OnboardingIconType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface OnboardingRepository {
    fun getOnboardingFeatures(): Flow<List<OnboardingFeature>>
}

class OnboardingRepositoryImpl : OnboardingRepository {
    override fun getOnboardingFeatures(): Flow<List<OnboardingFeature>> = flow {
        emit(
            listOf(
                OnboardingFeature(
                    id = 1,
                    title = "Care that knows you",
                    description = "Your journey, your preferences.",
                    iconType = OnboardingIconType.HEART
                ),
                OnboardingFeature(
                    id = 2,
                    title = "Your health, your choice",
                    description = "You control who sees your information.",
                    iconType = OnboardingIconType.SHIELD
                )
            )
        )
    }
}
