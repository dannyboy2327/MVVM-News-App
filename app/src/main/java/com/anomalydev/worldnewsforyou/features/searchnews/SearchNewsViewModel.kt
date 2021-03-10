package com.anomalydev.worldnewsforyou.features.searchnews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.room.Query
import com.anomalydev.worldnewsforyou.database.NewsArticle
import com.anomalydev.worldnewsforyou.database.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchNewsViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel() {

    private val currentQuery = MutableStateFlow<String?>(null)

    val searchResults = currentQuery.flatMapLatest { query ->
        query?.let {
            repository.getSearchResultsPaged(query)
        } ?: emptyFlow()
    }.cachedIn(viewModelScope)

    fun onSearchQuerySubmit(query: String) {
        currentQuery.value = query
    }

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
}