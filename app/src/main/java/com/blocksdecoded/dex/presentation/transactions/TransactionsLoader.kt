package com.blocksdecoded.dex.presentation.transactions

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
import io.reactivex.BackpressureStrategy
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.math.BigDecimal
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
            val price = BigDecimal.ZERO
            val feeRate = transaction.fee?.normalizedMul(price)

            TransactionViewItem(
                coin = adapter.coin,
                transactionHash = transaction.transactionHash,
                coinValue = transaction.amount,
                fiatValue = transaction.amount.multiply(price),
                fee = transaction.fee,
                fiatFee = feeRate,
                historicalRate = price,
                from = transaction.from.firstOrNull()?.address,
                to = transaction.to.firstOrNull()?.address,
                incoming = transaction.to.firstOrNull()?.address == adapter.receiveAddress,
                date = Date(transaction.timestamp * 1000),
                status = TransactionStatus.Completed,
                innerIndex = transaction.interTransactionIndex
            )
        })

        syncSubject.onNext(Unit)

        val ratesRequestPool = ArrayList<Single<Pair<TransactionRecord, BigDecimal>>>()
        transactions.forEach { transaction ->
            ratesRequestPool.add(
                ratesManager.getHistoricalRate(adapter.coin.code, transaction.timestamp)
                    .map { rate -> transaction to rate }
            )
        }

        Single.concatArray(*ratesRequestPool.toTypedArray()).toObservable().toFlowable(BackpressureStrategy.BUFFER)
            .ioSubscribe(disposables, {
                val indexes = ArrayList<Int>()
                transactionItems.forEachIndexed { index, transaction ->
                    if (transaction.transactionHash == it.first.transactionHash &&
                        transaction.date?.time == it.first.timestamp * 1000 &&
                        transaction.innerIndex == it.first.interTransactionIndex) {
                        indexes.add(index)
                    }
                }

                indexes.forEach { index ->
                    transactionItems[index].fiatValue = it.first.amount.multiply(it.second)
                    transactionItems[index].fiatFee = it.first.fee?.multiply(it.second)
                    transactionItems[index].historicalRate = it.second
                    syncTransaction.onNext(index)
                }

                syncSubject.onNext(Unit)
            }, {
                Logger.e(it)
            })
    }
}
