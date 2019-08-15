package com.blocksdecoded.dex.core.ui

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class CoreViewModel: ViewModel() {
    protected val disposables = CompositeDisposable()

    val errorEvent = SingleLiveEvent<Int>()
    val messageEvent = SingleLiveEvent<Int>()
    
    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}