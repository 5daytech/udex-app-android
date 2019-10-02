package com.blocksdecoded.dex.core.manager.duration

import com.blocksdecoded.dex.core.manager.duration.ETransactionType.*
import com.blocksdecoded.dex.core.model.Coin

class ProcessingDurationProvider : IProcessingDurationProvider {

    private val millisInSecond = 1000L

    override fun getEstimatedDuration(
        coin: Coin,
        type: ETransactionType
    ): Long = when(type) {
        SEND -> 20
        CONVERT -> 20
        EXCHANGE -> 30
        CANCEL -> 20
        APPROVE -> 20
    } * millisInSecond
}