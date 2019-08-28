package com.blocksdecoded.dex.core.rates

import com.blocksdecoded.dex.core.model.Rate
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

interface IRatesManager {
    val ratesUpdateSubject: BehaviorSubject<Unit>
    val ratesStateSubject: BehaviorSubject<RatesState>

    fun getRates(codes: List<String>): List<Rate>

    fun getRate(code: String): Rate

    fun refresh()

    fun stop()

    fun clear()
}

interface IRatesStorage {
    fun allRates(): Single<List<Rate>>
    fun rateSingle(symbol: String): Single<Rate>
    fun save(vararg rates: Rate)
    fun deleteAll()
}