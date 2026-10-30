package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.NutritionPlanData
import com.example.SIMATS.data.model.NutritionPriority
import com.example.SIMATS.data.model.PriorityIconType
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface NutritionRepository {
    fun getNutritionPlan(): Flow<NutritionPlanData>
}

class NutritionRepositoryImpl : NutritionRepository {
    private val apiService = RetrofitClient.apiService

    override fun getNutritionPlan(): Flow<NutritionPlanData> = flow {
        try {
            val response = apiService.getNutritionPlan(2)
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            NutritionPlanData(
                trimesterTitle = "Balanced ideas for your second trimester.",
                featuredMealCategory = "LUNCH INSPIRATION",
                featuredMealTitle = "Chickpea & quinoa bowl",
                featuredMealDetails = "25 min · Plant-based · Protein & fiber",
                priorities = listOf(
                    NutritionPriority(
                        id = 1,
                        title = "Iron-rich foods",
                        subtitle = "Lentils, beans and well-cooked lean meats.",
                        iconType = PriorityIconType.IRON
                    ),
                    NutritionPriority(
                        id = 2,
                        title = "Calcium & protein",
                        subtitle = "Pasteurized yogurt or fortified alternatives.",
                        iconType = PriorityIconType.CALCIUM
                    ),
                    NutritionPriority(
                        id = 3,
                        title = "Keep water nearby",
                        subtitle = "Sip regularly; adapt to your care plan.",
                        iconType = PriorityIconType.HYDRATION
                    )
                )
            )
        )
    }
}
