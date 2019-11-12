package com.blocksdecoded.dex.presentation.transactions

import com.blocksdecoded.dex.core.model.TransactionRecord
import com.blocksdecoded.dex.data.adapter.AdapterState
import com.blocksdecoded.dex.data.adapter.IAdapter
import com.blocksdecoded.dex.data.manager.rates.IRatesManager
import com.blocksdecoded.dex.presentation.transactions.model.TransactionStatus
import com.blocksdecoded.dex.presentation.transactions.model.TransactionViewItem
import com.blocksdecoded.dex.presentation.transactions.model.TransactionsState
import com.blocksdecoded.dex.utils.normalizedMul
import com.blocksdecoded.dex.utils.rx.ioObserve
import com.blocksdecoded.dex.utils.rx.ioSubscribe
import io.reactivex.Observable
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
    private val pageLimit = 15

    private val transactions = ArrayList<TransactionRecord>()

    val transactionItems = arrayListOf<TransactionViewItem>()
    val syncSubject = PublishSubject.create<Unit>()
    val syncTransactions = PublishSubject.create<List<Int>>()

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

        val timestamps = hashSetOf(*transactions.map { it.timestamp }.toTypedArray())
        val ratesRequestPool = ArrayList<Observable<Pair<Long, BigDecimal>>>()

        timestamps.forEach { timestamp ->
            ratesRequestPool.add(
                ratesManager.getHistoricalRate(adapter.coin.code, timestamp)
                    .retry(2) {
                        it is NullPointerException
                    }
                    .map { rate -> timestamp to rate }
                    .toObservable()
            )
        }

        Observable.concatDelayError(ratesRequestPool).ioObserve()
            .subscribe({
                onTransactionRateLoaded(it)
            }, { }).let { disposables.add(it) }
    }

    @Synchronized
    private fun onTransactionRateLoaded(data: Pair<Long, BigDecimal>) {
        val indexes = ArrayList<Int>()
        transactionItems.forEachIndexed { index, transaction ->
            if (transaction.date?.time == data.first * 1000) {
                indexes.add(index)
            }
        }

        indexes.forEach { index ->
            transactionItems[index].fiatValue = transactionItems[index].coinValue.multiply(data.second)
            transactionItems[index].fiatFee = transactionItems[index].fee?.multiply(data.second)
            transactionItems[index].historicalRate = data.second
        }

        syncTransactions.onNext(indexes)
        syncSubject.onNext(Unit)
    }
}
