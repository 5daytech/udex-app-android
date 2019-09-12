package com.blocksdecoded.dex.core

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.zrxkit.model.AssetItem
import io.horizontalsystems.ethereumkit.core.EthereumKit

interface IAppConfiguration {
    val testMode: Boolean
    val networkType: EthereumKit.NetworkType
    val etherscanKey: String
    val infuraCredentials: EthereumKit.InfuraCredentials
    val appShareUrl: String
    val transactionExploreBaseUrl: String
    val ipfsId: String
    val ipfsMainGateway: String
    val ipfsFallbackGateway: String
    val allCoins: List<Coin>
    val allExchangePairs: List<Pair<AssetItem, AssetItem>>
}