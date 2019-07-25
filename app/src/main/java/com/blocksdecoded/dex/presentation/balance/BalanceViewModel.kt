package com.blocksdecoded.dex.presentation.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.manager.IAdapterManager
import com.blocksdecoded.dex.core.model.CoinValue
import com.blocksdecoded.dex.ui.CoreViewModel

class BalanceViewModel : CoreViewModel() {
    private val adaptersManager: IAdapterManager = App.adapterManager
    private val adapters: List<IAdapter>
        get() = adaptersManager.adapters

    private val mBalances = MutableLiveData<List<CoinValue>>()
    val balances: LiveData<List<CoinValue>> = mBalances

    private val mLoading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = mLoading

    init {
        mLoading.value = true

        adaptersManager.adaptersUpdatedSignal
                .subscribe { onRefreshAdapters() }
                .let { disposables.add(it) }
    }

    private fun onRefreshAdapters() {
        adapters.forEach { adapter ->
            adapter.stateUpdatedFlowable.subscribe {
                mLoading.postValue(false)
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
                    CoinValue(CoinManager.coins[index], baseAdapter.balance)
                }
        )
    }

    fun refresh() {
        adaptersManager.refresh()
    }

    fun onCoinClick(position: Int) {

    }

    fun onSendClick(position: Int) {

    }

    fun onReceiveClick(position: Int) {

    }
}
