package com.blocksdecoded.dex.core.manager.rates.bootstrap

import io.reactivex.Single

interface IBootstrapClient {
    fun getConfigs(): Single<BootstrapResponse>
}