package com.fridaytech.dex.presentation.exchangehistory

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.App
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent
import com.fridaytech.dex.data.manager.history.ExchangeRecord
import com.fridaytech.dex.utils.isValidIndex

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
