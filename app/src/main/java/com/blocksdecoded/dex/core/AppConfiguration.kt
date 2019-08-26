package com.blocksdecoded.dex.core

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinType
import com.blocksdecoded.zrxkit.ZrxKit
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

    fun addressForSymbol(symbol: String): String = ((testCoins.firstOrNull {
        when(it.type) {
            is CoinType.Erc20 -> it.code.equals(symbol, true)
            else -> false
        }
    } ?: testCoins[1]).type as? CoinType.Erc20)?.address ?: ""

    val testCoins = listOf(
        Coin("Ethereum", "ETH", CoinType.Ethereum),
        Coin("Wrapped ETH", "WETH", CoinType.Erc20("0xc778417e063141139fce010982780140aa0cd5ab", 18)),
        Coin("0x", "ZRX", CoinType.Erc20("0xff67881f8d12f372d91baae9752eb3631ff0ed00", 18)),
        Coin("Wrapped Bitcoin", "WBTC", CoinType.Erc20("0x96639968b1da3438dbb618465bcb2bf7b25ee6ad", 18)),
        Coin("Dai", "DAI", CoinType.Erc20("0xd914796ec26edd3f9651393f9751e0f3c00dd027", 18)), // Its CHO
        Coin("ChainLink", "LINK", CoinType.Erc20("0x30845a385581ce1dc51d651ff74689d7f4415146", 18)), // Its TMKV2
        Coin("Tether USD", "USDT", CoinType.Erc20("0x6D00364318D008C3AEA08c097c25F5639AB5D2e6", 3)), // Its PPA
        Coin("Huobi", "HT", CoinType.Erc20("0x52E64BB7aEE0E5bdd3a1995E3b070e012277c0fd", 2)) // Its TMK
    )

    val testExchangePairs = listOf(
        ZrxKit.assetItemForAddress(addressForSymbol("WBTC"))
                to ZrxKit.assetItemForAddress(addressForSymbol("WETH")), // WBTC/WETH
        ZrxKit.assetItemForAddress(addressForSymbol("ZRX"))
                to ZrxKit.assetItemForAddress(addressForSymbol("WETH")), // ZRX/WETH
        ZrxKit.assetItemForAddress(addressForSymbol("DAI"))
                to ZrxKit.assetItemForAddress(addressForSymbol("WETH")), // ZRX/WETH
        ZrxKit.assetItemForAddress(addressForSymbol("USDT"))
                to ZrxKit.assetItemForAddress(addressForSymbol("WETH")), // ZRX/WETH
        ZrxKit.assetItemForAddress(addressForSymbol("HT"))
                to ZrxKit.assetItemForAddress(addressForSymbol("WETH")), // ZRX/WETH
        ZrxKit.assetItemForAddress(addressForSymbol("LINK"))
                to ZrxKit.assetItemForAddress(addressForSymbol("WETH")) // TMKV2/WETH
    )
}