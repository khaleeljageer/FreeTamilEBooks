package com.jskaleel.fte.ui.main.home

import com.jskaleel.fte.data.local.AppDatabase
import com.jskaleel.fte.data.remote.GetBooksUseCase
import com.jskaleel.fte.ui.base.BaseViewModel

class HomeViewModel(
    private val booksUseCase: GetBooksUseCase, val appDataBase: AppDatabase
) : BaseViewModel() {

    fun getBooks() {

    }
}