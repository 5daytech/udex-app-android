package com.blocksdecoded.dex.core.rates.remote

import com.blocksdecoded.dex.core.rates.remote.model.RatesResponse
import io.reactivex.Single
import java.math.BigDecimal

interface IRatesApiClient {
    fun init(rateClientConfig: IRatesClientConfig)

    fun getRates(): Single<RatesResponse>

    fun getHistoricalRate(coinCode: String, timestamp: Long): Single<BigDecimal>
}