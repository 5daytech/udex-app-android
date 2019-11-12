package com.blocksdecoded.dex.data.buycrypto

import com.blocksdecoded.dex.core.IAppConfiguration

class BuyCryptoProvider(
    private val appConfiguration: IAppConfiguration
) : IBuyCryptoProvider {
    override val isFeatureAvailable: Boolean
        get() = !appConfiguration.testMode && appConfiguration.merchantId.isNotEmpty()

    override fun getBuyUrl(coinCode: String, recipientAddress: String): String {
        return "https://business.coindirect.com/buy?merchantId=${appConfiguration.merchantId}&to=$coinCode&address=$recipientAddress"
    }
}
