package com.blocksdecoded.dex.data.manager

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.model.AuthData
import com.blocksdecoded.dex.core.IAppConfiguration
import io.horizontalsystems.ethereumkit.core.EthereumKit

class EthereumKitManager(
    private val appConfiguration: IAppConfiguration
) : IEthereumKitManager {
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
            appConfiguration.networkType,
            appConfiguration.infuraCredentials,
            appConfiguration.etherscanKey,
            authData.walletId
        )

        kit?.start()

        return kit!!
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