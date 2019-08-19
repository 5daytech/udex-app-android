package com.blocksdecoded.dex.presentation.transactions

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.model.TransactionRecord
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.utils.uiObserver
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.widgets.balance.TotalBalanceInfo

class TransactionsViewModel : CoreViewModel() {
    private val adapterManager = App.adapterManager
    private lateinit var adapter: IAdapter

    val coinName = MutableLiveData<String?>()
    val balance = MutableLiveData<TotalBalanceInfo>()
    val transactions = MutableLiveData<List<TransactionRecord>>()

    val finishEvent = SingleLiveEvent<Int>()

    val showTransactionInfoEvent = SingleLiveEvent<TransactionRecord>()

    fun init(coinCode: String?) {
        val adapter = adapterManager.adapters.firstOrNull { it.coin.code == coinCode }

        if (adapter == null) {
            errorEvent.postValue(R.string.error_invalid_coin)
            return
        } else {
            this.adapter = adapter
        }

        coinName.value = adapter.coin.title
        balance.value = TotalBalanceInfo(adapter.coin, adapter.balance, 0.0)

        adapter.getTransactions(limit = 200)
            .uiObserver()
            .subscribe({
                updateTransactions(it)
            }, {

            }).let { disposables.add(it) }

        adapter.transactionRecordsFlowable.subscribe {
            updateTransactions(it)
        }?.let { disposables.add(it) }
    }

    private fun updateTransactions(transactions: List<TransactionRecord>) {
        this.transactions.value = transactions
    }

    fun onTransactionClick(position: Int) {
        if (transactions.value != null && transactions.value.isValidIndex(position)) {
            showTransactionInfoEvent.postValue(transactions.value?.get(position))
        }
    }

    fun onBackClick() {
        finishEvent.call()
    }

}
