package com.blocksdecoded.dex.data.zrx

import com.blocksdecoded.dex.core.IAppConfiguration
import com.blocksdecoded.dex.core.UnauthorizedException
import com.blocksdecoded.dex.data.adapter.FeeRatePriority
import com.blocksdecoded.dex.data.manager.auth.IAuthManager
import com.blocksdecoded.dex.data.manager.fee.IFeeRateProvider
import com.blocksdecoded.zrxkit.ZrxKit
import java.math.BigInteger

class ZrxKitManager(
    private val appConfiguration: IAppConfiguration,
    private val authManager: IAuthManager,
    private val feeRateProvider: IFeeRateProvider
) : IZrxKitManager {
    val gasProvider: ZrxKit.GasInfoProvider = object : ZrxKit.GasInfoProvider() {
        override fun getGasLimit(contractFunc: String?): BigInteger = when (contractFunc) {
            "deposit" -> 100000.toBigInteger()
            "withdraw" -> 100000.toBigInteger()
            "approve" -> 80000.toBigInteger()
            else -> 500_000.toBigInteger()
        }

        override fun getGasPrice(contractFunc: String?): BigInteger =
            feeRateProvider.ethereumGasPrice(FeeRatePriority.MEDIUM).toBigInteger()
    }

    private var kit: ZrxKit? = null

    override fun zrxKit(): ZrxKit {
        kit?.let { return it }

        authManager.authData?.let { auth ->
            kit = ZrxKit.getInstance(
                appConfiguration.relayers,
                auth.privateKey,
                appConfiguration.infuraCredentials.secretKey ?: "",
                appConfiguration.zrxNetworkType,
                gasProvider
            )

            return kit!!
        }

        throw UnauthorizedException()
    }
}
