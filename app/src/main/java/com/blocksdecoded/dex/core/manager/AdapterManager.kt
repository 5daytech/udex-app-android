package com.blocksdecoded.dex.core.manager

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.blocksdecoded.dex.core.adapter.AdapterFactory
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.model.Coin
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.web3j.crypto.Wallet

class AdapterManager(
    private val adapterFactory: AdapterFactory,
    private val ethereumKitManager: IEthereumKitManager)
    : IAdapterManager, HandlerThread("A") {

    private val handler: Handler
    private val disposables = CompositeDisposable()

    init {
        start()
        handler = Handler(looper)
    }

    override var adapters: List<IAdapter> = listOf()
    override val adaptersUpdatedSignal = PublishSubject.create<Unit>()

    override fun refresh() {
        handler.post {
            adapters.forEach { it.refresh() }
        }

        ethereumKitManager.refresh()
    }

    override fun initAdapters(coins: List<Coin>) {
        handler.post {
            adapters.forEach { it.stop() }

            adapters = coins.map { adapterFactory.adapterForCoin(it) }

            adapters.forEach { it.start() }

            adaptersUpdatedSignal.onNext(Unit)
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
