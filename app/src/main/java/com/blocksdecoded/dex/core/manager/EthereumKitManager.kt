package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.model.AuthData
import io.horizontalsystems.ethereumkit.core.EthereumKit
import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger

class EthereumKitManager(
    private val testMode: Boolean,
    val configuration: AppConfiguration = AppConfiguration.DEFAULT
) : IEthereumKitManager {

    override val gasProvider: ContractGasProvider = object : ContractGasProvider {
        override fun getGasLimit(contractFunc: String?): BigInteger = 400_000.toBigInteger()
        override fun getGasLimit(): BigInteger = 400_000.toBigInteger()
        override fun getGasPrice(contractFunc: String?): BigInteger = 5_000_000_000L.toBigInteger()
        override fun getGasPrice(): BigInteger = 5_000_000_000L.toBigInteger()
    }

    private val words: List<String>
        get() = "fancy pond surprise panic grocery hedgehog slight relief deal wash clog female".split(" ")

    val authData: AuthData
        get() = AuthData(words, walletId = "ether")

    override var ethereumKit: EthereumKit? = null
    private var useCount = 0

    private val infuraCredentials = configuration.infuraCredentials
    private val etherscanKey = configuration.etherscanKey

    override fun ethereumKit(authData: AuthData): EthereumKit {
        useCount += 1

        ethereumKit?.let { return it }

        val syncMode = EthereumKit.SyncMode.ApiSyncMode()

        ethereumKit = EthereumKit.getInstance(
            App.instance,
            authData.privateKey,
            syncMode,
            configuration.networkType,
            infuraCredentials,
            etherscanKey,
            authData.walletId
        )

        startKit()

        return ethereumKit!!
    }

    override fun defaultKit(): EthereumKit = ethereumKit(authData)

    private fun startKit() {
        ethereumKit?.start()
    }

    override fun refresh() {
        ethereumKit?.refresh()
    }

    override fun unlink() {
        useCount -= 1

        if (useCount < 1) {
            ethereumKit?.stop()
            ethereumKit = null
        }
    }
}