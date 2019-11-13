package com.fridaytech.dex.core

import com.blocksdecoded.zrxkit.ZrxKit
import com.blocksdecoded.zrxkit.model.AssetItem
import com.blocksdecoded.zrxkit.relayer.model.Relayer
import com.fridaytech.dex.core.model.Coin
import io.horizontalsystems.ethereumkit.core.EthereumKit

interface IAppConfiguration {
    val testMode: Boolean
    val networkType: EthereumKit.NetworkType
    val zrxNetworkType: ZrxKit.NetworkType
    val etherscanKey: String
    val infuraCredentials: EthereumKit.InfuraCredentials

    val merchantId: String

    val appShareUrl: String
    val transactionExploreBaseUrl: String

    val ipfsId: String
    val ipfsMainGateway: String
    val ipfsFallbackGateway: String

    val allCoins: List<Coin>
    val defaultCoinCodes: List<String>
    val allExchangePairs: List<Pair<AssetItem, AssetItem>>
    val fixedCoinCodes: List<String>

    val relayers: List<Relayer>
}
