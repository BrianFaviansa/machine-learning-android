package com.dicoding.asclepius.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
class HistoryEntity (
    @PrimaryKey(autoGenerate = true)
    @field:ColumnInfo(name = "id")
    val id: Int = 0,

    @field:ColumnInfo(name = "category")
    val category: String,

    @field:ColumnInfo(name = "confidenceScore")
    val confidenceScore: Float,

    @field:ColumnInfo(name = "imageUri", typeAffinity = ColumnInfo.BLOB)
    val imageUri: ByteArray,

    @field:ColumnInfo(name = "timestamp")
    val timestamp: String
)