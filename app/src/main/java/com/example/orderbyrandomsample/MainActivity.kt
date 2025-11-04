package com.example.orderbyrandomsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.orderbyrandomsample.db.AppDatabase
import com.example.orderbyrandomsample.db.Book
import com.example.orderbyrandomsample.db.BookInfo
import com.example.orderbyrandomsample.ui.theme.OrderByRandomSampleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "database-name",
        ).build()

        setContent {
            OrderByRandomSampleTheme {
                var books by remember { mutableStateOf<List<Book>>(emptyList()) }
                var chosenBook by remember { mutableStateOf<Book?>(null) }
                LaunchedEffect(Unit) {
                    val bookDao = db.bookDao()
                    books = bookDao.getAll()

                    // Works
                    // chosenBook = bookDao.getBookById(bookDao.getRandomBookId())

                    // Crash
                    chosenBook = bookDao.getRandomBook()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val bookDao = db.bookDao()
                                    bookDao.upsertBookInfo(
                                        BookInfo(
                                            id = 0,
                                            title = "Book Title ${books.size + 1}",
                                        )
                                    )
                                    books = bookDao.getAll()
                                }
                            },
                        ) {
                            Text("+")
                        }
                    }
                ) { innerPadding ->
                    BookContent(innerPadding, books, chosenBook)
                }
            }
        }
    }

    @Composable
    private fun BookContent(
        innerPadding: PaddingValues,
        books: List<Book>,
        chosenBook: Book?,
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            Books(
                books = books,
            )
            HorizontalDivider()
            if (chosenBook != null) {
                ChosenBook(
                    book = chosenBook,
                )
            } else {
                Text(
                    text = "No book chosen",
                    modifier = Modifier.padding(8.dp),
                )
            }
        }
    }
}

@Composable
fun Books(
    books: List<Book>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        for (book in books) {
            Text(text = "Book ID: ${book.bookInfo.id}, Name: ${book.bookInfo.title}")
        }
    }
}

@Composable
fun ChosenBook(
    book: Book,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Chosen Book ID: ${book.bookInfo.id}, Name: ${book.bookInfo.title}",
            color = Color.Red,
        )
    }
}

