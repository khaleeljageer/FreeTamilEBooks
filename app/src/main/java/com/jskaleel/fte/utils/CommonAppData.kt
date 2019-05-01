package com.jskaleel.fte.utils

import android.content.Context
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.model.NewBookAdded
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object CommonAppData {


    fun updateBooksFromApi(context: Context): Disposable {
        var isNewBookAdded = false
        val localBooksDao = AppDatabase.getAppDatabase(context).localBooksDao();

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://raw.githubusercontent.com/KaniyamFoundation/Free-Tamil-Ebooks/")
            .build().create(ApiInterface::class.java)

        return retrofit.getNewBooks().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                run {
                    if (result != null && result.books.isNotEmpty()) {
                        for (localBook in result.books) {
                            if (!localBooksDao.isIdAvailable(localBook.bookid)) {
                                localBooksDao.insert(localBook)
                                isNewBookAdded = true
                            }
                        }

                        RxBus.publish(NewBookAdded(isNewBookAdded))
                    }
                }
            }
    }
}