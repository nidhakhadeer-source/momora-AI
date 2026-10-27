package com.example.SIMATS.ui.articledetail

sealed interface ArticleDetailUiEvent {
    data object OnToggleBookmark : ArticleDetailUiEvent
    data class OnSourceLinkClicked(val urlText: String) : ArticleDetailUiEvent
    data class OnTabSelected(val tabIndex: Int) : ArticleDetailUiEvent
    data object OnBackClicked : ArticleDetailUiEvent
    data object OnNavigationHandled : ArticleDetailUiEvent
}
