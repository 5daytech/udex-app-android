package com.blocksdecoded.dex.core.manager

import android.os.Handler
import android.os.HandlerThread
import com.blocksdecoded.dex.core.adapter.AdapterFactory
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.manager.auth.IAuthManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class AdapterManager(
    private val coinManager: ICoinManager,
    private val adapterFactory: AdapterFactory,
    private val ethereumKitManager: IEthereumKitManager,
    private val authManager: IAuthManager
) : IAdapterManager, HandlerThread("A") {

    private val handler: Handler
    private val disposables = CompositeDisposable()

    init {
        start()
        handler = Handler(looper)

        disposables.add(coinManager.coinsUpdatedSubject
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { initAdapters() }
        )

        disposables.add(authManager.authDataSubject
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
            val oldAdapters = adapters.toMutableList()

            adapters = if (authManager.authData == null) {
                listOf()
            } else {
                coinManager.coins.mapNotNull { coin ->
                    var adapter = adapters.find { it.coin == coin }
                    if (adapter == null) {
                        adapter = adapterFactory.adapterForCoin(coin, authManager.authData!!)
                        adapter.start()
                    }
                    adapter
                }
            }

            adaptersUpdatedSignal.onNext(Unit)

            oldAdapters.forEach { oldAdapter ->
                if (adapters.none { it.coin == oldAdapter.coin }) {
                    oldAdapter.stop()
                    adapterFactory.unlinkAdapter(oldAdapter)
                }
            }

            oldAdapters.clear()
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
