package com.jskaleel.fte.ui.main.download

import androidx.lifecycle.LiveData
import com.jskaleel.fte.data.entities.LocalBooks
import com.jskaleel.fte.data.entities.SavedBooks

interface DownloadsRepository {
    fun getAllBooks(): LiveData<List<SavedBooks>>
    fun updateBooks(book: LocalBooks)
}