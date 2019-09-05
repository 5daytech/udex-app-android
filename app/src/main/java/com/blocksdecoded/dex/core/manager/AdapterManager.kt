package com.blocksdecoded.dex.core.manager

import android.os.Handler
import android.os.HandlerThread
import com.blocksdecoded.dex.core.adapter.AdapterFactory
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.manager.auth.AuthManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class AdapterManager(
    private val coinManager: ICoinManager,
    private val adapterFactory: AdapterFactory,
    private val ethereumKitManager: IEthereumKitManager,
    private val authManager: AuthManager
) : IAdapterManager, HandlerThread("A") {

    private val handler: Handler
    private val disposables = CompositeDisposable()

    init {
        start()
        handler = Handler(looper)
    
        disposables.add(authManager.authDataSignal
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { initAdapters() }
        )
    }

    override var adapters: List<IAdapter> = listOf()
    override val adaptersUpdatedSignal = BehaviorSubject.create<Unit>()

    override fun refresh() {
        handler.post {
            adapters.forEach { it.refresh() }
        }

        ethereumKitManager.refresh()
    }

    override fun initAdapters() {
        handler.post {
            authManager.authData?.let { auth ->
                adapters.forEach { it.stop() }
                
                adapters = coinManager.coins.map { adapterFactory.adapterForCoin(it, auth) }
    
                adapters.forEach { it.start() }
    
                adaptersUpdatedSignal.onNext(Unit)
            }
        }
    }

    override fun stopKits() {
        handler.post {
            adapters.forEach {
                it.stop()
                adapterFactory.unlinkAdapter(it)
            }
            adapters = listOf()
            adaptersUpdatedSignal.onNext(Unit)
        }
    }
}
