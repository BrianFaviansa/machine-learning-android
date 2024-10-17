package com.dicoding.asclepius.view

import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.HistoryRepository
import com.dicoding.asclepius.data.local.entity.HistoryEntity

class HistoryViewModel(private val repository: HistoryRepository) : ViewModel() {
    fun getHistories() = repository.getHistories()
    fun insertHistory(history: HistoryEntity) = repository.insertHistory(history)
}