package com.fridaytech.dex.presentation.transactions

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.App
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent
import com.fridaytech.dex.data.adapter.IAdapter
import com.fridaytech.dex.presentation.transactions.model.TransactionViewItem
import com.fridaytech.dex.presentation.transactions.model.TransactionsState
import com.fridaytech.dex.presentation.transactions.model.TransactionsState.*
import com.fridaytech.dex.presentation.widgets.balance.TotalBalanceInfo
import com.fridaytech.dex.utils.isValidIndex
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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

    val syncTransactions = SingleLiveEvent<List<Int>>()
    val finishEvent = SingleLiveEvent<Int>()
    val showTransactionInfoEvent =
        SingleLiveEvent<TransactionViewItem>()

    fun init(coinCode: String?) {
        isEmpty.value = false
        val adapter = adapterManager.adapters.firstOrNull { it.coin.code == coinCode }

        if (adapter == null) {
            errorEvent.postValue(R.string.error_invalid_coin)
            return
        } else {
            this.adapter = adapter
        }

        transactionsLoader = TransactionsLoader(
            adapter,
            ratesManager,
            disposables
        )
        adapter.refresh()

        coinName.value = adapter.coin.title
        balance.value = TotalBalanceInfo(
            adapter.coin,
            adapter.balance,
            ratesConverter.getCoinsPrice(adapter.coin.code, adapter.balance)
        )

        transactionsLoader.syncTransactions
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { syncTransactions.value = it }
            .let { disposables.add(it) }

        transactionsLoader.syncSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.transactions.value = transactionsLoader.transactionItems
                isEmpty.value = transactionsLoader.transactionItems.isEmpty()
            }.let { disposables.add(it) }

        transactionsLoader.syncState.subscribe {
            updateState(transactionsLoader.state)
        }.let { disposables.add(it) }

        updateState(transactionsLoader.state)

        transactionsLoader.loadNext(initial = true)
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
