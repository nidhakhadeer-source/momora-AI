package com.example.SIMATS.ui.learn

import com.example.SIMATS.R

data class ArticleItem(
    val id: String,
    val title: String,
    val categoryAndReadTime: String,
    val iconRes: Int
)

data class LearnLibraryUiState(
    val title: String = "A little knowledge. More calm.",
    val subtitle: String = "Evidence-informed reading for your journey.",
    val searchQuery: String = "",
    val selectedCategory: String = "For you",
    val categoryOptions: List<String> = listOf("For you", "Your body", "Baby", "Birth", "Wellbeing"),
    val featuredTag: String = "FEATURED FOR WEEK 28 · 5 MIN READ",
    val featuredTitle: String = "Getting to know your baby's movements",
    val featuredDescription: String = "What to notice, and when to call your care team.",
    val pickedArticles: List<ArticleItem> = listOf(
        ArticleItem("1", "Iron-rich vegetarian meals", "Nutrition · 4 min read", R.drawable.ic_cutlery),
        ArticleItem("2", "Finding a comfortable sleep position", "Wellbeing · 3 min read", R.drawable.ic_moon),
        ArticleItem("3", "Your 28-week appointment", "Care essentials · 4 min read", R.drawable.ic_clipboard)
    ),
    val disclaimerText: String = "Educational content · Sources and review dates are listed in each article.",
    val isBookmarked: Boolean = false,
    val selectedTab: Int = 3,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
