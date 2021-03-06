package com.anomalydev.worldnewsforyou.features.breakingnews

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.anomalydev.worldnewsforyou.R
import com.anomalydev.worldnewsforyou.databinding.FragmentBreakingNewsBinding
import com.anomalydev.worldnewsforyou.shared.NewsArticleListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {

    private val viewModel: BreakingNewsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBreakingNewsBinding.bind(view)

        val newsArticleListAdapter = NewsArticleListAdapter()

        binding.apply {
            recyclerView.apply {
                adapter = newsArticleListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.breakingNews.collect { articles ->
                    newsArticleListAdapter.submitList(articles)
                }
            }
        }
    }
}