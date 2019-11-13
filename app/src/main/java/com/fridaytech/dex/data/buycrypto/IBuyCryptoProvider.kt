package com.fridaytech.dex.data.buycrypto

interface IBuyCryptoProvider {
    val isFeatureAvailable: Boolean

    fun getBuyUrl(coinCode: String, recipientAddress: String): String
}
