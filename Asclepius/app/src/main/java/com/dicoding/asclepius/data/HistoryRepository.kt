package com.dicoding.asclepius.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.data.local.room.HistoryDao

class HistoryRepository private constructor(private val historyDao: HistoryDao) {
    fun getHistories(): LiveData<Result<List<HistoryEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val histories = historyDao.getHistories()
            emit(Result.Success(histories))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun insertHistory(history: HistoryEntity): LiveData<Result<HistoryEntity>> = liveData {
        emit(Result.Loading)
        try {
            historyDao.insertHistory(history)
            emit(Result.Success(history))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

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