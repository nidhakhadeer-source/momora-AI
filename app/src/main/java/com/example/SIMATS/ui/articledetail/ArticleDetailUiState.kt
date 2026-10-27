package com.example.SIMATS.ui.articledetail

data class ArticleDetailUiState(
    val title: String = "Getting to know your baby's movements",
    val metadata: String = "Baby & you · 5 min read · Week 28",
    val editorialReviewText: String = "Momora editorial team · Reviewed 12 Oct 2026 Based on NHS and RCOG patient guidance",
    val paragraph1: String = "Those little flutters can become rolls, stretches and kicks. Learning your baby's own pattern matters more than reaching a specific number.",
    val section1Title: String = "What might you notice?",
    val section1Body: String = "By around 28 weeks, many people recognise a daily pattern. Babies have periods of sleep and activity. Movements may feel different as pregnancy progresses, but should not simply become less frequent near your due date.",
    val section2Title: String = "There isn't one normal count",
    val section2Body: String = "Pay attention to what is usual for your baby. If your care team suggests a counting method, follow their advice. An app log cannot tell you whether your baby is well.",
    val warningTitle: String = "Less movement? Call now.",
    val warningDescription: String = "If movements slow down, stop or change, contact your maternity team immediately, even at night. Don't wait until tomorrow or rely on a home Doppler.",
    val sourcesTitle: String = "Sources & further reading",
    val sourcesLinks: List<String> = listOf(
        "NHS · Your baby's movements ↗",
        "RCOG · Your baby's movements in pregnancy ↗"
    ),
    val isBookmarked: Boolean = false,
    val selectedTab: Int = 3,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
