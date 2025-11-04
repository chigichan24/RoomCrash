package com.example.orderbyrandomsample.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * A main database class that holds the database and serves as the main access point for the underlying connection.
 */
@Database(entities = [BookInfo::class, Page::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
