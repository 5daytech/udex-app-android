package com.blocksdecoded.dex.core.ui

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class CoreViewModel: ViewModel() {
    protected val disposables = CompositeDisposable()

    val errorEvent = SingleLiveEvent<Int>()
    val messageEvent = SingleLiveEvent<Int>()

    protected fun onConnectionStateChanged() {

    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}