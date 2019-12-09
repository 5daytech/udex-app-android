package com.fridaytech.dex.presentation.balance

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.App
import com.fridaytech.dex.core.model.Coin
import com.fridaytech.dex.core.model.CoinBalance
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent
import com.fridaytech.dex.presentation.convert.model.ConvertConfig
import com.fridaytech.dex.presentation.widgets.balance.TotalBalanceInfo
import com.fridaytech.dex.utils.isValidIndex
import io.reactivex.android.schedulers.AndroidSchedulers
import java.math.BigDecimal

class BalanceViewModel : CoreViewModel() {
    private val buyCryptoProvider = App.buyCryptoProvider
    private var balanceLoader: BalanceLoader =
        BalanceLoader(
            App.coinManager,
            App.adapterManager,
            App.ratesManager,
            App.ratesConverter,
            disposables
        )
    private val syncManager = App.syncManager

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
    val openCoinRateStats = SingleLiveEvent<Coin>()
    val openCoinManager = SingleLiveEvent<Unit>()
    val openUrlEvent = SingleLiveEvent<String>()
    val showTestModeDialog = SingleLiveEvent<Unit>()

    init {
        totalBalanceVisible.value = true
        topUpVisible.value = false

        totalBalance.value = balanceLoader.totalBalance
        balances.value = balanceLoader.balances
        syncBalances()

        balanceLoader.balancesSyncSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { syncBalances() }
            .let { disposables.add(it) }

        syncManager.start()
    }

    override fun onCleared() {
        super.onCleared()
        balanceLoader.clear()
        syncManager.stop()
    }

    override fun onNetworkConnectionAvailable() {
        super.onNetworkConnectionAvailable()
        balanceLoader.refresh()
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

    fun onBuyCryptoClick() {
        if (buyCryptoProvider.isFeatureAvailable) {
            App.adapterManager.adapters.firstOrNull { it.coin.code == baseCoinCode }?.let {
                openUrlEvent.postValue(buyCryptoProvider.getBuyUrl(baseCoinCode, it.receiveAddress))
            }
        } else {
            showTestModeDialog.call()
        }
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
                openConvertDialog.postValue(
                    ConvertConfig(
                        it.coin.code,
                        it.convertType
                    )
                )
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

    fun onRateStatsClick(position: Int) {
        if (mBalances.isValidIndex(position)) {
            openCoinRateStats.postValue(mBalances[position].coin)
        }
    }

    fun onManageCoinsClick() {
        openCoinManager.call()
    }

    //endregion
}
