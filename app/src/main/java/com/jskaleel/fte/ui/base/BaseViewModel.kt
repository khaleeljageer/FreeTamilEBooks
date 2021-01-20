package com.jskaleel.fte.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

open class BaseViewModel : ViewModel() {

    protected val _mViewState = MutableLiveData<Boolean>()
    val viewState: LiveData<Boolean> get() = _mViewState

    protected val _mMessageData = MutableLiveData<String>()
    val messageData: LiveData<String> get() = _mMessageData

    protected val scope = CoroutineScope(
        Job() + Dispatchers.IO
    )
}