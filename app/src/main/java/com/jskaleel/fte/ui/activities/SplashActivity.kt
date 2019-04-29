package com.jskaleel.fte.ui.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import com.google.gson.Gson
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.model.BooksResponse
import com.jskaleel.fte.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*
import java.io.InputStream
import java.lang.ref.WeakReference
import com.jskaleel.fte.utils.NetworkSchedulerService

class SplashActivity : BaseActivity() {
    private val sleepDuration: Long = 2000
    private var activityDestroyed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val mHandler = FTEHandler(this@SplashActivity)
        mHandler.sendEmptyMessageDelayed(1, sleepDuration)
    }

    private class FTEHandler internal constructor(splashScreen: SplashActivity) : Handler() {
        internal var splash: WeakReference<SplashActivity> = WeakReference(splashScreen)

        override fun handleMessage(msg: Message) {
            val activity = splash.get()
            if (activity != null && msg.what == 1 && !activity.activityDestroyed) {
                activity.checkForLocalBooks()
            }
        }
    }

    private fun checkForLocalBooks() {
        txtLoading.visibility = View.VISIBLE
        progressLoader.visibility = View.VISIBLE

        val localBooks = appDatabase.localBooksDao().getAllLocalBooks()
        if (localBooks.isEmpty()) {
            val booksString = readJSONFromAsset()
            if (booksString != null) {
                val asyncTask = ConvertAssetsToDB(booksString, appDatabase, object : TaskProgressUpdate {
                    override fun taskFinished() {
                        startNextActivity()
                    }

                    override fun taskProgressUpdate(percent: Int) {
                        progressLoader.progress = percent
                    }
                })
                asyncTask.execute()
            }
        } else {
            progressLoader.progress = 100
            startNextActivity()
        }
    }

    fun startNextActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        this@SplashActivity.finish()
    }

    class ConvertAssetsToDB(
        private val booksString: String,
        private val appDatabase: AppDatabase,
        private val progressUpdate: TaskProgressUpdate
    ) : AsyncTask<Boolean, Int, Boolean>() {
        override fun doInBackground(vararg p0: Boolean?): Boolean {
            val booksJson = Gson().fromJson(booksString, BooksResponse::class.java)
            if (booksJson != null && !booksJson.books.isEmpty()) {
                for ((i, book) in booksJson.books.withIndex()) {
                    publishProgress(((i.div(booksJson.books.size.toDouble())).times(100).toInt()))
                    appDatabase.localBooksDao().insert(book)
                }
                return true
            }
            return false
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            progressUpdate.taskProgressUpdate(values[0]!!)
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            progressUpdate.taskFinished()
        }
    }

    private fun readJSONFromAsset(): String? {
        val json: String
        try {
            val inputStream: InputStream = baseContext.assets.open("booksdb.json")
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    interface TaskProgressUpdate {
        fun taskProgressUpdate(percent: Int)
        fun taskFinished()
    }

    override fun onDestroy() {
        activityDestroyed = true
        super.onDestroy()
    }
}