package com.example.orderbyrandomsample.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A database entity that represents a page.
 */
@Entity(
    indices = [Index("bookId")],
)
data class Page(
    @PrimaryKey
    val id: Long,
    val content: String,
    val pageNumber: Int,
    val bookId: Long
)
