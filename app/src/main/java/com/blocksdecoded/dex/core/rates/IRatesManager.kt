package com.blocksdecoded.dex.core.rates

import com.blocksdecoded.dex.core.model.CoinRate
import io.reactivex.subjects.BehaviorSubject

interface IRatesManager {
    val ratesUpdateSubject: BehaviorSubject<Unit>

    fun getRates(symbols: List<String>): List<CoinRate>

    fun getRate(symbol: String): CoinRate
}