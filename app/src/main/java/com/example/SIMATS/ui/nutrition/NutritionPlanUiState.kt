package com.example.SIMATS.ui.nutrition

import com.example.SIMATS.R

data class MealPlanItem(
    val id: String,
    val title: String,
    val description: String,
    val actionText: String,
    val isCompleted: Boolean,
    val iconRes: Int
)

data class NutritionPlanUiState(
    val title: String = "Nourish your day",
    val menuSubtitle: String = "Monday's menu · Vegetarian · Week 28",
    val focusTag: String = "TODAY'S FOOD FOCUS",
    val focusTitle: String = "Iron + a little vitamin C",
    val focusDescription: String = "Pair beans or lentils with peppers or citrus to help your body absorb plant-based iron.",
    val mealItems: List<MealPlanItem> = listOf(
        MealPlanItem("1", "Breakfast · 8:00 AM", "Porridge, berries and pumpkin seeds", "Done", true, R.drawable.ic_bowl),
        MealPlanItem("2", "Lunch · 12:30 PM", "Lentil bowl with spinach and lemon", "Recipe", false, R.drawable.ic_sun_meal),
        MealPlanItem("3", "Snack · 3:30 PM", "Orange and a handful of nuts", "View", false, R.drawable.ic_apple),
        MealPlanItem("4", "Dinner · 7:00 PM", "Tofu, broccoli and brown rice", "Recipe", false, R.drawable.ic_moon)
    ),
    val disclaimerText: String = "Wash produce well and cook food thoroughly. Keep taking iron as prescribed; food suggestions don't replace treatment.",
    val isBookmarked: Boolean = false,
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
