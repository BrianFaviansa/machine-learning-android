package com.dicoding.asclepius.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.asclepius.data.local.entity.NewsEntity
import com.dicoding.asclepius.data.local.room.NewsDao
import com.dicoding.asclepius.data.remote.retrofit.ApiService

class NewsRepository private constructor(
    private val apiService: ApiService,
    private val newsDao: NewsDao
) {
    fun getNews(): LiveData<Result<List<NewsEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val localNewsData = newsDao.getNews()
            if (localNewsData.isNotEmpty()) {
                emit(Result.Success(localNewsData))
            } else {
                try {
                    val response = apiService.getHeadlineNews("cancer", "health")
                    val news = response.articles
                    val newsList = news.map { newsItem ->
                        val newsId = news.indexOf(newsItem) + 1
                        NewsEntity(
                            newsId,
                            newsItem.publishedAt,
                            newsItem.author,
                            newsItem.urlToImage,
                            newsItem.description,
                            newsItem.source?.name,
                            newsItem.title,
                            newsItem.url,
                            newsItem.content
                        )
                    }
                        .filter { it.title != "[Removed]" }
                    newsDao.deleteNews()
                    newsDao.insertNews(newsList)
                    emit(Result.Success(newsList))
                } catch (e: Exception) {
                    emit(Result.Error(e.message.toString()))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

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