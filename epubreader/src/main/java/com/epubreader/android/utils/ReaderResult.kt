package com.epubreader.android.utils

sealed class ReaderResult<out T> {
    data class Success<T>(val data: T) : ReaderResult<T>()
    data class Failure(val readerError: ReaderError) : ReaderResult<Nothing>()
}

inline fun <reified R, T> ReaderResult<T>.mapSuccess(transform: (T) -> R): ReaderResult<R> {
    return when (this) {
        is ReaderResult.Success -> {
            try {
                ReaderResult.Success(transform(data))
            } catch (e: ReaderError) {
                ReaderResult.Failure(e)
            } catch (e: Exception) {
                ReaderResult.Failure(LocalReaderError(e.message.toString()))
            }
        }

        is ReaderResult.Failure -> this
    }
}

inline fun <reified R, T> ReaderResult<T>.switchMapSuccess(transform: (T) -> ReaderResult<R>): ReaderResult<R> {
    return when (this) {
        is ReaderResult.Success -> transform(data)
        is ReaderResult.Failure -> this
    }
}

inline fun <T> ReaderResult<T>.mapFailure(transform: (ReaderError) -> ReaderError): ReaderResult<T> {
    return when (this) {
        is ReaderResult.Success -> this
        is ReaderResult.Failure -> ReaderResult.Failure(transform(readerError))
    }
}

inline fun <T> ReaderResult<T>.onSuccess(data: (T) -> Unit): ReaderResult<T> {
    if (this is ReaderResult.Success) {
        data(this.data)
    }
    return this
}

inline fun <T> ReaderResult<T>.onFailure(failure: (ReaderError) -> Unit): ReaderResult<T> {
    if (this is ReaderResult.Failure) {
        failure(this.readerError)
    }
    return this
}

fun <T> ReaderResult<T>.getOrNull(): T? {
    return when (this) {
        is ReaderResult.Success -> data
        is ReaderResult.Failure -> null
    }
}

fun <T> ReaderResult<T>.getOrThrow(): T {
    return when (this) {
        is ReaderResult.Success -> data
        is ReaderResult.Failure -> throw readerError
    }
}

inline fun <T> responseBuilder(block: () -> T): ReaderResult<T> {
    return try {
        ReaderResult.Success(block())
    } catch (e: ReaderError) {
        ReaderResult.Failure(e)
    }
}

fun <T> ReaderResult<T?>.toNonNullable(error: String): ReaderResult<T> {
    return when (this) {
        is ReaderResult.Success -> {
            if (this.data != null) ReaderResult.Success(this.data)
            else {
                val localError = LocalReaderError(error)
                ReaderResult.Failure(readerError = localError)
            }
        }

        is ReaderResult.Failure -> this
    }
}