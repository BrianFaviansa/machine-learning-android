package com.dicoding.asclepius.data.remote.retrofit

import com.dicoding.asclepius.data.remote.response.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    suspend fun getHeadlineNews(
        @Query("q") query: String,
        @Query("category") category: String
    ) : NewsResponse
}