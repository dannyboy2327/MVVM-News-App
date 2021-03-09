package com.anomalydev.worldnewsforyou.features.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anomalydev.worldnewsforyou.database.NewsArticle
import com.anomalydev.worldnewsforyou.database.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel() {

    val bookmarks = repository.getAllBookmarkedArticles()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    /**
     *  Will modify old article with new article based on its bookmark value
     */
    fun onBookmarkClick(article: NewsArticle) {
        val currentlyBookmarked = article.isBookmarked
        val updatedArticle = article.copy(isBookmarked = !currentlyBookmarked)
        viewModelScope.launch {
            repository.updateArticle(updatedArticle)
        }
    }

    fun onDeleteAllBookmarks() {
        viewModelScope.launch {
            repository.resetAllBookmarks()
        }
    }
}