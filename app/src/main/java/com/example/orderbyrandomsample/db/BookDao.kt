package com.example.orderbyrandomsample.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

/**
 * An interface that defines database operations for the Book entity.
 */
@Dao
interface BookDao {

    @Transaction
    @Query("SELECT * FROM BookInfo")
    suspend fun getAll(): List<Book>

    @Transaction
    @Query("SELECT * FROM BookInfo ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomBook(): Book?

    @Transaction
    @Query("SELECT id FROM BookInfo ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomBookId(): Long

    @Transaction
    @Query("SELECT * FROM BookInfo WHERE id = :id")
    suspend fun getBookById(id: Long): Book

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookInfo(vararg bookInfo: BookInfo)
}
