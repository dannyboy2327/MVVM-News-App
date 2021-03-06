package com.anomalydev.worldnewsforyou.features.breakingnews

import androidx.lifecycle.ViewModel
import com.anomalydev.worldnewsforyou.database.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel() {


}