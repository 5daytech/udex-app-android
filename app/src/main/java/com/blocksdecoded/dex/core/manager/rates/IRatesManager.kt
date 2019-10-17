package com.blocksdecoded.dex.core.manager.rates

import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.model.Rate
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

interface IRatesManager {
    val marketsUpdateSubject: BehaviorSubject<Unit>
    val marketsStateSubject: BehaviorSubject<MarketState>

    fun getMarkets(coinCodes: List<String>): List<Market>

    fun getMarket(coinCode: String): Market

    fun getRateSingle(coinCode: String, timeStamp: Long): Single<Rate>

    fun getLatestRate(coinCode: String): Single<Rate>

    fun getRate(coinCode: String, timeStamp: Long): Rate?

    fun refresh()

    fun stop()

    fun clear()
}