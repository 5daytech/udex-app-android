package com.blocksdecoded.dex.presentation.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.manager.ICoinManager
import com.blocksdecoded.dex.core.manager.rates.RatesConverter
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinBalance
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
    private val ratesConverter: RatesConverter = App.ratesConverter

    private var balanceLoader: BalanceLoader = BalanceLoader(
        App.coinManager,
        App.adapterManager,
        App.ratesManager,
        App.ratesConverter,
        disposables
    )
    private val mBalances: List<CoinBalance>
        get() = balanceLoader.balances

    private val mRefreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> = mRefreshing

    val balances = MutableLiveData<List<CoinBalance>>()

    val totalBalance = MutableLiveData<TotalBalanceInfo>()
    val totalBalanceVisible = MutableLiveData<Boolean>()
    val topUpVisible = MutableLiveData<Boolean>()

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

        balanceLoader.balancesSyncSubject.subscribe {
            balances.postValue(balanceLoader.balances)
            mRefreshing.postValue(false)
            updateTotalBalance()
        }.let { disposables.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        balanceLoader.clear()
    }

    //region Private

    private fun updateTotalBalance() {
        var balance = BigDecimal.ZERO

        mBalances.forEach {
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
        balanceLoader.refresh()
    }

    fun onAddCoinsClick() {
        val baseCoinIndex = mBalances.indexOfFirst { it.coin.code == baseCoinCode }
        onReceiveClick(baseCoinIndex)
    }

    fun onSendClick(position: Int) {
        if (mBalances.isValidIndex(position)) {
            openSendDialog.postValue(mBalances[position].coin.code)
        }
    }

    fun onReceiveClick(position: Int) {
        if (mBalances.isValidIndex(position)) {
            openReceiveDialog.postValue(mBalances[position].coin.code)
        }
    }

    fun onConvertClick(position: Int) {
        if (mBalances.isValidIndex(position)) {
            val balance = mBalances.get(position)
            balance.let {
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
        if (mBalances.isValidIndex(position)) {
            openTransactions.postValue(mBalances[position].coin.code)
        }
    }

    fun onInfoClick(position: Int) {
        if (mBalances.isValidIndex(position)) {
            openCoinInfo.postValue(mBalances[position].coin)
        }
    }

    fun onManageCoinsClick() {
        openCoinManager.call()
    }

    //endregion
}
