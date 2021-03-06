package com.anomalydev.worldnewsforyou.database

import com.anomalydev.worldnewsforyou.api.NewsApi
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApi: NewsApi,
    private val newsArticleArticleDb: NewsArticleDatabase
) {

    private val newsArticleDao = newsArticleArticleDb.newsArticleDao()
}