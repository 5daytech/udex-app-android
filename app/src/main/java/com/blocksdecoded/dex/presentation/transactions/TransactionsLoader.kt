package com.blocksdecoded.dex.presentation.transactions

import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.model.TransactionRecord
import com.blocksdecoded.dex.data.adapter.AdapterState
import com.blocksdecoded.dex.data.adapter.IAdapter
import com.blocksdecoded.dex.data.manager.rates.IRatesManager
import com.blocksdecoded.dex.presentation.transactions.model.TransactionStatus
import com.blocksdecoded.dex.presentation.transactions.model.TransactionViewItem
import com.blocksdecoded.dex.presentation.transactions.model.TransactionsState
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.normalizedMul
import com.blocksdecoded.dex.utils.rx.ioSubscribe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.*
import kotlin.collections.ArrayList

class TransactionsLoader(
    private val adapter: IAdapter,
    private val ratesManager: IRatesManager,
    private val disposables: CompositeDisposable
) {
    private val pageLimit = 10

    private val transactions = ArrayList<TransactionRecord>()
    val transactionItems = arrayListOf<TransactionViewItem>()
    val syncSubject = PublishSubject.create<Unit>()
    val syncTransaction = PublishSubject.create<Int>()

    var state = TransactionsState.SYNCING
    val syncState = PublishSubject.create<Unit>()

    var allLoaded = false
    var loading: Boolean = false

    init {
        adapter.transactionRecordsFlowable.subscribe {
            allLoaded = false
            transactions.clear()
            transactionItems.clear()
            loadNext(initial = true)
        }?.let { disposables.add(it) }

        adapter.stateUpdatedFlowable.subscribe {
            updateState()
        }.let { disposables.add(it) }

        updateState()
        loadNext(true)
    }

    fun loadNext(initial: Boolean = false) {
        if (loading || allLoaded) return
        loading = true

        val from = transactions.lastOrNull()?.let {
            it.transactionHash to it.interTransactionIndex
        }

        adapter.getTransactions(from = from, limit = pageLimit)
            .ioSubscribe(disposables,
                {
                    allLoaded = it.isEmpty()
                    transactions.addAll(it)
                    loadMeta(it)
                },
                { loading = false }
            )
    }

    private fun updateState() {
        state = when (adapter.state) {
            is AdapterState.Syncing -> TransactionsState.SYNCING
            is AdapterState.Synced -> TransactionsState.SYNCED
            is AdapterState.NotSynced -> TransactionsState.FAILED
        }
        syncState.onNext(Unit)
    }

    private fun loadMeta(transactions: List<TransactionRecord>) {
        loading = false

        transactions.mapIndexedTo(transactionItems, { _, transaction ->
            val price = ratesManager.getHistoricalRate(adapter.coin.code, transaction.timestamp).cache()
            val feeRate = transaction.fee?.normalizedMul(price.blockingGet())

            TransactionViewItem(
                coin = adapter.coin,
                transactionHash = transaction.transactionHash,
                coinValue = transaction.amount,
                fiatValue = transaction.amount.multiply(price.blockingGet()),
                fee = transaction.fee,
                fiatFee = feeRate,
                historicalRate = price.blockingGet(),
                from = transaction.from.firstOrNull()?.address,
                to = transaction.to.firstOrNull()?.address,
                incoming = transaction.to.firstOrNull()?.address == adapter.receiveAddress,
                date = Date(transaction.timestamp * 1000),
                status = TransactionStatus.Completed
            )
        })

        syncSubject.onNext(Unit)

        val ratesRequestPool = ArrayList<Single<Pair<TransactionRecord, Rate>>>()
//        transactions.forEach { transaction ->
//            ratesRequestPool.add(
//                ratesManager.getHistoricalRate(adapter.coin.code, transaction.timestamp)
//                    .map { rate -> transaction to rate }
//            )
//        }

        Single.concatArray(*ratesRequestPool.toTypedArray())
            .ioSubscribe(disposables, {
                val index = transactionItems.indexOfFirst { transaction ->
                    transaction.transactionHash == it.first.transactionHash
                }

                if (index >= 0) {
                    transactionItems[index].fiatValue = it.first.amount.multiply(it.second.price)
                    transactionItems[index].historicalRate = it.second.price
                    syncTransaction.onNext(index)
                    syncSubject.onNext(Unit)
                }
            }, {
                Logger.e(it)
            })
    }
}
