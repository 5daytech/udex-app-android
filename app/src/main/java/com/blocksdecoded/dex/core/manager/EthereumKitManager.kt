package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.model.AuthData
import com.blocksdecoded.dex.core.AppConfiguration
import io.horizontalsystems.ethereumkit.core.EthereumKit
import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger

class EthereumKitManager(
    val configuration: AppConfiguration = AppConfiguration.DEFAULT
) : IEthereumKitManager {
    override val gasProvider: ContractGasProvider = object : ContractGasProvider {
        override fun getGasLimit(contractFunc: String?): BigInteger = 400_000.toBigInteger()
        override fun getGasLimit(): BigInteger = 400_000.toBigInteger()
        override fun getGasPrice(contractFunc: String?): BigInteger = 5_000_000_000L.toBigInteger()
        override fun getGasPrice(): BigInteger = 5_000_000_000L.toBigInteger()
    }

    override var kit: EthereumKit? = null
    private var useCount = 0

    override fun ethereumKit(authData: AuthData): EthereumKit {
        useCount += 1

        kit?.let { return it }

        val syncMode = EthereumKit.SyncMode.ApiSyncMode()

        kit = EthereumKit.getInstance(
            App.instance,
            authData.privateKey,
            syncMode,
            configuration.networkType,
            configuration.infuraCredentials,
            configuration.etherscanKey,
            authData.walletId
        )

        startKit()

        return kit!!
    }

    private fun startKit() {
        kit?.start()
    }

    override fun refresh() {
        kit?.refresh()
    }

    override fun unlink() {
        useCount -= 1

        if (useCount < 1) {
            kit?.stop()
            kit = null
        }
    }
}