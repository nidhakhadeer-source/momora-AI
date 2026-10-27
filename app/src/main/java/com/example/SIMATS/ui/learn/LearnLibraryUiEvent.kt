package com.example.SIMATS.ui.learn

sealed interface LearnLibraryUiEvent {
    data class OnSearchQueryChanged(val query: String) : LearnLibraryUiEvent
    data class OnCategorySelected(val category: String) : LearnLibraryUiEvent
    data object OnFeaturedArticleClicked : LearnLibraryUiEvent
    data class OnArticleClicked(val articleId: String) : LearnLibraryUiEvent
    data object OnSeeAllClicked : LearnLibraryUiEvent
    data object OnToggleBookmark : LearnLibraryUiEvent
    data class OnTabSelected(val tabIndex: Int) : LearnLibraryUiEvent
    data object OnBackClicked : LearnLibraryUiEvent
    data object OnNavigationHandled : LearnLibraryUiEvent
}
