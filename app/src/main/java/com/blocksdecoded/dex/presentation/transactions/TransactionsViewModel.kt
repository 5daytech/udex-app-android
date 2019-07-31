package com.blocksdecoded.dex.presentation.transactions

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.model.TransactionRecord
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.utils.observeUi
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.utils.Logger

class TransactionsViewModel : CoreViewModel() {
    private val adapterManager = App.adapterManager
    private lateinit var adapter: IAdapter

    val coinName = MutableLiveData<String?>()
    val transactions = MutableLiveData<List<TransactionRecord>>()

    val errorEvent = SingleLiveEvent<Int>()
    val messageEvent = SingleLiveEvent<Int>()

    val showTransactionInfoEvent = SingleLiveEvent<TransactionRecord>()

    fun init(coinCode: String?) {
        val adapter = adapterManager.adapters.firstOrNull { it.coin.code == coinCode }

        if (adapter == null) {
            errorEvent.postValue(R.string.error_invalid_coin)
        } else {
            this.adapter = adapter
        }

        coinName.value = adapter?.coin?.title

        adapter?.getTransactions(limit = 200)
                ?.observeUi()
                ?.subscribe({
                    updateTransactions(it)
                }, {

                })?.let { disposables.add(it) }

        adapter?.transactionRecordsFlowable?.subscribe {
            updateTransactions(it)
            Logger.d("update transactions")
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

}
