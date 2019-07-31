package com.blocksdecoded.dex.presentation.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.manager.IAdapterManager
import com.blocksdecoded.dex.core.model.CoinValue
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent

class BalanceViewModel : CoreViewModel() {
    private val adaptersManager: IAdapterManager = App.adapterManager
    private val adapters: List<IAdapter>
        get() = adaptersManager.adapters

    private val mBalances = MutableLiveData<List<CoinValue>>()
    val balances: LiveData<List<CoinValue>> = mBalances

    private val mRefreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> = mRefreshing

    val openSendDialog = SingleLiveEvent<String>()
    val openReceiveDialog = SingleLiveEvent<String>()
    val openTransactions = SingleLiveEvent<String>()
    val openConvertDialog = SingleLiveEvent<String>()

    init {
        mRefreshing.value = true

        adaptersManager.adaptersUpdatedSignal
                .subscribe { onRefreshAdapters() }
                .let { disposables.add(it) }
    }

    private fun onRefreshAdapters() {
        adapters.forEach { adapter ->
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
                adapters.mapIndexed { index, baseAdapter ->
                    CoinValue(CoinManager.coins[index], baseAdapter.balance, index in 0..1)
                }
        )
    }

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

    }

    fun onTransactionsClick(position: Int) {
        if (adapters.isValidIndex(position)) {
            openTransactions.postValue(adapters[position].coin.code)
        }
    }
}
