package com.blocksdecoded.dex.core.manager.zrx

import com.blocksdecoded.dex.core.IAppConfiguration
import com.blocksdecoded.dex.core.UnauthorizedException
import com.blocksdecoded.dex.core.manager.auth.IAuthManager
import com.blocksdecoded.zrxkit.ZrxKit
import com.blocksdecoded.zrxkit.relayer.model.Relayer
import com.blocksdecoded.zrxkit.relayer.model.RelayerConfig
import java.math.BigInteger

class ZrxKitManager(
    private val appConfiguration: IAppConfiguration,
    private val authManager: IAuthManager
): IZrxKitManager {
    private val networkType = if (appConfiguration.testMode) ZrxKit.NetworkType.Ropsten else ZrxKit.NetworkType.MainNet
    
    val gasProvider: ZrxKit.GasInfoProvider = object : ZrxKit.GasInfoProvider() {
        override fun getGasLimit(contractFunc: String?): BigInteger = 250_000.toBigInteger()
        override fun getGasPrice(contractFunc: String?): BigInteger = 5_000_000_000L.toBigInteger()
    }

    private var kit: ZrxKit? = null

    private val relayers = listOf(
        Relayer(
            0,
            "BD Relayer",
            appConfiguration.allExchangePairs,
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
                appConfiguration.infuraCredentials.secretKey ?: "",
                networkType
            )
    
            return kit!!
        }
        
        throw UnauthorizedException()
    }
}