package com.blocksdecoded.dex.core.tradehistory

import com.blocksdecoded.dex.core.manager.IAdapterManager
import com.blocksdecoded.dex.core.model.TransactionRecord
import com.blocksdecoded.dex.utils.Logger
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class TradeHistoryManager(
    val adapterManager: IAdapterManager
) : ITradeHistoryManager {

    private val disposables = CompositeDisposable()

    override val tradesHistory: List<TradeRecord>
        get() = tradeTransactions.values.toList().sortedByDescending { it.timestamp }

    override val tradesUpdateSubject: BehaviorSubject<Unit> = BehaviorSubject.create()

    private val tradeTransactions = HashMap<String, TradeRecord>()

    private val transactionsPool = HashMap<String, ArrayList<TransactionRecord>>()

    init {
        adapterManager.adaptersUpdatedSignal
            .subscribe { subscribeToAdapters() }
            .let { disposables.add(it) }
    }

    private fun subscribeToAdapters() {
        adapterManager.adapters.forEach { adapter ->
            transactionsPool[adapter.coin.code] = ArrayList()

            adapter.getTransactions(limit = 300).subscribe({
                refreshTransactionsPool(adapter.coin.code, it)
            }, {}).let { disposables.add(it) }

            adapter.transactionRecordsFlowable.subscribe {
                refreshTransactionsPool(adapter.coin.code, it)
            }
        }
    }

    private fun refreshTransactionsPool(coin: String, transactions: List<TransactionRecord>) {
        transactionsPool[coin]?.addAll(transactions)

        transactionsPool.forEach { coinTransactions ->
            coinTransactions.value.forEach { transaction ->
                val tradeTx = ArrayList<TradeRecordItem>()

                transactionsPool.forEach { otherCoinTxs ->
                    otherCoinTxs.value.filter {
                        it.blockHeight == transaction.blockHeight &&
                                it.transactionIndex == transaction.transactionIndex
                    }.let {
                        if (it.isNotEmpty()) {
                            it.forEach { tradeTx.add(TradeRecordItem(otherCoinTxs.key, it)) }
                        }
                    }
                }

                if (tradeTx.size > 1) {
                    var allItemsIdentical = true
                    tradeTx.forEach {
                        if (it.coinCode != tradeTx[0].coinCode) allItemsIdentical = false
                    }

                    if (!allItemsIdentical) {
                        if (tradeTransactions[transaction.transactionHash] == null) {
                            tradeTransactions[transaction.transactionHash] = TradeRecord(
                                transaction.transactionHash,
                                transaction.timestamp,
                                tradeTx,
                                listOf()
                            )
                        }
                    }
                }
            }
        }

        tradesUpdateSubject.onNext(Unit)
        tradeTransactions.forEach {
            Logger.d("Trade record ${it.key} - ${it.value}")
        }
    }
}