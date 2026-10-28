package com.example.SIMATS.data.model

data class NutritionPlanData(
    val trimesterTitle: String,
    val featuredMealCategory: String,
    val featuredMealTitle: String,
    val featuredMealDetails: String,
    val priorities: List<NutritionPriority>
)

data class NutritionPriority(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconType: PriorityIconType
)

enum class PriorityIconType {
    IRON,
    CALCIUM,
    HYDRATION
}
