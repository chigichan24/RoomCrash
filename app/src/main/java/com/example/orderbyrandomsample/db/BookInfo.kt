package com.example.orderbyrandomsample.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
)
