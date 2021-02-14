package com.jskaleel.fte.ui.feedback

import com.jskaleel.fte.data.remote.ApiService
import com.jskaleel.fte.ui.base.BaseViewModel
import com.jskaleel.fte.utils.PrintLog
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class FeedbackViewModel : BaseViewModel() {

    fun submitFeedback(name: String, email: String, comments: String) {
        _mViewState.postValue(false)
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://docs.google.com/forms/d/")
            .build().create(ApiService::class.java)
        scope.launch {
            val response = retrofit.sendFeedback("application/json", name, email, comments)
            PrintLog.info("Response : $response")
            if (response.code() == HttpURLConnection.HTTP_OK) {
                _mViewState.postValue(true)
            }
        }
    }
    
    // Cancel the job when the view model is destroyed
    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}