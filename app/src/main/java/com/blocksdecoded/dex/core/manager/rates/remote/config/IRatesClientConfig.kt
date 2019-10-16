package com.blocksdecoded.dex.core.manager.rates.remote.config

interface IRatesClientConfig {
    var ipfsUrl: String
    var historicalIpfsConfig: String
    val ipnsPath: String
}