package com.blocksdecoded.dex.core.adapter

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.TransactionRecord
import io.reactivex.Flowable
import io.reactivex.Single
import java.math.BigDecimal

interface IAdapter {
    val coin: Coin
    val feeCoinCode: String?

    val decimal: Int
    val confirmationsThreshold: Int

    fun start()
    fun stop()
    fun refresh()

    val lastBlockHeight: Int?
    val lastBlockHeightUpdatedFlowable: Flowable<Unit>

    val state: AdapterState
    val stateUpdatedFlowable: Flowable<Unit>

    val balance: BigDecimal
    val balanceUpdatedFlowable: Flowable<Unit>

    fun getTransactions(from: Pair<String, Int>? = null, limit: Int): Single<List<TransactionRecord>>
    val transactionRecordsFlowable: Flowable<List<TransactionRecord>>

    fun send(address: String, value: BigDecimal, feePriority: FeeRatePriority): Single<Unit>

    fun availableBalance(address: String?, feePriority: FeeRatePriority): BigDecimal
    fun fee(value: BigDecimal, address: String?, feePriority: FeeRatePriority): BigDecimal
    @Throws
    fun validate(address: String)

    fun validate(amount: BigDecimal, address: String?, feePriority: FeeRatePriority): List<SendStateError>

    val receiveAddress: String

    val debugInfo: String
}

enum class FeeRatePriority(val value: Int) {
    LOWEST(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    HIGHEST(4);

    companion object {
        fun valueOf(value: Int): FeeRatePriority = values().find { it.value == value } ?: MEDIUM
    }
}

sealed class SendStateError {
    object InsufficientAmount : SendStateError()
    object InsufficientFeeBalance : SendStateError()
}