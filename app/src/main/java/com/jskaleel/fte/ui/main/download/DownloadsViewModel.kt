package com.jskaleel.fte.ui.main.download

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jskaleel.fte.data.entities.LocalBooks
import com.jskaleel.fte.data.entities.SavedBooks
import com.jskaleel.fte.ui.base.BaseViewModel

class DownloadsViewModel(
    private val downloadRepository: DownloadsRepositoryImpl
) : BaseViewModel() {

    private val _savedBooks = MutableLiveData<MutableList<LocalBooks>>()
    val savedBooks: LiveData<MutableList<LocalBooks>> get() = _savedBooks

    fun loadSavedBooks(): LiveData<List<SavedBooks>> {
        return downloadRepository.getAllBooks()
    }

    fun updateDatabase(book: LocalBooks) {
        downloadRepository.updateBooks(book)
    }
}