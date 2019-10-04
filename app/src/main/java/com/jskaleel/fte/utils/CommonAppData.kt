package com.jskaleel.fte.utils

import android.content.Context
import android.text.TextUtils
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.model.NetWorkMessage
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
        val localBooksDao = AppDatabase.getAppDatabase(context).localBooksDao()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://raw.githubusercontent.com/KaniyamFoundation/Free-Tamil-Ebooks/")
            .build()S

        return retrofit.getNewBooks().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                run {
                    if (result != null && result.books.isNotEmpty()) {
                        for ((i, localBook) in result.books.withIndex()) {
                            if (!localBooksDao.isIdAvailable(localBook.bookid)) {
                                localBook.createdAt = System.currentTimeMillis()
                                localBook.downloadId = -1
                                localBook.isDownloaded = false
                                localBook.savedPath = ""
                                val categoryList: List<String> = localBook.category.split(",").map { it.trim() }
                                val newCategory = if (TextUtils.isEmpty(categoryList[0])) {
                                    "மற்றவை"
                                } else {
                                    when {
                                        categoryList[0] == "பயணக் கட்டுரைகள்" -> "கட்டுரைகள்"
                                        categoryList[0] == "ஆளுமைகள்" -> "வரலாறு"
                                        categoryList[0] == "சமூக நாவல்" -> "நாவல்"
                                        categoryList[0] == "உணவு" -> "உடல் நலம்"
                                        categoryList[0] == "அறிவியல் புனைவுகள்" -> "அறிவியல்"
                                        categoryList[0] == "பல்சுவை இதழ்கள்" -> "மற்றவை"
                                        categoryList[0] == "கணிணி நுட்பம்" -> "கணினி நுட்பம்"
                                        categoryList[0] == "கணிணி அறிவியல்" -> "கணினி நுட்பம்"
                                        categoryList[0] == "கணினி அறிவியல்" -> "கணினி நுட்பம்"
                                        categoryList[0] == "அறிவியல் கட்டுரைகள்" -> "அறிவியல்"
                                        else -> categoryList[0]
                                    }
                                }
                                localBook.category = newCategory
                                localBooksDao.insert(localBook)
                                isNewBookAdded = true
                            }
                        }
                        RxBus.publish(NewBookAdded(isNewBookAdded))
                    }
                }
            }, { error ->
                run {
                    PrintLog.info(error.toString())
                    RxBus.publish(NetWorkMessage(context.getString(R.string.try_again_later)))
                }
            })
    }

    fun updateBooksCategory(context: Context) {
        val booksDb = AppDatabase.getAppDatabase(context).localBooksDao()
        var allBooks = booksDb.getAllLocalBooks()
        for (item in allBooks) {
            PrintLog.info("Before : ${item.category}")
            val categoryList: List<String> = item.category.split(",").map { it.trim() }
            val newCategory = if (TextUtils.isEmpty(categoryList[0])) {
                "மற்றவை"
            } else {
                when {
                    categoryList[0] == "பயணக் கட்டுரைகள்" -> "கட்டுரைகள்"
                    categoryList[0] == "ஆளுமைகள்" -> "வரலாறு"
                    categoryList[0] == "சமூக நாவல்" -> "நாவல்"
                    categoryList[0] == "உணவு" -> "உடல் நலம்"
                    categoryList[0] == "அறிவியல் புனைவுகள்" -> "அறிவியல்"
                    categoryList[0] == "பல்சுவை இதழ்கள்" -> "மற்றவை"
                    categoryList[0] == "கணிணி நுட்பம்" -> "கணினி நுட்பம்"
                    categoryList[0] == "கணிணி அறிவியல்" -> "கணினி நுட்பம்"
                    categoryList[0] == "கணினி அறிவியல்" -> "கணினி நுட்பம்"
                    categoryList[0] == "அறிவியல் கட்டுரைகள்" -> "அறிவியல்"
                    else -> categoryList[0]
                }
            }

            booksDb.updateCategory(
                newCategory, item.bookid
            )
        }


        val categoryMap: MutableSet<String> = mutableSetOf()
        allBooks = booksDb.getAllLocalBooks()
        for (item in allBooks) {
            categoryMap.add(item.category)
        }

//        val catBookList = appDataBase.localBooksDao().getBooksByCategory(categoryList[0])
//        PrintLog.info("Category--> ${item.category} --> ${categoryList[0]}")
        PrintLog.info("Category--> ${categoryMap.size} -- $categoryMap")
    }

    fun getAuthorsListWithCount(context: Context): MutableList<String> {

        val authorsList = AppDatabase.getAppDatabase(context).localBooksDao().getAuthorsList()
        val authorsMap = mutableMapOf<String, Int>()
        for (author in authorsList) {
            if (authorsMap.containsKey(author)) {
                val count = authorsMap.getValue(author)
                authorsMap[author] = count.plus(1)
            } else {
                authorsMap[author] = 1
            }
        }
        val authorsListCount = mutableListOf<String>()
        for (entry in authorsMap.entries) {
            authorsListCount.add(entry.key + "(${entry.value})")
        }
        return authorsListCount
    }

    fun getCategoryListWithCount(context: Context): MutableList<String> {

        val categoryList = AppDatabase.getAppDatabase(context).localBooksDao().getCategoryList()
        val categoryMap = mutableMapOf<String, Int>()
        for (category in categoryList) {
            if (categoryMap.containsKey(category)) {
                val count = categoryMap.getValue(category)
                categoryMap[category] = count.plus(1)
            } else {
                categoryMap[category] = 1
            }
        }
        val categoryListCount = mutableListOf<String>()
        for (entry in categoryMap.entries) {
            categoryListCount.add(entry.key + "(${entry.value})")
        }
        return categoryListCount
    }
}