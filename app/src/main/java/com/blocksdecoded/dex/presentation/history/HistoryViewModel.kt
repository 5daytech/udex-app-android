package com.blocksdecoded.dex.presentation.history

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.tradehistory.TradeRecord
import com.blocksdecoded.dex.core.ui.CoreViewModel

class HistoryViewModel : CoreViewModel() {
    private val tradeHistoryManager = App.tradeHistoryManager

    val trades = MutableLiveData<List<TradeRecord>>()
    val emptyTradesVisible = MutableLiveData<Boolean>()

    init {
        tradeHistoryManager.tradesUpdateSubject
            .subscribe { refreshTrades() }
            .let { disposables.add(it) }

        refreshTrades()
    }

    private fun refreshTrades() {
        trades.postValue(tradeHistoryManager.tradesHistory)
        emptyTradesVisible.postValue(tradeHistoryManager.tradesHistory.isEmpty())
    }
}