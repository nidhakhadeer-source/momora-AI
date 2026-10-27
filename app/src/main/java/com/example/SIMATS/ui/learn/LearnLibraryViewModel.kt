package com.example.SIMATS.ui.learn

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LearnLibraryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LearnLibraryUiState())
    val uiState: StateFlow<LearnLibraryUiState> = _uiState.asStateFlow()

    fun onEvent(event: LearnLibraryUiEvent) {
        when (event) {
            is LearnLibraryUiEvent.OnSearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
            }
            is LearnLibraryUiEvent.OnCategorySelected -> {
                _uiState.update { it.copy(selectedCategory = event.category) }
            }
            LearnLibraryUiEvent.OnFeaturedArticleClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.ArticleDetail.route) }
            }
            is LearnLibraryUiEvent.OnArticleClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.ArticleDetail.route) }
            }
            LearnLibraryUiEvent.OnSeeAllClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.ArticleDetail.route) }
            }
            LearnLibraryUiEvent.OnToggleBookmark -> {
                _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
            }
            is LearnLibraryUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            LearnLibraryUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            LearnLibraryUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
