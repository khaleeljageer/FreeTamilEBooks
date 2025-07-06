package com.jskaleel.fte.ui.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

sealed class NavigationState<out T> {
    data class Navigate<T>(val data: T, var isConsumed: Boolean = false) : NavigationState<T>()
    data object DoNothing : NavigationState<Nothing>()
}

fun <T> mutableNavigationState() = mutableStateOf<NavigationState<T>>(NavigationState.DoNothing)

@SuppressLint("ComposableNaming")
@Composable
inline fun <T> NavigationState<T>.consume(crossinline block: (T) -> Unit) {
    LaunchedEffect(key1 = this) {
        when (this@consume) {
            is NavigationState.Navigate -> {
                if (!isConsumed) {
                    isConsumed = true
                    block(data)
                }
            }

            NavigationState.DoNothing -> {}
        }
    }
}

inline fun <reified T> navigate(data: T) = NavigationState.Navigate(data)

@Composable
fun InvokeOnce(
    block: () -> Unit
) {
    rememberSaveable {
        block()
        true
    }
}
