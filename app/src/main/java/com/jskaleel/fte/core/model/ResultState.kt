package com.jskaleel.fte.core.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class ResultState<out T> {
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val message: String, val code: Int? = null) : ResultState<Nothing>()
}

inline fun <I> ResultState<I>.onSuccess(action: (I) -> Unit): ResultState<I> {
    if (this is ResultState.Success) action(data)
    return this
}

inline fun <I> ResultState<I>.onError(action: (Int?, String?) -> Unit): ResultState<I> {
    if (this is ResultState.Error) action(code, message)
    return this
}

suspend fun <I, O> ResultState<I>.map(responseProvider: ResultMapper<I, O>): ResultState<O> {
    return withContext(Dispatchers.Default) {
        responseProvider.map(this@map)
    }
}

const val NO_INTERNET_ERROR_CODE = 1
const val UNKNOWN_ERROR_CODE = 2
