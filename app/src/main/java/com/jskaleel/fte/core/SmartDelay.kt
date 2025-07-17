package com.jskaleel.fte.core

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SmartDelay(
    private val coroutineScope: CoroutineScope,
    private var delayInMillis: Long,
) {
    private var job: Job? = null

    fun execute(callBack: suspend () -> Unit) {
        job?.cancel(SmartDelayCancellationException())
        job = coroutineScope.launch {
            delay(delayInMillis)
            callBack()
        }
    }

    fun cancel() {
        job?.cancel()
    }

    fun updateDelay(delayInMillis: Long) {
        this.delayInMillis = delayInMillis
    }
}

class SmartDelayCancellationException : CancellationException(MESSAGE_IGNORE) {
    companion object {
        const val MESSAGE_IGNORE = "Ignore"
    }
}
