package com.blocksdecoded.dex.presentation.transactions

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.widgets.balance.TotalBalanceInfo

class TransactionsViewModel : CoreViewModel() {
    private val adapterManager = App.adapterManager
    private val ratesConverter = App.ratesConverter
    private val ratesManager = App.ratesManager
    private lateinit var adapter: IAdapter
    private lateinit var transactionsLoader: TransactionsLoader

    val coinName = MutableLiveData<String?>()
    val balance = MutableLiveData<TotalBalanceInfo>()
    val transactions = MutableLiveData<List<TransactionViewItem>>()
    val syncTransaction = SingleLiveEvent<Int>()

    val finishEvent = SingleLiveEvent<Int>()

    val showTransactionInfoEvent = SingleLiveEvent<TransactionViewItem>()

    fun init(coinCode: String?) {
        val adapter = adapterManager.adapters.firstOrNull { it.coin.code == coinCode }

        if (adapter == null) {
            errorEvent.postValue(R.string.error_invalid_coin)
            return
        } else {
            this.adapter = adapter
        }

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
                this.transactions.postValue(transactionsLoader.transactionItems)
            }.let { disposables.add(it) }
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

}
