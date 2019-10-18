package com.blocksdecoded.dex.presentation.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.adapter.AdapterState
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.manager.IAdapterManager
import com.blocksdecoded.dex.core.manager.ICoinManager
import com.blocksdecoded.dex.core.manager.rates.IRatesManager
import com.blocksdecoded.dex.core.manager.rates.RatesConverter
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinBalance
import com.blocksdecoded.dex.core.model.EConvertType.*
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.convert.model.ConvertConfig
import com.blocksdecoded.dex.presentation.convert.model.ConvertType
import com.blocksdecoded.dex.presentation.widgets.balance.TotalBalanceInfo
import com.blocksdecoded.dex.utils.isValidIndex
import java.math.BigDecimal

class BalanceViewModel : CoreViewModel() {
    private val baseCoinCode = "ETH"
    private val coinManager: ICoinManager = App.coinManager
    private val adaptersManager: IAdapterManager = App.adapterManager
    private val ratesManager: IRatesManager = App.ratesManager
    private val ratesConverter: RatesConverter = App.ratesConverter
    private val adapters: List<IAdapter>
        get() = adaptersManager.adapters

    private val mBalances = MutableLiveData<List<CoinBalance>>()
    val balances: LiveData<List<CoinBalance>> = mBalances

    val totalBalance = MutableLiveData<TotalBalanceInfo>()
    val totalBalanceVisible = MutableLiveData<Boolean>()
    val topUpVisible = MutableLiveData<Boolean>()

    private val mRefreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> = mRefreshing

    val openSendDialog = SingleLiveEvent<String>()
    val openReceiveDialog = SingleLiveEvent<String>()
    val openTransactions = SingleLiveEvent<String>()
    val openConvertDialog = SingleLiveEvent<ConvertConfig>()
    val openCoinInfo = SingleLiveEvent<Coin>()
    val openCoinManager = SingleLiveEvent<Unit>()

    init {
        mRefreshing.value = true
        totalBalanceVisible.value = false
        topUpVisible.value = false

        ratesManager.ratesUpdateSubject
            .subscribe { updateBalance() }
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
                CoinBalance(
                    coinManager.coins[index],
                    adapter.balance,
                    ratesConverter.getCoinsPrice(adapter.coin.code, adapter.balance),
                    ratesConverter.getTokenPrice(adapter.coin.code),
                    when(adapter.coin.code) {
                        "ETH" -> WRAP
                        "WETH" -> UNWRAP
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
            val priceInBase = ratesConverter.baseFrom(it.coin.code)
            val convertedBalance = it.balance.multiply(priceInBase)
            balance += convertedBalance
        }
        
        val fiatBalance = ratesConverter.getCoinsPrice(baseCoinCode, balance)

        val isEmptyBalance = balance.stripTrailingZeros() <= BigDecimal.ZERO
        topUpVisible.postValue(isEmptyBalance)
        totalBalanceVisible.postValue(!isEmptyBalance)

        totalBalance.postValue(
            TotalBalanceInfo(
                coinManager.getCoin(baseCoinCode),
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

    fun onAddCoinsClick() {
        val baseCoinIndex = adapters.indexOfFirst { it.coin.code == baseCoinCode }
        onReceiveClick(baseCoinIndex)
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
                val type = when(it.coin.code) {
                    "ETH" -> ConvertType.WRAP
                    "WETH" -> ConvertType.UNWRAP
                    else -> ConvertType.WRAP
                }

                openConvertDialog.postValue(ConvertConfig(it.coin.code, type))
            }
        }
    }

    fun onTransactionsClick(position: Int) {
        if (adapters.isValidIndex(position)) {
            openTransactions.postValue(adapters[position].coin.code)
        }
    }

    fun onInfoClick(position: Int) {
        if (adapters.isValidIndex(position)) {
            openCoinInfo.postValue(adapters[position].coin)
        }
    }

    fun onManageCoinsClick() {
        openCoinManager.call()
    }

    //endregion
}
