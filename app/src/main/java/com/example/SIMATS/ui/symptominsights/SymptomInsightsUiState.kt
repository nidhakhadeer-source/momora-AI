package com.example.SIMATS.ui.symptominsights

import com.example.SIMATS.R

data class GentleTipItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int
)

data class SymptomInsightsUiState(
    val title: String = "Let's make sense of it",
    val symptomLoggedSubtitle: String = "Heartburn · Mild · Logged 26 Oct, 1:30 PM",
    val insightTag: String = "MOMORA AI · EDUCATIONAL INSIGHT",
    val insightTitle: String = "You noticed it after eating",
    val insightDescription: String = "You've logged heartburn twice this week, both after lunch. This may be a useful pattern to discuss, not a diagnosis.",
    val tipsList: List<GentleTipItem> = listOf(
        GentleTipItem("Try smaller meals", "Eat slowly and notice personal triggers.", R.drawable.ic_utensils),
        GentleTipItem("Stay upright after eating", "Avoid lying down straight after a meal.", R.drawable.ic_armchair),
        GentleTipItem("Ask before taking a remedy", "A pharmacist or clinician can advise.", R.drawable.ic_chat_bubble)
    ),
    val warningTitle: String = "When not to treat it as heartburn",
    val warningDescription: String = "Severe pain under the ribs, chest pain, severe headache or vision changes need urgent care. Contact your maternity team; use local emergency services for severe chest pain or breathing difficulty.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
