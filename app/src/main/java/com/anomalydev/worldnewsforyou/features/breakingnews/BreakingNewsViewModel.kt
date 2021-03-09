package com.anomalydev.worldnewsforyou.features.breakingnews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anomalydev.worldnewsforyou.database.NewsArticle
import com.anomalydev.worldnewsforyou.database.NewsRepository
import com.anomalydev.worldnewsforyou.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel() {

    // Channel to send and receive triggers from flows
    private val refreshTriggerChannel = Channel<Refresh>()
    // Triggers for when channel receives a new flow
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    // Channel to send and receive different events
    private val _eventChannel = Channel<Event>()
    val events = _eventChannel.receiveAsFlow()

    // Trigger to scroll to top of screen
    var pendingScrollToTopAfterRefresh = false

    val breakingNews = refreshTrigger.flatMapLatest { refresh ->
        repository.getBreakingNews(
            refresh == Refresh.FORCE,
            onFetchSuccess = {
                // If success succeeds, set scroll to top to true
                pendingScrollToTopAfterRefresh = true
            },
            onFetchFailed = { t ->
                viewModelScope.launch { _eventChannel.send(Event.ShowErrorMessage(t)) }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            repository.deleteNonBookmarkedArticlesOlderThan(
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            )
        }
    }

    /**
     * Will refresh the data flow from breakingNews when onStart is triggered on the
     * lifecycle.
     */
    fun onStart() {
        if (breakingNews.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.NORMAL)
            }
        }

    }

    /**
     * Will refresh the data flow from breakingNews when user manually triggers the
     * refresh options.
     */
    fun onManualRefresh() {
        if (breakingNews.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.FORCE)
            }
        }

    }

    enum class Refresh {
        FORCE, NORMAL
    }

    // Class is used to trigger events for snackbar
    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }
}