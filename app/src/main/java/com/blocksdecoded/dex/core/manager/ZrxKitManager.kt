package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.dex.core.UnauthorizedException
import com.blocksdecoded.zrxkit.ZrxKit
import com.blocksdecoded.zrxkit.relayer.model.Relayer
import com.blocksdecoded.zrxkit.relayer.model.RelayerConfig
import java.math.BigInteger

class ZrxKitManager(
    private val etherKit: EthereumKitManager,
    private val authManager: AuthManager
): IZrxKitManager {
    
    private val networkType = ZrxKit.NetworkType.Ropsten
    
    val gasProvider: ZrxKit.GasInfoProvider = object : ZrxKit.GasInfoProvider() {
        override fun getGasLimit(contractFunc: String?): BigInteger = 250_000.toBigInteger()
        override fun getGasPrice(contractFunc: String?): BigInteger = 5_000_000_000L.toBigInteger()
    }

    private var kit: ZrxKit? = null

    private val relayers = listOf(
        Relayer(
            0,
            "BD Relayer",
            listOf(
                ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("ZRX"))
                        to ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("WETH")), // ZRX/WETH
                ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("ZRX"))
                    to ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("TMKV2")), // ZRX/TMKV2
                ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("TMKV2"))
                    to ZrxKit.assetItemForAddress(CoinManager.addressForSymbol("WETH")) // TMKV2/WETH
            ),
            listOf("0x2e8da0868e46fc943766a98b8d92a0380b29ce2a"),
            networkType.exchangeAddress,
            RelayerConfig("http://relayer.ropsten.fridayte.ch", "", "v2")
        )
    )

    override fun zrxKit(): ZrxKit {
        kit?.let { return it }
    
        authManager.authData?.let { auth ->
            kit = ZrxKit.getInstance(
                relayers,
                auth.privateKey,
                gasProvider,
                etherKit.configuration.infuraCredentials.secretKey ?: "",
                networkType
            )
    
            return kit!!
        }
        
        throw UnauthorizedException()
    }
}