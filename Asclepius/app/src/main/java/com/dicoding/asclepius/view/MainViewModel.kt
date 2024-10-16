package com.dicoding.asclepius.view

import com.dicoding.asclepius.data.HistoryRepository
import com.dicoding.asclepius.data.NewsRepository
import com.dicoding.asclepius.data.local.entity.HistoryEntity

class MainViewModel(
    private val newsRepository: NewsRepository,
    private val historyRepository: HistoryRepository
) {
    //    fun getNews() = newsRepository.getNews()
    fun getHistories() = historyRepository.getHistories()
    fun insertHistory(history: HistoryEntity) = historyRepository.insertHistory(history)
}