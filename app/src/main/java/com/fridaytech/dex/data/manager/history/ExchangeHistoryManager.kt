package com.fridaytech.dex.data.manager.history

import com.fridaytech.dex.core.model.TransactionRecord
import com.fridaytech.dex.data.manager.IAdapterManager
import com.fridaytech.dex.utils.Logger
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

// TODO: Refactoring and optimization
class ExchangeHistoryManager(
    val adapterManager: IAdapterManager
) : IExchangeHistoryManager {

    private val disposables = CompositeDisposable()

    override val exchangeHistory: List<ExchangeRecord>
        get() = exchangeTransactions.values.toList().sortedByDescending { it.timestamp }

    override val syncSubject: BehaviorSubject<Unit> = BehaviorSubject.create()

    private val exchangeTransactions = HashMap<String, ExchangeRecord>()

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
            }, { Logger.e(it) }).let { disposables.add(it) }

            adapter.transactionRecordsFlowable.subscribe({
                refreshTransactionsPool(adapter.coin.code, it)
            }, { Logger.e(it) }).let { disposables.add(it) }
        }
    }

    private fun refreshTransactionsPool(coin: String, transactions: List<TransactionRecord>) {
        transactionsPool[coin]?.addAll(transactions)

        transactionsPool.forEach { coinTransactions ->
            coinTransactions.value.forEach { transaction ->
                val tradeTx = ArrayList<ExchangeRecordItem>()

                transactionsPool.forEach { otherCoinTxs ->
                    otherCoinTxs.value.filter {
                        it.blockHeight == transaction.blockHeight &&
                                it.transactionIndex == transaction.transactionIndex
                    }.let {
                        if (it.isNotEmpty()) {
                            it.forEach { tradeTx.add(
                                ExchangeRecordItem(
                                    otherCoinTxs.key,
                                    it
                                )
                            ) }
                        }
                    }
                }

                if (tradeTx.size > 1) {
                    var allItemsIdentical = true
                    tradeTx.forEach {
                        if (it.coinCode != tradeTx[0].coinCode) allItemsIdentical = false
                    }

                    if (!allItemsIdentical) {
                        if (exchangeTransactions[transaction.transactionHash] == null) {
                            exchangeTransactions[transaction.transactionHash] =
                                ExchangeRecord(
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

        syncSubject.onNext(Unit)
    }
}
