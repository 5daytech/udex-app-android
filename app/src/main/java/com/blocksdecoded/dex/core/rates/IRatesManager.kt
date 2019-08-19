package com.blocksdecoded.dex.core.rates

import com.blocksdecoded.dex.core.model.CoinRate
import io.reactivex.subjects.BehaviorSubject

interface IRatesManager {
    val ratesUpdateSubject: BehaviorSubject<Unit>
    val ratesStateSubject: BehaviorSubject<RatesState>

    fun getRates(codes: List<String>): List<CoinRate>

    fun getRate(code: String): CoinRate

    fun refresh()

    fun stop()

    fun clear()
}