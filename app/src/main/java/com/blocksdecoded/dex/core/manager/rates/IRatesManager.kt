package com.blocksdecoded.dex.core.manager.rates

import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.model.Rate
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

interface IRatesManager {
    val ratesUpdateSubject: BehaviorSubject<Unit>
    val ratesStateSubject: BehaviorSubject<RatesSyncState>

    fun getMarkets(coinCodes: List<String>): List<Market>

    fun getRateSingle(coinCode: String, timeStamp: Long): Single<Rate>

    fun getRate(coinCode: String, timeStamp: Long): Rate?

    fun getLatestRateSingle(coinCode: String): Single<Rate>

    fun getLatestRate(coinCode: String): Rate?

    fun refresh()

    fun stop()

    fun clear()
}