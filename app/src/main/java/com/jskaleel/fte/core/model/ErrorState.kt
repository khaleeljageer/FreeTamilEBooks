package com.jskaleel.fte.core.model

import com.jskaleel.fte.core.SmartDelayCancellationException

sealed interface ErrorState {
    val message: String
    var isDisplayed: Boolean

    data class LocalError(
        override val message: String,
        override var isDisplayed: Boolean,
    ) : ErrorState

    companion object {
        val none = LocalError("", true)
    }
}

fun String.toErrorState() = ErrorState.LocalError(
    message = this,
    isDisplayed = this == SmartDelayCancellationException.MESSAGE_IGNORE,
)

fun String.toLocalErrorState() = ErrorState.LocalError(
    message = this,
    isDisplayed = false,
)

inline fun ErrorState.consume(block: (ErrorState) -> Unit) {
    if (!isDisplayed) {
        isDisplayed = true
        block(this)
    }
}
