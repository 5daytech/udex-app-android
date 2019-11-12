package com.blocksdecoded.dex.data.buycrypto

interface IBuyCryptoProvider {
    val isFeatureAvailable: Boolean

    fun getBuyUrl(coinCode: String, recipientAddress: String): String
}
