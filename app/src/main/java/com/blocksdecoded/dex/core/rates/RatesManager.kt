package com.blocksdecoded.dex.core.rates

import com.blocksdecoded.dex.core.model.CoinRate
import io.reactivex.subjects.BehaviorSubject

class RatesManager: IRatesManager {
    override val ratesUpdateSubject: BehaviorSubject<Unit> = BehaviorSubject.create()

    //TODO: Fetch cached rates
    override fun getRates(symbols: List<String>): List<CoinRate> {
        return listOf()
    }

    //TODO: Fetch coin rate
    override fun getRate(symbol: String): CoinRate {
        return CoinRate(symbol)
    }

}