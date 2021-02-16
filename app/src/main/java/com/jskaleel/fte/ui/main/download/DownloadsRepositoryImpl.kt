package com.jskaleel.fte.ui.main.download

import androidx.lifecycle.LiveData
import com.jskaleel.fte.data.entities.LocalBooks
import com.jskaleel.fte.data.entities.SavedBooks
import com.jskaleel.fte.data.local.AppDatabase

class DownloadsRepositoryImpl(private val appDatabase: AppDatabase) : DownloadsRepository {

    private var allBooks: LiveData<List<SavedBooks>> =
        appDatabase.savedBooksDao().getAllLocalBooks()

    override fun getAllBooks(): LiveData<List<SavedBooks>> {
        return allBooks
    }

    override fun updateBooks(book: LocalBooks) {
        appDatabase.savedBooksDao().insert(
            SavedBooks(
                book.title,
                book.image,
                book.author,
                book.epub,
                book.bookid,
                book.savedPath
            )
        )
    }
}