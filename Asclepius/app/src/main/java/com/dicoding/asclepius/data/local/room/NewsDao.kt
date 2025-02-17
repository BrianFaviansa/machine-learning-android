package com.dicoding.asclepius.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.entity.NewsEntity

@Dao
interface NewsDao {
    @Query("SELECT * FROM news")
    suspend fun getNews(): List<NewsEntity>

    @Query("DELETE FROM news")
    suspend fun deleteNews()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: List<NewsEntity>)
}