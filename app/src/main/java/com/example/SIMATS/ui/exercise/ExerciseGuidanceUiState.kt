package com.example.SIMATS.ui.exercise

import com.example.SIMATS.R

data class ExerciseStepItem(
    val title: String,
    val description: String,
    val iconRes: Int
)

data class ExerciseGuidanceUiState(
    val title: String = "Move with kindness",
    val subtitle: String = "Comfortable movement · Third trimester",
    val selectedIntensity: String = "Gentle",
    val intensityOptions: List<String> = listOf("Gentle", "10 minutes", "No equipment"),
    val routineTitle: String = "A comfortable reset",
    val exerciseSteps: List<ExerciseStepItem> = listOf(
        ExerciseStepItem("Easy walk · 5 minutes", "Choose a pace where you can still talk.", R.drawable.ic_walk),
        ExerciseStepItem("Supported stretch · 3 minutes", "Use a chair and stay within a comfortable range.", R.drawable.ic_stretch),
        ExerciseStepItem("Slow breathing · 2 minutes", "Relax your shoulders; don't hold your breath.", R.drawable.ic_breath)
    ),
    val safetyTitle: String = "Check what's right for you",
    val safetyDescription: String = "Follow any activity restrictions from your clinician. Stop for pain, dizziness, bleeding, chest pain or fluid leakage and seek advice.",
    val disclaimerText: String = "Rest is a valid choice too. You don't need to finish to benefit.",
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
