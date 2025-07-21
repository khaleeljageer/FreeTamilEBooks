package com.jskaleel.fte.ui.utils

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

@Immutable
class SnackBarController(
    private val host: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    companion object {
        val current
            @Composable
            @ReadOnlyComposable
            get() = LocalSnackBarController.current

        fun showMessage(
            message: String,
            duration: SnackbarDuration = SnackbarDuration.Short,
        ) {
            channel.trySend(
                SnackBarChannelMessage(
                    message = message,
                    duration = duration,
                )
            )
        }
    }

    fun showMessage(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) {
        scope.launch {
            host.currentSnackbarData?.dismiss()
            host.showSnackbar(
                message = message,
                duration = duration
            )
        }
    }
}

private val LocalSnackBarController = staticCompositionLocalOf {
    SnackBarController(
        host = SnackbarHostState(),
        scope = CoroutineScope(EmptyCoroutineContext)
    )
}

data class SnackBarChannelMessage(
    val message: String,
    val duration: SnackbarDuration = SnackbarDuration.Short,
)

private val channel = Channel<SnackBarChannelMessage>(capacity = Int.MAX_VALUE)

@Composable
fun SnackBarControllerProvider(content: @Composable (snackBarHost: SnackbarHostState) -> Unit) {
    val snackHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackController = remember(scope) { SnackBarController(snackHostState, scope) }

    DisposableEffect(snackController, scope) {
        val job = scope.launch {
            for (payload in channel) {
                snackController.showMessage(
                    message = payload.message,
                    duration = payload.duration,
                )
            }
        }

        onDispose {
            job.cancel()
        }
    }

    CompositionLocalProvider(LocalSnackBarController provides snackController) {
        content(
            snackHostState
        )
    }
}
