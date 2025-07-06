package com.jskaleel.fte.data.source.remote

import com.jskaleel.fte.core.model.ResultState
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class NetworkManager @Inject constructor() {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ResultState<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ResultState.Success(body)
                } else {
                    ResultState.Error("Empty response body", response.code())
                }
            } else {
                ResultState.Error(response.message(), response.code())
            }
        } catch (e: IOException) {
            ResultState.Error("Network error: ${e.localizedMessage}")
        } catch (e: HttpException) {
            ResultState.Error("HTTP exception: ${e.localizedMessage}", e.code())
        } catch (e: Exception) {
            ResultState.Error("Unknown error: ${e.localizedMessage}")
        }
    }
}
