package com.blocksdecoded.dex.core.rates.remote

import com.blocksdecoded.dex.core.rates.remote.model.RatesResponse
import io.reactivex.Single

interface IRateClient {
    fun init(rateClientConfig: IRateClientConfig)

    fun getRates(): Single<RatesResponse>
}