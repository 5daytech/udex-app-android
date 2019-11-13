package com.fridaytech.dex.data.storage

import com.fridaytech.dex.core.model.EnabledCoin
import com.fridaytech.dex.core.model.Market
import com.fridaytech.dex.core.model.Rate
import io.reactivex.Flowable
import io.reactivex.Single

interface IMarketsStorage {
    fun getAllMarkets(): Single<List<Market>>

    fun getMarket(coinCode: String): Single<Market>

    fun save(vararg markets: Market)

    fun deleteAll()
}

interface IRatesStorage {
    fun getRate(coinCode: String, timeStamp: Long): Rate?

    fun getRateSingle(coinCode: String, timeStamp: Long): Single<Rate>

    fun getLatestRates(): Single<List<Rate>>

    fun saveLatest(rates: List<Rate>)

    fun save(vararg rates: Rate)

    fun deleteAll()
}

interface IEnabledCoinsStorage {
    fun enabledCoinsObservable(): Flowable<List<EnabledCoin>>

    fun save(coins: List<EnabledCoin>)

    fun deleteAll()
}
