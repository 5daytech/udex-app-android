package com.blocksdecoded.dex.data.manager.duration

import com.blocksdecoded.dex.core.model.Coin

interface IProcessingDurationProvider {

    fun getEstimatedDuration(coin: Coin, type: ETransactionType): Long
}

enum class ETransactionType {
    SEND,
    CONVERT,
    EXCHANGE,
    APPROVE,
    CANCEL
}
