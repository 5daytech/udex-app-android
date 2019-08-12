package com.blocksdecoded.dex.core.manager

import io.horizontalsystems.ethereumkit.core.EthereumKit.*
import io.horizontalsystems.ethereumkit.core.EthereumKit.NetworkType.*

class AppConfiguration(
    val testMode: Boolean = true,
    val networkType: NetworkType = if (testMode) Ropsten else MainNet,
    val etherscanKey: String = "GKNHXT22ED7PRVCKZATFZQD1YI7FK9AAYE",
    val infuraCredentials: InfuraCredentials = InfuraCredentials(
    "0c3f9e6a005b40c58235da423f58b198",
    "57b6615fb10b4749a54b29c2894a00df"
    )
) {
    companion object {
        val DEFAULT = AppConfiguration()
    }
}