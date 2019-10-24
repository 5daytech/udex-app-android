package com.blocksdecoded.dex.presentation.balance

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.manager.IAdapterManager
import com.blocksdecoded.dex.core.manager.ICoinManager
import com.blocksdecoded.dex.core.manager.rates.IRatesManager
import com.blocksdecoded.dex.core.manager.rates.RatesConverter
import com.blocksdecoded.dex.core.model.BalanceState
import com.blocksdecoded.dex.core.model.CoinBalance
import com.blocksdecoded.dex.core.model.EConvertType
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class BalanceLoader(
    private val coinManager: ICoinManager = App.coinManager,
    private val adaptersManager: IAdapterManager = App.adapterManager,
    private val ratesManager: IRatesManager = App.ratesManager,
    private val ratesConverter: RatesConverter = App.ratesConverter,
    private val disposables: CompositeDisposable
) {
    val balancesSyncSubject = PublishSubject.create<Unit>()
    var balances = listOf<CoinBalance>()

    private val balanceDisposable = CompositeDisposable()

    private val adapters: List<IAdapter>
        get() = adaptersManager.adapters

    init {
        ratesManager.ratesUpdateSubject
            .subscribe { updateBalance() }
            .let { disposables.add(it) }

        adaptersManager.adaptersUpdatedSignal
            .subscribe {
                onRefreshAdapters()
            }
            .let { disposables.add(it) }
    }

    fun refresh() {
        adaptersManager.refresh()
        ratesManager.refresh()
    }

    fun clear() {
        balanceDisposable.clear()
    }

    private fun onRefreshAdapters() {
        clear()
        adapters.forEach { adapter ->
            adapter.stateUpdatedFlowable.subscribe {

            }.let { balanceDisposable.add(it) }

            adapter.balanceUpdatedFlowable.subscribe {
                updateBalance()
            }.let { balanceDisposable.add(it) }
        }

        updateBalance()
    }

    private fun updateBalance() {
        balances = adapters.mapIndexed { index, adapter ->
                CoinBalance(
                    coinManager.coins[index],
                    adapter.balance,
                    ratesConverter.getCoinsPrice(adapter.coin.code, adapter.balance),
                    ratesConverter.getTokenPrice(adapter.coin.code),
                    BalanceState.SYNCING,
                    when(adapter.coin.code) {
                        "ETH" -> EConvertType.WRAP
                        "WETH" -> EConvertType.UNWRAP
                        else -> EConvertType.NONE
                    }
                )
            }

        balancesSyncSubject.onNext(Unit)
    }
}