package com.jskaleel.fte.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.trimmedLength
import com.jskaleel.fte.R
import com.jskaleel.fte.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val key = it.toString()
                    if (key.trimmedLength() > 0) {
                        binding.edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_arrow_back_black_24dp,
                            0,
                            R.drawable.ic_clear_black_24dp,
                            0
                        )
                    } else if (key.trimmedLength() == 0) {
                        binding.edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_arrow_back_black_24dp,
                            0,
                            0,
                            0
                        )
                    }
                }
            }
        })



        binding.edtSearch.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                val DRAWABLE_START = 0
                val DRAWABLE_END = 2
                if (event != null) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (binding.edtSearch.compoundPaddingEnd != null) {
                            if (event.x >= (binding.edtSearch.width - binding.edtSearch.compoundPaddingEnd)
                            ) {
                                binding.edtSearch.text?.clear()
                                return true
                            }
                        }

                        if (event.x <= (binding.edtSearch.compoundPaddingStart)) {
                            closeSearchActivity()
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

    private fun closeSearchActivity() {
        onBackPressed()
    }
}