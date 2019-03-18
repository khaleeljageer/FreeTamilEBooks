package com.jskaleel.fte.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.fte.database.AppDatabase

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appDatabase = AppDatabase.getAppDatabase(applicationContext)
    }
}