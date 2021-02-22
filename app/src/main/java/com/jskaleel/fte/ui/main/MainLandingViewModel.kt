package com.jskaleel.fte.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainLandingViewModel : ViewModel() {
    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: MutableLiveData<String> = _message

    fun showToast(message: String) {
        _message.value = message
    }
}