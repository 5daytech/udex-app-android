package com.blocksdecoded.dex.presentation.transactions

import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.model.TransactionRecord
import com.blocksdecoded.dex.core.rates.IRatesManager
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.ioSubscribe
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
    private val pageLimit = 200

    val transactionItems = arrayListOf<TransactionViewItem>()
    val syncSubject = PublishSubject.create<Unit>()
    val syncTransaction = PublishSubject.create<Int>()

    init {
        adapter.transactionRecordsFlowable.subscribe {
            loadNext(false)
        }?.let { disposables.add(it) }

        loadNext(true)
    }

    var loading: Boolean = false

    fun loadNext(initial: Boolean = false) {
        if (loading) return
        loading = true

        adapter.getTransactions(limit = pageLimit)
            .ioSubscribe(disposables,
                { loadMeta(it) },
                { loading = false }
            )
    }

    private fun loadMeta(transactions: List<TransactionRecord>) {
        loading = false

        transactions.mapIndexedTo(transactionItems, { index, transaction ->
            val histRate = ratesManager.getRate(adapter.coin.code, transaction.timestamp)

            TransactionViewItem(
                adapter.coin,
                transaction.transactionHash,
                transaction.amount,
                transaction.amount.multiply(histRate?.price ?: BigDecimal.ZERO),
                histRate?.price ?: BigDecimal.ZERO,
                transaction.from.firstOrNull()?.address,
                transaction.to.firstOrNull()?.address,
                transaction.to.firstOrNull()?.address == adapter.receiveAddress,
                Date(transaction.timestamp * 1000),
                TransactionStatus.Completed
            )
        })

        syncSubject.onNext(Unit)

        val ratesRequestPool = ArrayList<Single<Pair<TransactionRecord, Rate>>>()
        transactions.forEach { transaction ->
            ratesRequestPool.add(
                ratesManager.getRateSingle(adapter.coin.code, transaction.timestamp)
                    .map { rate -> transaction to rate }
            )
        }

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

    enum class TransactionsState {
        SYNCED,
        SYNCING,
        FAILED
    }
}