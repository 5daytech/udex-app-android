package com.blocksdecoded.dex.core.rates.bootstrap

import io.reactivex.Single

interface IBootstrapClient {
    fun getConfigs(): Single<BootstrapResponse>
}