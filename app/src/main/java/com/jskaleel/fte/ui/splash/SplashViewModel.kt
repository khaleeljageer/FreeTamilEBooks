package com.jskaleel.fte.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.data.entities.DownloadResult
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SplashViewModel() : ViewModel(), KoinComponent {

    private val _mViewState = MutableLiveData<DownloadResult>()
    val viewState: LiveData<DownloadResult> get() = _mViewState


    fun fetchBooks() {
        viewModelScope.launch {
            _mViewState.value = DownloadResult.Loading(true)


        }
    }
}