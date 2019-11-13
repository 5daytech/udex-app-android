package com.fridaytech.dex.core.ui

import androidx.lifecycle.ViewModel
import com.fridaytech.dex.App
import io.reactivex.disposables.CompositeDisposable

abstract class CoreViewModel : ViewModel() {
    protected val disposables = CompositeDisposable()

    val errorEvent = SingleLiveEvent<Int>()
    val messageEvent = SingleLiveEvent<Int>()

    init {
        App.networkStateManager.networkAvailabilitySubject
            .subscribe {
                if (App.networkStateManager.isConnected) {
                    onNetworkConnectionAvailable()
                } else {
                    onNetworkConnectionLost()
                }
            }.let { disposables.add(it) }
    }

    protected open fun onNetworkConnectionLost() { }
    protected open fun onNetworkConnectionAvailable() { }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
