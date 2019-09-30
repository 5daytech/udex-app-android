package com.blocksdecoded.dex.core.manager.duration

import com.blocksdecoded.dex.core.manager.duration.ETransactionType.*
import com.blocksdecoded.dex.core.model.Coin

class TransactionDurationProvider : ITransactionDurationProvider {

    private val millisInSecond = 1000L

    override fun getEstimatedDuration(
        coin: Coin,
        type: ETransactionType
    ): Long = when(type) {
        SEND -> 20
        WRAP -> 20
        UNWRAP -> 20
        EXCHANGE -> 20
        CANCEL -> 20
        APPROVE -> 20
    } * millisInSecond
}