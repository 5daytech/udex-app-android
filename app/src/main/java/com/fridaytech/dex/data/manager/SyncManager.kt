package com.fridaytech.dex.data.manager

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class SyncManager(
    private val adapterManager: IAdapterManager,
    interval: Long = 30L
) : ISyncManager {
    
    private var syncObservable: Observable<Long> = Observable.interval(interval, TimeUnit.SECONDS)
    private var disposable: Disposable? = null

    override fun start() {
        disposable?.dispose()
        syncObservable.subscribe {
            adapterManager.refresh()
        }.let { disposable = it }
    }

    override fun stop() {
        disposable?.dispose()
    }
}