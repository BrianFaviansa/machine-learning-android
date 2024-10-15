package com.dicoding.asclepius.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.HistoryRepository
import com.dicoding.asclepius.data.NewsRepository
import com.dicoding.asclepius.di.Injection

class ViewModelFactory(
    private val newsRepository: NewsRepository,
    private val historyRepository: HistoryRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(newsRepository, historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideNewsRepository(),
                    Injection.provideHistoryRepository()
                )
            }
    }
}