package com.jskaleel.fte.utils

import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject


object RxBus {

    private val publisher = PublishSubject.create<Any>()

    fun subscribe(@NonNull action: (Any) -> Unit): Disposable {
        return publisher.subscribe(action)
    }

    fun publish(@NonNull message: Any) {
        publisher.onNext(message)
    }
}