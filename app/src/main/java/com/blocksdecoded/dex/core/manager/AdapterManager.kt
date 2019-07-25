package com.blocksdecoded.dex.core.manager

import android.os.Handler
import android.os.HandlerThread
import com.blocksdecoded.dex.core.adapter.AdapterFactory
import com.blocksdecoded.dex.core.adapter.IAdapter
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

        ethereumKitManager.ethereumKit?.refresh()
    }

    override fun initAdapters(wallets: List<Wallet>) {
        handler.post {

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
