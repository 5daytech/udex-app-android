package com.blocksdecoded.dex.core.rates.remote

import com.blocksdecoded.dex.core.rates.remote.model.RatesResponse
import io.reactivex.Single

interface IRatesApiClient {
    fun init(rateClientConfig: IRatesClientConfig)

    fun getRates(): Single<RatesResponse>
}