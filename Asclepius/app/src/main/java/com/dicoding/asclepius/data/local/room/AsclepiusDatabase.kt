package com.dicoding.asclepius.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.data.local.entity.NewsEntity

@Database(entities = [NewsEntity::class, HistoryEntity::class], version = 1, exportSchema = false)
abstract class AsclepiusDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var instance: AsclepiusDatabase? = null
        fun getInstance(context: Context): AsclepiusDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext, AsclepiusDatabase::class.java, "asclepius_db"
            ).build()
        }
    }
}