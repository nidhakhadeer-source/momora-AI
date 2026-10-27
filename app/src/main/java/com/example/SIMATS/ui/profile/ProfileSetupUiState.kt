package com.example.SIMATS.ui.profile

data class ProfileSetupUiState(
    val stepText: String = "GETTING TO KNOW YOU - 2 OF 5",
    val progress: Float = 0.4f,
    val preferredName: String = "Maya",
    val dateOfBirth: String = "18 May 1996",
    val isFirstPregnancy: Boolean = true,
    val pregnancyType: String = "One baby",
    val pregnancyTypeOptions: List<String> = listOf("One baby", "Twins", "Triplets or more"),
    val careTeam: String = "Dr Amira Shah · Riverside Maternity",
    val calculatedAgeText: String = "Your age is 30. Only add details you're comfortable sharing.",
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
