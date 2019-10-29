package com.blocksdecoded.dex.presentation.exchangehistory

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.data.manager.history.ExchangeRecord
import com.blocksdecoded.dex.utils.isValidIndex

class ExchangeHistoryViewModel : CoreViewModel() {
    private val exchangeHistoryManager = App.exchangeHistoryManager

    val trades = MutableLiveData<List<ExchangeRecord>>()
    val emptyTradesVisible = MutableLiveData<Boolean>()

    val openTransactionInfoEvent = SingleLiveEvent<String>()

    init {
        exchangeHistoryManager.syncSubject
            .subscribe { refreshTrades() }
            .let { disposables.add(it) }

        refreshTrades()
    }

    private fun refreshTrades() {
        trades.postValue(exchangeHistoryManager.exchangeHistory)
        emptyTradesVisible.postValue(exchangeHistoryManager.exchangeHistory.isEmpty())
    }

    fun onTransactionClick(position: Int) {
        if (trades.value.isValidIndex(position)) {
            trades.value?.get(position)?.hash?.let {
                openTransactionInfoEvent.value = it
            }
        }
    }
}
