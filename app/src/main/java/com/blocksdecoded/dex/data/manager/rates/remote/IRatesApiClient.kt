package com.blocksdecoded.dex.data.manager.rates.remote

import com.blocksdecoded.dex.data.manager.rates.model.LatestRateData
import com.blocksdecoded.dex.data.manager.rates.model.RateStatData
import io.reactivex.Single
import java.math.BigDecimal

interface IRatesApiClient {
    fun getRateStats(coinCode: String): Single<RateStatData>

    fun getLatestRates(): Single<LatestRateData>

    fun getHistoricalRate(coinCode: String, timestamp: Long): Single<BigDecimal>
}
