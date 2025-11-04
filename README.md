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

## Generated Code Analysis

To better understand the root cause of this crash, we have included sample generated Java code in the repository:

- **`generated_dao_crash.java`**: Generated code that causes crash (from Room 2.8.3 with KSP)
- **`generated_dao_not_crash.java`**: Generated code that works correctly (from Room 2.6.1 with KAPT)

### Root Cause of the Crash

The key difference between these two generated implementations lies in how they handle cursor positioning after collecting relation keys:

#### Crash Version (generated_dao_crash.java)
```java
while (_stmt.step()) {
    final long _tmpKey;
    _tmpKey = _stmt.getLong(_columnIndexOfId);
    if (!_collectionPages.containsKey(_tmpKey)) {
        _collectionPages.put(_tmpKey, new ArrayList<Page>());
    }
}
_stmt.reset();  // Line 17: This causes the problem!
__fetchRelationshipPageAscomExampleOrderbyrandomsampleDbPage(_connection, _collectionPages);
final Book _result;
if (_stmt.step()) {  // Line 20: Reading data after reset
    // ...
}
```

**The Problem:** The `_stmt.reset()` call on line 17 causes the `ORDER BY RANDOM()` query to be **re-evaluated**. This means:
1. First loop (lines 10-16): Collects IDs from the first random execution
2. `reset()` call: Re-executes the query with a **different random order**
3. Second read (line 20): Attempts to read data, but the ID may not match the previously collected IDs
4. Result: The `_collectionPages.get(_tmpKey_1)` on line 30 may return `null`, causing a crash

#### Working Version (generated_dao_not_crash.java)
```java
while (_cursor.moveToNext()) {
    final long _tmpKey;
    _tmpKey = _cursor.getLong(_cursorIndexOfId);
    if (!_collectionPages.containsKey(_tmpKey)) {
        _collectionPages.put(_tmpKey, new ArrayList<Page>());
    }
}
_cursor.moveToPosition(-1);  // Line 24: Just resets cursor position
__fetchRelationshipPageAscomExampleOrderbyrandomsampleDbPage(_collectionPages);
final Book _result;
if (_cursor.moveToFirst()) {  // Line 27: Reading from the same result set
    // ...
}
```

**Why It Works:** The `_cursor.moveToPosition(-1)` call on line 24 only resets the **cursor position** without re-executing the query. The same result set is maintained, ensuring the IDs collected in the first loop match the data read in the second step.

### Language Generation Note

**Note:** This issue occurs regardless of whether you generate Java or Kotlin code. When using `generateKotlin=true` in Room configuration, the same problematic pattern is generated in Kotlin, resulting in the identical crash. The underlying issue is in Room's code generation logic for handling relations with `ORDER BY RANDOM()`, not in the target language itself.
