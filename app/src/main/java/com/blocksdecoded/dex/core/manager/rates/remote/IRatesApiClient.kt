package com.blocksdecoded.dex.core.manager.rates.remote

import com.blocksdecoded.dex.core.manager.rates.model.RateStatData
import com.blocksdecoded.dex.core.manager.rates.remote.config.IRatesClientConfig
import com.blocksdecoded.dex.core.manager.rates.remote.model.RatesResponse
import io.reactivex.Single
import java.math.BigDecimal

interface IRatesApiClient {
    fun init(rateClientConfig: IRatesClientConfig)

    fun getRateStats(coinCode: String): Single<RateStatData>

    fun getRates(): Single<RatesResponse>

    fun getHistoricalRate(coinCode: String, timestamp: Long): Single<BigDecimal>
}