package com.jskaleel.fte.ui.splash

import android.content.Context
import com.jskaleel.fte.R
import com.jskaleel.fte.data.entities.BooksResponse
import com.jskaleel.fte.data.entities.ErrorModel
import com.jskaleel.fte.data.local.AppDatabase
import com.jskaleel.fte.data.remote.GetBooksUseCase
import com.jskaleel.fte.data.remote.base.UseCaseResponse
import com.jskaleel.fte.ui.base.BaseViewModel
import kotlinx.coroutines.cancel

class SplashViewModel constructor(
    private val booksUseCase: GetBooksUseCase,
    private val appDatabase: AppDatabase
) : BaseViewModel() {

    fun fetchBooks(context: Context) {
        _mMessageData.value = context.getString(R.string.loading)
        _mViewState.value = true
        booksUseCase.invoke(scope, object : UseCaseResponse<BooksResponse> {
            override fun onSuccess(result: BooksResponse) {
                if (result.books.isNotEmpty()) {
                    val savedBooksDao = appDatabase.savedBooksDao()
                    val booksDao = appDatabase.localBooksDao()
                    for (book in result.books) {
                        book.apply {
                            if (savedBooksDao.isIdAvailable(book.bookid)) {
                                val savedBook = savedBooksDao.getBookByBookId(book.bookid)
                                isDownloaded = true
                                savedPath = savedBook.savedPath
                            } else {
                                isDownloaded = false
                                savedPath = ""
                            }
                        }

                        booksDao.insert(book)
                    }
                    _mViewState.postValue(false)
                } else {
                    _mMessageData.postValue("Something went wrong...\nTry after sometime...")
                }
            }

            override fun onError(errorModel: ErrorModel?) {
                _mMessageData.postValue(errorModel?.message ?: "Something wrong")
                _mViewState.postValue(true)
            }
        })
    }


    // Cancel the job when the view model is destroyed
    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}