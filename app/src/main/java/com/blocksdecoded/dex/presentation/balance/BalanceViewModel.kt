package com.blocksdecoded.dex.presentation.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.adapter.AdapterState
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.manager.IAdapterManager
import com.blocksdecoded.dex.core.model.CoinValue
import com.blocksdecoded.dex.core.model.EConvertType.*
import com.blocksdecoded.dex.core.rates.IRatesManager
import com.blocksdecoded.dex.core.rates.RatesConverter
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.convert.ConvertConfig
import com.blocksdecoded.dex.presentation.widgets.balance.TotalBalanceInfo
import java.math.BigDecimal

class BalanceViewModel : CoreViewModel() {
    private val baseCoinCode = "ETH"
    private val adaptersManager: IAdapterManager = App.adapterManager
    private val ratesManager: IRatesManager = App.ratesManager
    private val ratesConverter: RatesConverter = App.ratesConverter
    private val adapters: List<IAdapter>
        get() = adaptersManager.adapters

    private val mBalances = MutableLiveData<List<CoinValue>>()
    val balances: LiveData<List<CoinValue>> = mBalances

    val totalBalance = MutableLiveData<TotalBalanceInfo>()

    private val mRefreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> = mRefreshing

    val openSendDialog = SingleLiveEvent<String>()
    val openReceiveDialog = SingleLiveEvent<String>()
    val openTransactions = SingleLiveEvent<String>()
    val openConvertDialog = SingleLiveEvent<ConvertConfig>()

    init {
        mRefreshing.value = true

        ratesManager.ratesUpdateSubject
            .subscribe { onRefreshAdapters() }
            .let { disposables.add(it) }
        
        adaptersManager.adaptersUpdatedSignal
            .subscribe { onRefreshAdapters() }
            .let { disposables.add(it) }
    }

    //region Private

    private fun onRefreshAdapters() {
        adapters.forEach { adapter ->
            if (adapter.state == AdapterState.NotSynced) {
                mRefreshing.postValue(true)
            }
            adapter.stateUpdatedFlowable.subscribe {
                mRefreshing.postValue(false)
            }

            adapter.balanceUpdatedFlowable.subscribe {
                updateBalance()
            }
        }

        updateBalance()
    }

    private fun updateBalance() {
        mBalances.postValue(
            adapters.mapIndexed { index, adapter ->
                CoinValue(
                    CoinManager.coins[index],
                    adapter.balance,
                    ratesConverter.getCoinsPrice(adapter.coin.code, adapter.balance),
                    ratesConverter.getTokenPrice(adapter.coin.code),
                    when(index) {
                        0 -> WRAP
                        1 -> UNWRAP
                        else -> NONE
                    }
                )
            }
        )
        
        updateTotalBalance()
    }

    private fun updateTotalBalance() {
        var balance = BigDecimal.ZERO
        
        adapters.forEach {
            val priceInBase = ratesConverter.baseFrom(it.coin.code).toBigDecimal()
            val convertedBalance = it.balance.multiply(priceInBase)
            balance += convertedBalance
        }
        
        val fiatBalance = ratesConverter.getCoinsPrice(baseCoinCode, balance)

        totalBalance.postValue(
            TotalBalanceInfo(
                CoinManager.getCoin(baseCoinCode),
                balance,
                fiatBalance
            )
        )
    }

    //endregion

    //region Public

    fun refresh() {
        adaptersManager.refresh()
    }

    fun onSendClick(position: Int) {
        if (adapters.isValidIndex(position)) {
            openSendDialog.postValue(adapters[position].coin.code)
        }
    }

    fun onReceiveClick(position: Int) {
        if (adapters.isValidIndex(position)) {
            openReceiveDialog.postValue(adapters[position].coin.code)
        }
    }

    fun onConvertClick(position: Int) {
        if (mBalances.value.isValidIndex(position)) {
            val balance = mBalances.value?.get(position)
            balance?.let {
                //TODO: Refactor. P.s. pass only coin code
                openConvertDialog.postValue(ConvertConfig(
                    it.coin.code,
                    if (position == 0) ConvertConfig.ConvertType.WRAP else ConvertConfig.ConvertType.UNWRAP)
                )
            }
        }
    }

    fun onTransactionsClick(position: Int) {
        if (adapters.isValidIndex(position)) {
            openTransactions.postValue(adapters[position].coin.code)
        }
    }

    //endregion
}
