package com.anomalydev.worldnewsforyou.features.breakingnews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.anomalydev.worldnewsforyou.R
import com.anomalydev.worldnewsforyou.databinding.FragmentBreakingNewsBinding
import com.anomalydev.worldnewsforyou.shared.NewsArticleListAdapter
import com.anomalydev.worldnewsforyou.util.exhaustive
import com.anomalydev.worldnewsforyou.util.showSnackbar
import com.bumptech.glide.load.engine.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {

    private val viewModel: BreakingNewsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBreakingNewsBinding.bind(view)

        val newsArticleListAdapter = NewsArticleListAdapter(
            onItemClick = { article ->
                val uri = Uri.parse(article.url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                requireActivity().startActivity(intent)
            },
            onBookmarkClick = { article ->
               viewModel.onBookmarkClick(article)
            }
        )

        binding.apply {
            recyclerView.apply {
                adapter = newsArticleListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                // Removes flash animation
                itemAnimator?.changeDuration = 0
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.breakingNews.collect {
                    val result = it ?: return@collect

                    swipeRefreshLayout.isRefreshing = result is com.anomalydev.worldnewsforyou.util.Resource.Loading
                    recyclerView.isVisible = !result.data.isNullOrEmpty()
                    textViewError.isVisible = result.error != null && result.data.isNullOrEmpty()
                    buttonRetry.isVisible = result.error != null && result.data.isNullOrEmpty()
                    textViewError.text = getString(
                        R.string.could_not_refresh, result.error?.localizedMessage ?: getString(R.string.unknown_error_occurred))
                    newsArticleListAdapter.submitList(result.data) {
                        // This will trigger the recycler view to the top of the list
                        if (viewModel.pendingScrollToTopAfterRefresh) {
                            recyclerView.scrollToPosition(0)
                            viewModel.pendingScrollToTopAfterRefresh = false
                        }
                    }
                }
            }

            // Will trigger when user swipes down from top of screen
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.onManualRefresh()
            }

            // Will trigger when user manually triggers the retry button
            buttonRetry.setOnClickListener {
                viewModel.onManualRefresh()
            }

            // Will show a snackbar when the event has a throwable from the flow
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.events.collect { event ->
                    when (event) {
                        is BreakingNewsViewModel.Event.ShowErrorMessage ->
                            showSnackbar(
                            getString(R.string.could_not_refresh,
                            event.error.localizedMessage ?: getString(R.string.unknown_error_occurred))
                        )
                    }.exhaustive
                }
            }
        }

        // Allows menu option on top right of the screen
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        // Will refresh the layout every time the layout comes to the foreground
        viewModel.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_breaking_news, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_refresh -> {
                // Will manually refresh the layout
                viewModel.onManualRefresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}