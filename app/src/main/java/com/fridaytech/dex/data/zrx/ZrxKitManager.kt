package com.fridaytech.dex.data.zrx

import com.fridaytech.dex.core.IAppConfiguration
import com.fridaytech.dex.core.UnauthorizedException
import com.fridaytech.dex.data.adapter.FeeRatePriority
import com.fridaytech.dex.data.manager.auth.IAuthManager
import com.fridaytech.dex.data.manager.fee.IFeeRateProvider
import com.fridaytech.zrxkit.ZrxKit
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
            val rpcProviderMode = ZrxKit.RpcProviderMode.Infura(
                appConfiguration.infuraCredentials.projectId,
                appConfiguration.infuraCredentials.secretKey ?: ""
            )

            kit = ZrxKit.getInstance(
                appConfiguration.relayers,
                auth.privateKey,
                rpcProviderMode,
                appConfiguration.zrxNetworkType,
                gasProvider
            )

            return kit!!
        }

        throw UnauthorizedException()
    }

    override fun unlink() {
        kit = null
    }
}
