package com.anomalydev.worldnewsforyou.features.breakingnews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anomalydev.worldnewsforyou.database.NewsArticle
import com.anomalydev.worldnewsforyou.database.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel() {

    private val _breakingNewsFlow = MutableStateFlow<List<NewsArticle>>(emptyList())
    val breakingNews: Flow<List<NewsArticle>>
        get() = _breakingNewsFlow

    init {
        viewModelScope.launch {
            val news = repository.getBreakingNews()
            _breakingNewsFlow.value = news
        }
    }
}