package com.blocksdecoded.dex.presentation.transactions

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.data.adapter.IAdapter
import com.blocksdecoded.dex.presentation.transactions.model.TransactionViewItem
import com.blocksdecoded.dex.presentation.transactions.model.TransactionsState
import com.blocksdecoded.dex.presentation.transactions.model.TransactionsState.*
import com.blocksdecoded.dex.presentation.widgets.balance.TotalBalanceInfo
import com.blocksdecoded.dex.utils.isValidIndex

class TransactionsViewModel : CoreViewModel() {
    private val adapterManager = App.adapterManager
    private val ratesConverter = App.ratesConverter
    private val ratesManager = App.ratesManager
    private lateinit var adapter: IAdapter
    private lateinit var transactionsLoader: TransactionsLoader

    val coinName = MutableLiveData<String?>()
    val balance = MutableLiveData<TotalBalanceInfo>()
    val isEmpty = MutableLiveData<Boolean>()
    val isSyncing = MutableLiveData<Boolean>()
    val error = MutableLiveData<Int>()
    val transactions = MutableLiveData<List<TransactionViewItem>>()

    val syncTransaction = SingleLiveEvent<Int>()
    val finishEvent = SingleLiveEvent<Int>()
    val showTransactionInfoEvent = SingleLiveEvent<TransactionViewItem>()

    fun init(coinCode: String?) {
        isEmpty.postValue(false)
        val adapter = adapterManager.adapters.firstOrNull { it.coin.code == coinCode }

        if (adapter == null) {
            errorEvent.postValue(R.string.error_invalid_coin)
            return
        } else {
            this.adapter = adapter
        }

        adapter.refresh()
        transactionsLoader = TransactionsLoader(adapter, ratesManager, disposables)

        coinName.value = adapter.coin.title
        balance.value = TotalBalanceInfo(
            adapter.coin,
            adapter.balance,
            ratesConverter.getCoinsPrice(adapter.coin.code, adapter.balance)
        )

        transactionsLoader.syncTransaction
            .subscribe { syncTransaction.postValue(it) }
            .let { disposables.add(it) }

        transactionsLoader.syncSubject
            .subscribe {
                isEmpty.postValue(transactionsLoader.transactionItems.isEmpty())
                this.transactions.postValue(transactionsLoader.transactionItems)
            }.let { disposables.add(it) }

        transactionsLoader.syncState.subscribe {
            updateState(transactionsLoader.state)
        }.let { disposables.add(it) }

        updateState(transactionsLoader.state)
    }

    private fun updateState(state: TransactionsState) {
        when (state) {
            SYNCED -> {
                error.postValue(0)
                isSyncing.postValue(false)
            }
            SYNCING -> {
                error.postValue(0)
                isSyncing.postValue(true)
            }
            FAILED -> {
                isSyncing.postValue(false)
            }
        }
    }

    fun onTransactionClick(position: Int) {
        if (transactions.value != null && transactions.value.isValidIndex(position)) {
            transactions.value?.get(position)?.let {
                showTransactionInfoEvent.postValue(it)
            }
        }
    }

    fun onBackClick() {
        finishEvent.call()
    }

    fun loadNext() {
        transactionsLoader.loadNext()
    }
}
