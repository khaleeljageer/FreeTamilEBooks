package com.jskaleel.fte.ui.main.download

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jskaleel.fte.data.entities.LocalBooks
import com.jskaleel.fte.data.entities.SavedBooks
import com.jskaleel.fte.data.local.AppDatabase
import com.jskaleel.fte.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class DownloadsViewModel(
    private val appDataBase: AppDatabase
) : BaseViewModel() {

    private val _savedBooks = MutableLiveData<MutableList<LocalBooks>>()
    val savedBooks: LiveData<MutableList<LocalBooks>> get() = _savedBooks

    fun loadSavedBooks() {
        scope.launch {
            val localBooks: MutableList<LocalBooks> = mutableListOf()
            val allBooks = appDataBase.savedBooksDao().getAllLocalBooks()
            for (book in allBooks) {
                localBooks.add(
                    LocalBooks(
                        book.title,
                        book.bookid,
                        book.author,
                        book.image,
                        book.epub,
                        "",
                        true,
                        book.savedPath
                    )
                )
            }
            _savedBooks.postValue(localBooks)
        }
    }

    fun updateDatabase(book: LocalBooks) {
        appDataBase.savedBooksDao().insert(
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