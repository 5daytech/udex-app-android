package com.blocksdecoded.dex.presentation.transactions

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.model.TransactionRecord
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.widgets.balance.TotalBalanceInfo
import com.blocksdecoded.dex.utils.ioSubscribe
import java.math.BigDecimal
import java.util.*

class TransactionsViewModel : CoreViewModel() {
    private val adapterManager = App.adapterManager
    private val ratesConverter = App.ratesConverter
    private val ratesManager = App.ratesManager
    private lateinit var adapter: IAdapter

    val coinName = MutableLiveData<String?>()
    val balance = MutableLiveData<TotalBalanceInfo>()
    val transactions = MutableLiveData<List<TransactionViewItem>>()

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

        coinName.value = adapter.coin.title
        balance.value = TotalBalanceInfo(
            adapter.coin,
            adapter.balance,
            ratesConverter.getCoinsPrice(adapter.coin.code, adapter.balance)
        )

        adapter.getTransactions(limit = 200)
            .ioSubscribe(disposables,
                { updateTransactions(it) },
                {  }
            )

        adapter.transactionRecordsFlowable.subscribe {
            updateTransactions(it)
        }?.let { disposables.add(it) }
    }

    private fun updateTransactions(transactions: List<TransactionRecord>) {
        this.transactions.postValue(
            transactions.map {
                val transactionRate = ratesManager.getRate(adapter.coin.code)
                TransactionViewItem(
                    adapter.coin,
                    it.transactionHash,
                    it.amount,
                    BigDecimal.ZERO,
                    it.from.firstOrNull()?.address,
                    it.to.firstOrNull()?.address,
                    it.to.firstOrNull()?.address == adapter.receiveAddress,
                    Date(it.timestamp * 1000),
                    TransactionStatus.Completed,
                    transactionRate
                )
            }
        )
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
