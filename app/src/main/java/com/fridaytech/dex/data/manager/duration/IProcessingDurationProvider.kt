package com.fridaytech.dex.data.manager.duration

import com.fridaytech.dex.core.model.Coin

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
