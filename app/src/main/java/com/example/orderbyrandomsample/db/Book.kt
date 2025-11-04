package com.example.orderbyrandomsample.db

import androidx.room.Embedded
import androidx.room.Relation

/**
 * A database entity that represents a book.
 */
data class Book(
    @Embedded
    val bookInfo: BookInfo,
    @Relation(parentColumn = "id", entityColumn = "bookId")
    var pages: List<Page>,
)
