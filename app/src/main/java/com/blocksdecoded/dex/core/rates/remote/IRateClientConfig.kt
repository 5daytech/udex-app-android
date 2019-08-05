package com.blocksdecoded.dex.core.rates.remote

import okhttp3.HttpUrl

interface IRateClientConfig {
    var ipfsUrl: String

    val ipnsPath: String
        get() = HttpUrl.get(ipfsUrl).encodedPath()
}