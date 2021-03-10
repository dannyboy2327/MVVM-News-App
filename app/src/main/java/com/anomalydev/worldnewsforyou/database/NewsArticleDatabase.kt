package com.anomalydev.worldnewsforyou.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NewsArticle::class, BreakingNews::class, SearchResults::class, SearchQueryRemoteKey::class], version = 1, exportSchema = false)
abstract class NewsArticleDatabase: RoomDatabase() {

    abstract fun newsArticleDao(): NewsArticleDao
    abstract fun searchQueryRemoteKeyDao(): SearchQueryRemoteKeyDao
}