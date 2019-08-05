package com.blocksdecoded.dex.core.bootstrap

import io.reactivex.Single

interface IBootstrapClient {
    fun getConfigs(): Single<BootstrapResponse>
}