package com.blocksdecoded.dex.core.manager.duration

import com.blocksdecoded.dex.core.manager.duration.ITransactionDurationProvider.ETransactionType.*
import com.blocksdecoded.dex.core.model.Coin

class TransactionDurationProvider : ITransactionDurationProvider {

    private val millisInSecond = 1000L

    override fun getEstimatedDuration(
        coin: Coin,
        type: ITransactionDurationProvider.ETransactionType
    ): Long = when(type) {
        SEND -> 15
        WRAP -> 15
        UNWRAP -> 15
        EXCHANGE -> 15
        CANCEL -> 15
        APPROVE -> 15
    } * millisInSecond
}