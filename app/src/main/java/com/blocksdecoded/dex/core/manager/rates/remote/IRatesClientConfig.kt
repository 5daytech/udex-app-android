package com.blocksdecoded.dex.core.manager.rates.remote

import okhttp3.HttpUrl

interface IRatesClientConfig {
    var ipfsUrl: String
    var historicalIpfsConfig: String

    val ipnsPath: String
        get() = HttpUrl.get(ipfsUrl).encodedPath()
}