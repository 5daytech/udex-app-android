package com.fridaytech.dex.presentation.exchangehistory

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.App
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent
import com.fridaytech.dex.data.manager.history.ExchangeRecord
import com.fridaytech.dex.utils.Logger
import com.fridaytech.dex.utils.isValidIndex
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ExchangeHistoryViewModel : CoreViewModel() {
    private val exchangeHistoryManager = App.exchangeHistoryManager

    val trades = MutableLiveData<List<ExchangeRecord>>()
    val emptyTradesVisible = MutableLiveData<Boolean>()

    val openTransactionInfoEvent = SingleLiveEvent<String>()

    init {
        exchangeHistoryManager.syncSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ refreshTrades() }, { Logger.e(it) })
            .let { disposables.add(it) }

        refreshTrades()
    }

    private fun refreshTrades() {
        trades.value = exchangeHistoryManager.exchangeHistory
        emptyTradesVisible.value = exchangeHistoryManager.exchangeHistory.isEmpty()
    }

    fun onTransactionClick(position: Int) {
        if (trades.value.isValidIndex(position)) {
            trades.value?.get(position)?.hash?.let {
                openTransactionInfoEvent.value = it
            }
        }
    }
}
