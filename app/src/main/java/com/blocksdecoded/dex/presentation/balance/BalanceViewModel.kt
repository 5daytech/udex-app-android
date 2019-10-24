package com.blocksdecoded.dex.presentation.balance

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinBalance
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.convert.model.ConvertConfig
import com.blocksdecoded.dex.presentation.widgets.balance.TotalBalanceInfo
import com.blocksdecoded.dex.utils.isValidIndex
import java.math.BigDecimal

class BalanceViewModel : CoreViewModel() {
    private var balanceLoader: BalanceLoader = BalanceLoader(
        App.coinManager,
        App.adapterManager,
        App.ratesManager,
        App.ratesConverter,
        disposables
    )
    private val baseCoinCode: String
        get() = balanceLoader.baseCoinCode

    private val mBalances: List<CoinBalance>
        get() = balanceLoader.balances

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
        totalBalanceVisible.value = true
        topUpVisible.value = false

        totalBalance.value = balanceLoader.totalBalance

        syncBalances()

        balanceLoader.balancesSyncSubject.subscribe {
            syncBalances()
        }.let { disposables.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        balanceLoader.clear()
    }

    //region Private

    private fun syncBalances() {
        totalBalance.postValue(balanceLoader.totalBalance)
        balances.postValue(balanceLoader.balances)

        if (balanceLoader.isAllSynced) {
            val balance = balanceLoader.totalBalance.balance
            val isPositiveBalance = balance.stripTrailingZeros() > BigDecimal.ZERO
            topUpVisible.postValue(!isPositiveBalance)
            totalBalanceVisible.postValue(isPositiveBalance)
        }
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
            val balance = mBalances[position]
            balance.let {
                openConvertDialog.postValue(ConvertConfig(it.coin.code, it.convertType))
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
