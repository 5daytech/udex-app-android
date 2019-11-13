package com.fridaytech.dex.presentation.balance

import com.fridaytech.dex.App
import com.fridaytech.dex.core.model.BalanceState
import com.fridaytech.dex.core.model.CoinBalance
import com.fridaytech.dex.core.model.EConvertType
import com.fridaytech.dex.data.adapter.AdapterState
import com.fridaytech.dex.data.adapter.IAdapter
import com.fridaytech.dex.data.manager.IAdapterManager
import com.fridaytech.dex.data.manager.ICoinManager
import com.fridaytech.dex.data.manager.rates.IRatesManager
import com.fridaytech.dex.data.manager.rates.RatesConverter
import com.fridaytech.dex.presentation.widgets.balance.TotalBalanceInfo
import com.fridaytech.dex.utils.normalizedDiv
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.math.BigDecimal

class BalanceLoader(
    private val coinManager: ICoinManager = App.coinManager,
    private val adaptersManager: IAdapterManager = App.adapterManager,
    private val ratesManager: IRatesManager = App.ratesManager,
    private val ratesConverter: RatesConverter = App.ratesConverter,
    private val disposables: CompositeDisposable
) {
    val baseCoinCode = "ETH"

    val balancesSyncSubject = PublishSubject.create<Unit>()
    var balances = listOf<CoinBalance>()

    var totalBalance = TotalBalanceInfo(
        coinManager.getCoin(baseCoinCode),
        BigDecimal.ZERO,
        BigDecimal.ZERO
    )

    val isAllSynced: Boolean
        get() {
            adapters.forEach {
                if (it.state !is AdapterState.Synced) {
                    return false
                }
            }

            return adapters.isNotEmpty()
        }

    private val balanceDisposable = CompositeDisposable()

    private val adapters: List<IAdapter>
        get() = adaptersManager.adapters

    init {
        ratesManager.getMarketsObservable()
            .subscribe { updateBalance() }
            .let { disposables.add(it) }

        adaptersManager.adaptersUpdatedSignal
            .subscribe { onRefreshAdapters() }
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
                updateBalance()
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
                ratesConverter.getCoinPrice(adapter.coin.code),
                matchAdapterState(adapter),
                when (adapter.coin.code) {
                    "ETH" -> EConvertType.WRAP
                    "WETH" -> EConvertType.UNWRAP
                    else -> EConvertType.NONE
                }
            )
            }

        updateTotalBalance()

        balancesSyncSubject.onNext(Unit)
    }

    private fun updateTotalBalance() {
        var totalFiat = BigDecimal.ZERO

        balances.forEach {
            totalFiat += it.fiatBalance
        }

        val balance = totalFiat.normalizedDiv(ratesConverter.getCoinPrice(baseCoinCode))

        totalBalance.balance = balance
        totalBalance.fiatBalance = totalFiat
    }

    private fun matchAdapterState(adapter: IAdapter): BalanceState {
        return when (adapter.state) {
            is AdapterState.Syncing -> {
                BalanceState.SYNCING
            }
            is AdapterState.Synced -> {
                BalanceState.SYNCED
            }
            is AdapterState.NotSynced -> {
                BalanceState.FAILED
            }
        }
    }
}
