# Room Database ORDER BY RANDOM() Crash Sample

This project is a minimal sample that reproduces a crash issue when executing queries with `ORDER BY RANDOM()` when using Relations in Android Room Database.

## Problem Overview

When using the `@Relation` annotation to define relationships between entities in Room Database, executing a `SELECT *` query with `ORDER BY RANDOM()` causes the application to crash.

### Affected Versions

The crash occurs with the following latest version combination:

- **Kotlin**: 2.2.21
- **KSP**: 2.2.21-2.0.4
- **Room**: 2.8.3

### Workaround

The crash can be avoided by using the following versions:

- **Kotlin**: 2.0.21
- **KSP**: 2.0.21-1.0.28
- **Room**: 2.6.1

## Code Overview

### Database Structure

This sample project uses two entities: Book and Page.

#### BookInfo (Entity)
- `id`: Primary key
- `title`: Book title

#### Page (Entity)
- `id`: Primary key
- `content`: Page content
- `pageNumber`: Page number
- `bookId`: Foreign key to BookInfo

#### Book (Relation Object)
Uses the `@Relation` annotation to link BookInfo and Page:
```kotlin
data class Book(
    @Embedded
    val bookInfo: BookInfo,
    @Relation(parentColumn = "id", entityColumn = "bookId")
    var pages: List<Page>,
)
```

### Query That Causes Crash

The following query defined in `BookDao.kt:20-21` causes the crash:

```kotlin
@Transaction
@Query("SELECT * FROM BookInfo ORDER BY RANDOM() LIMIT 1")
suspend fun getRandomBook(): Book?
```

### Working Query

The crash can be avoided by splitting it into two steps:

```kotlin
@Transaction
@Query("SELECT id FROM BookInfo ORDER BY RANDOM() LIMIT 1")
suspend fun getRandomBookId(): Long

@Transaction
@Query("SELECT * FROM BookInfo WHERE id = :id")
suspend fun getBookById(id: Long): Book
```

You can see the usage comparison in `MainActivity.kt:52-56`:

```kotlin
// Works (workaround)
// chosenBook = bookDao.getBookById(bookDao.getRandomBookId())

// Crashes (when using latest versions)
chosenBook = bookDao.getRandomBook()
```

## Project Setup

1. Clone the project
2. Open in Android Studio
3. Build and run

## Steps to Reproduce the Crash

1. Please refer the commit 4f1e81dd5ead4cf6286ecbec955ff43340fa146b 
2. Build and run the application
3. The app will crash on startup

## Purpose
