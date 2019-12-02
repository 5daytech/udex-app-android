package com.fridaytech.dex.core

import com.fridaytech.dex.core.model.Coin
import com.fridaytech.zrxkit.ZrxKit
import com.fridaytech.zrxkit.model.AssetItem
import com.fridaytech.zrxkit.relayer.model.Relayer
import io.horizontalsystems.ethereumkit.core.EthereumKit

interface IAppConfiguration {
    val testMode: Boolean
    val networkType: EthereumKit.NetworkType
    val zrxNetworkType: ZrxKit.NetworkType
    val etherscanKey: String
    val infuraCredentials: EthereumKit.InfuraCredentials

    val merchantId: String

    val appShareUrl: String
    val companySiteUrl: String
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
