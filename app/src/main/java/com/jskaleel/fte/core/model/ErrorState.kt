package com.jskaleel.fte.core.model

import com.jskaleel.fte.core.SmartDelayCancellationException
import com.remitbee.app.app.core.tools.SmartDelayCancellationException
import com.remitbee.app.app.core.util.emptyString

sealed interface ErrorState {
    val message: String
    var isDisplayed: Boolean

    data class LocalError(
        override val message: String,
        override var isDisplayed: Boolean,
    ) : ErrorState

    data class ApiError(
        override val message: String,
        override var isDisplayed: Boolean,
    ) : ErrorState

    companion object {
        val none = LocalError(emptyString(), true)
    }
}

fun Error.toErrorState() = when (this) {
    is ApiError -> ErrorState.ApiError(
        message = msg,
        isDisplayed = msg == SmartDelayCancellationException.MESSAGE_IGNORE,
    )

    is LocalError -> ErrorState.LocalError(
        message = msg,
        isDisplayed = msg == SmartDelayCancellationException.MESSAGE_IGNORE,
    )
}

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