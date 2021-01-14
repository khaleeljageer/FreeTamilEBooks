package com.jskaleel.fte.data.remote.base

import com.jskaleel.fte.data.remote.ApiErrorHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException

abstract class UseCase<Type>(private val apiErrorHandle: ApiErrorHandle?) where Type : Any {

    abstract suspend fun run(): Type

    fun invoke(
        scope: CoroutineScope,
        onResult: (UseCaseResponse<Type>)
    ) {
        val backgroundJob = scope.async { run() }
        scope.launch {
            backgroundJob.await().let {
                try {
                    onResult.onSuccess(it)
                } catch (e: HttpException) {
                    onResult.onError(apiErrorHandle?.traceErrorException(e))
                }
            }
        }
    }
}