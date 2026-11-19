package com.example.SIMATS.data.model

data class NutritionPlanData(
    val trimester: Int = 2,
    val trimesterTitle: String = "",
    val featuredMealCategory: String = "",
    val featuredMealTitle: String = "",
    val featuredMealDetails: String = "",
    val priorities: List<NutritionPriority> = emptyList(),
    val meals: List<NutritionMealItem> = emptyList()
)

data class NutritionMealItem(
    val type: String = "lunch",
    val title: String = "",
    val preparationMinutes: Int = 20,
    val tags: List<String> = emptyList(),
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList()
)

data class NutritionPriority(
    val id: Int = 0,
    val title: String = "",
    val subtitle: String = "",
    val iconType: PriorityIconType = PriorityIconType.IRON,
    val detail: String = "",
    val foods: List<String> = emptyList(),
    val tip: String = ""
)

enum class PriorityIconType {
    IRON,
    CALCIUM,
    HYDRATION
}
