package com.jskaleel.fte.ui.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.fte.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}