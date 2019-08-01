package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.zrxkit.ZrxKit
import com.blocksdecoded.zrxkit.relayer.model.Relayer
import com.blocksdecoded.zrxkit.relayer.model.RelayerConfig
import java.math.BigInteger

class ZrxKitManager(
    private val etherKit: EthereumKitManager
): IZrxKitManager {
    
    private val networkType = ZrxKit.NetworkType.Ropsten
    
    val gasProvider: ZrxKit.GasInfoProvider = object : ZrxKit.GasInfoProvider() {
        override fun getGasLimit(contractFunc: String?): BigInteger = 200_000.toBigInteger()
        override fun getGasPrice(contractFunc: String?): BigInteger = 5_000_000_000L.toBigInteger()
    }

    private var kit: ZrxKit? = null

    private val relayers = listOf(
        Relayer(
            0,
            "BD Relayer",
            listOf(
                ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("ZRX"))
                        to ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("WETH")),
                ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("PPA"))
                    to ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("WETH")),
                ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("TMKV2"))
                    to ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("WETH"))
            ),
            listOf("0x2e8da0868e46fc943766a98b8d92a0380b29ce2a"),
            networkType.exchangeAddress,
            RelayerConfig("http://relayer.ropsten.fridayte.ch", "", "v2")
        )
    )

    override fun zrxKit(): ZrxKit {
        kit?.let { return it }

        kit = ZrxKit.getInstance(
            relayers,
            etherKit.authData.privateKey,
            gasProvider,
            etherKit.configuration.infuraCredentials.secretKey ?: "",
            networkType
        )

        return kit!!
    }
}