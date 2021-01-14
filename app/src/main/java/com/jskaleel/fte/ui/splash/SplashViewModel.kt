package com.jskaleel.fte.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jskaleel.fte.data.entities.BooksResponse
import com.jskaleel.fte.data.entities.ErrorModel
import com.jskaleel.fte.data.local.AppDatabase
import com.jskaleel.fte.data.remote.GetBooksUseCase
import com.jskaleel.fte.data.remote.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class SplashViewModel constructor(
    private val booksUseCase: GetBooksUseCase,
    private val appDatabase: AppDatabase
) :
    ViewModel() {

    private val _mViewState = MutableLiveData<Boolean>()
    val viewState: LiveData<Boolean> get() = _mViewState

    private val _mMessageData = MutableLiveData<String>()
    val messageData: LiveData<String> get() = _mMessageData

    private val scope = CoroutineScope(
        Job() + Dispatchers.Main
    )

    fun fetchBooks() {
        _mMessageData.value = "Loading..."
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
                }
                _mViewState.value = false
            }

            override fun onError(errorModel: ErrorModel?) {
                _mMessageData.value = errorModel?.message ?: "Something wrong"
                _mViewState.value = true
            }
        })
    }


    // Cancel the job when the view model is destroyed
    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}