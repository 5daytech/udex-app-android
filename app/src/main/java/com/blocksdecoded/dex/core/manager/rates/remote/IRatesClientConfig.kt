package com.blocksdecoded.dex.core.manager.rates.remote

interface IRatesClientConfig {
    var ipfsUrl: String
    var historicalIpfsConfig: String
    val ipnsPath: String
}