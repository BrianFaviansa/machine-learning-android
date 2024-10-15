package com.dicoding.asclepius.data

import com.dicoding.asclepius.data.local.room.HistoryDao

class HistoryRepository private constructor(private val historyDao: HistoryDao) {

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null
        fun getInstance(
            historyDao: HistoryDao,
        ): HistoryRepository = instance ?: synchronized(this) {
            instance ?: HistoryRepository(historyDao)
        }.also { instance = it }
    }
}