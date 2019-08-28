package com.blocksdecoded.dex.utils

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.uiObserver() : Single<T> = this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.ioSubscribe(
    disposables: CompositeDisposable? = null,
    onNext: (T) -> Unit,
    onError: ((Throwable) -> Unit)? = null
) {
    this.subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe({ onNext(it) },
            { onError?.invoke(it) }
        ).let { disposables?.add(it) }
}

fun <T> Flowable<T>.uiObserver() : Flowable<T> = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.ioSubscribe(
    disposables: CompositeDisposable,
    onNext: (T) -> Unit,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
) {
    observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
        .subscribe({ onNext(it) },
            { onError?.invoke(it) },
            { onComplete?.invoke() }
        ).let { disposables.add(it) }
}

fun <T> Flowable<T>.uiSubscribe(
    disposables: CompositeDisposable,
    onNext: (T) -> Unit,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
) {
    uiSubscribe(onNext, onError, onComplete).let { disposables.add(it) }
}

fun <T> Flowable<T>.uiSubscribe(
    onNext: (T) -> Unit,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
) : Disposable = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe({ onNext(it) },
        { onError?.invoke(it) },
        { onComplete?.invoke() }
    )