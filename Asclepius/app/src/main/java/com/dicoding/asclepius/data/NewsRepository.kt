package com.dicoding.asclepius.data

import com.dicoding.asclepius.data.local.room.NewsDao
import com.dicoding.asclepius.data.remote.retrofit.ApiService

class NewsRepository private constructor(
    private val apiService: ApiService,
    private val newsDao: NewsDao
) {

    companion object {
        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            apiService: ApiService,
            newsDao: NewsDao
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService, newsDao)
            }.also { instance = it }
    }
}