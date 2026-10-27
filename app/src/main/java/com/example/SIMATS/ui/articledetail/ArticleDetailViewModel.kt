package com.example.SIMATS.ui.articledetail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ArticleDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    fun onEvent(event: ArticleDetailUiEvent) {
        when (event) {
            ArticleDetailUiEvent.OnToggleBookmark -> {
                _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
            }
            is ArticleDetailUiEvent.OnSourceLinkClicked -> {
                // Action for source links
            }
            is ArticleDetailUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            ArticleDetailUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            ArticleDetailUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
