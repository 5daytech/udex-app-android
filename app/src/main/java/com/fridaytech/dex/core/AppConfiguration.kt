package com.fridaytech.dex.core

import com.fridaytech.dex.BuildConfig
import com.fridaytech.dex.R
import com.fridaytech.dex.core.model.Coin
import com.fridaytech.dex.core.model.CoinType
import com.fridaytech.zrxkit.ZrxKit
import com.fridaytech.zrxkit.model.AssetItem
import com.fridaytech.zrxkit.relayer.model.Relayer
import com.fridaytech.zrxkit.relayer.model.RelayerConfig
import io.horizontalsystems.ethereumkit.core.EthereumKit.InfuraCredentials
import io.horizontalsystems.ethereumkit.core.EthereumKit.NetworkType
import io.horizontalsystems.ethereumkit.core.EthereumKit.NetworkType.MainNet
import io.horizontalsystems.ethereumkit.core.EthereumKit.NetworkType.Ropsten
import java.util.*

class AppConfiguration(
    override val testMode: Boolean = BuildConfig.testMode
) : IAppConfiguration {

    override val networkType: NetworkType = if (testMode) Ropsten else MainNet
    override val zrxNetworkType: ZrxKit.NetworkType = if (testMode) ZrxKit.NetworkType.Ropsten else ZrxKit.NetworkType.MainNet

    override val etherscanKey: String = BuildConfig.ETHERSCAN_KEY
    override val infuraCredentials: InfuraCredentials = InfuraCredentials(
        BuildConfig.INFURA_PROJECT_ID,
        BuildConfig.INFURA_PROJECT_SECRET
    )

    override val appShareUrl = "https://github.com/5daytech/dex-app-android"
    override val companySiteUrl: String = "https://fridayte.ch/"
    override val merchantId: String = BuildConfig.MERCHANT_ID

    override val transactionExploreBaseUrl = if (testMode) "https://ropsten.etherscan.io/tx/" else "https://etherscan.io/tx/"
    override val ipfsId = "QmXTJZBMMRmBbPun6HFt3tmb3tfYF2usLPxFoacL7G5uMX"
    override val ipfsMainGateway = "ipfs-ext.horizontalsystems.xyz"
    override val ipfsFallbackGateway = "ipfs.io"

    override val defaultCoinCodes: List<String>
        get() = listOf("ETH", "WETH", "ZRX", "USDC")

    override val fixedCoinCodes = listOf("ETH", "WETH", "ZRX")

    //region Coins

    private val testCoins = listOf(
        Coin("Ethereum", "ETH", CoinType.Ethereum),
        Coin("Wrapped ETH", "WETH", CoinType.Erc20("0xc778417e063141139fce010982780140aa0cd5ab", 18), R.string.info_weth),
        Coin("0x", "ZRX", CoinType.Erc20("0xff67881f8d12f372d91baae9752eb3631ff0ed00", 18)),
        Coin("Wrapped Bitcoin", "WBTC", CoinType.Erc20("0x96639968b1da3438dbb618465bcb2bf7b25ee6ad", 18)),
        Coin("Dai", "DAI", CoinType.Erc20("0xd914796ec26edd3f9651393f9751e0f3c00dd027", 18)), // Its CHO
        Coin("ChainLink", "LINK", CoinType.Erc20("0x30845a385581ce1dc51d651ff74689d7f4415146", 18)), // Its TMKV2
        Coin("Tether USD", "USDT", CoinType.Erc20("0x6D00364318D008C3AEA08c097c25F5639AB5D2e6", 3)), // Its PPA
        Coin("Huobi", "HT", CoinType.Erc20("0x52E64BB7aEE0E5bdd3a1995E3b070e012277c0fd", 2)) // Its TMK
    )

    private val coins = listOf(
        Coin("Ethereum", "ETH", CoinType.Ethereum),
        Coin("Wrapped ETH", "WETH", CoinType.Erc20("0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2", 18), R.string.info_weth),
        Coin("0x", "ZRX", CoinType.Erc20("0xE41d2489571d322189246DaFA5ebDe1F4699F498", 18)),
        Coin("Dai", "DAI", CoinType.Erc20("0x89d24a6b4ccb1b6faa2625fe562bdd9a23260359", 18)),
        Coin("USD Coin", "USDC", CoinType.Erc20("0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48", 6)),
        Coin("Tether USD", "USDT", CoinType.Erc20("0xdAC17F958D2ee523a2206206994597C13D831ec7", 6)),
        Coin("ChainLink", "LINK", CoinType.Erc20("0x514910771af9ca656af840dff83e8264ecf986ca", 18)),
        Coin("OmiseGO", "OMG", CoinType.Erc20("0xd26114cd6EE289AccF82350c8d8487fedB8A0C07", 18)),
        Coin("Maker", "MKR", CoinType.Erc20("0x9f8f72aa9304c8b593d555f12ef6589cc3a579a2", 18)),
        Coin("FOAM Token", "FOAM", CoinType.Erc20("0x4946fcea7c692606e8908002e55a582af44ac121", 18)),
        Coin("Livepeer Token", "LPT", CoinType.Erc20("0x58b6a8a3302369daec383334672404ee733ab239", 18)),
        Coin("Basic Attention Token", "BAT", CoinType.Erc20("0x0D8775F648430679A709E98d2b0Cb6250d2887EF", 18))
    )

    override val allCoins: List<Coin> = if (testMode) testCoins else coins

    //endregion

    // region Exchange pairs

    private val exchangePairs = listOf(
        getExchangePair("USDC", "WETH"),
        getExchangePair("USDT", "WETH"),
        getExchangePair("ZRX", "WETH"),
        getExchangePair("BAT", "WETH"),
        getExchangePair("LPT", "WETH"),
        getExchangePair("LINK", "WETH"),
        getExchangePair("FOAM", "WETH"),
        getExchangePair("MKR", "WETH"),
        getExchangePair("OMG", "WETH"),
        getExchangePair("DAI", "WETH"),
        getExchangePair("USDC", "DAI")
    )

    private val testExchangePairs = listOf(
        getExchangePair("ZRX", "WETH"),
        getExchangePair("WBTC", "WETH"),
        getExchangePair("DAI", "WETH"),
        getExchangePair("USDT", "WETH"),
        getExchangePair("HT", "WETH"),
        getExchangePair("LINK", "WETH"),
        getExchangePair("ZRX", "WBTC"),
        getExchangePair("DAI", "WBTC"),
        getExchangePair("USDT", "WBTC"),
        getExchangePair("HT", "WBTC"),
        getExchangePair("LINK", "WBTC"),
        getExchangePair("LINK", "USDT")
    )

    override val allExchangePairs: List<Pair<AssetItem, AssetItem>> = if (testMode) testExchangePairs else exchangePairs

    //endregion

    //region Relayers

    private val testRelayers: List<Relayer> = listOf(
        Relayer(
            0,
            "Ropsten Friday Tech",
            allExchangePairs,
            listOf("0xA5004C8b2D64AD08A80d33Ad000820d63aa2cCC9".toLowerCase(Locale.US)),
            zrxNetworkType.exchangeAddress,
            RelayerConfig("https://relayer.ropsten.fridayte.ch", "", "v2")
        )
    )

    private val mainRelayers: List<Relayer> = listOf(
        Relayer(
            0,
            "Friday Tech",
            allExchangePairs,
            listOf("0xA5004C8b2D64AD08A80d33Ad000820d63aa2cCC9".toLowerCase(Locale.US)),
            zrxNetworkType.exchangeAddress,
            RelayerConfig("https://relayer.fridayte.ch", "", "v2")
        )
    )

    override val relayers: List<Relayer> = if (testMode) testRelayers else mainRelayers

    //endregion

    private fun addressForSymbol(symbol: String): String = ((allCoins.firstOrNull {
        when (it.type) {
            is CoinType.Erc20 -> it.code.equals(symbol, true)
            else -> false
        }
    } ?: allCoins[1]).type as? CoinType.Erc20)?.address ?: ""

    private fun getExchangePair(from: String, to: String) =
        ZrxKit.assetItemForAddress(addressForSymbol(from)) to ZrxKit.assetItemForAddress(addressForSymbol(to))

    companion object {
        val DEFAULT = AppConfiguration()
    }
}
