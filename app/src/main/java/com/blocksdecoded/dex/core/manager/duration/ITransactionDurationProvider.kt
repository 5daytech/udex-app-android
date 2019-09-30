package com.blocksdecoded.dex.core.manager.duration

import com.blocksdecoded.dex.core.model.Coin

interface ITransactionDurationProvider {

    fun getEstimatedDuration(coin: Coin, type: ETransactionType): Long

}

enum class ETransactionType {
    SEND,
    WRAP,
    UNWRAP,
    EXCHANGE,
    APPROVE,
    CANCEL
}