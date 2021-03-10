package com.anomalydev.worldnewsforyou.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_query_remote_keys")
data class SearchQueryRemoteKey(
    @PrimaryKey val searchQuery: Int,
    val nextPageKey: Int
)