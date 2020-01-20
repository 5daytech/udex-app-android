package com.fridaytech.dex.data.buycrypto

import com.fridaytech.dex.core.IAppConfiguration

class BuyCryptoProvider(
    private val appConfiguration: IAppConfiguration
) : IBuyCryptoProvider {
    override val isFeatureAvailable: Boolean
        get() = !appConfiguration.testMode

    override fun getBuyUrl(coinCode: String, recipientAddress: String): String {
        return "https://udex.app/buy?type=debitcard-hosted&address=ethereum%3A$recipientAddress&currency=$coinCode"
    }
}
