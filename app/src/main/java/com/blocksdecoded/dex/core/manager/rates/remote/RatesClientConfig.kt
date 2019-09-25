package com.blocksdecoded.dex.core.manager.rates.remote

import com.blocksdecoded.dex.core.IAppConfiguration
import com.blocksdecoded.dex.core.shared.ISharedStorage
import okhttp3.HttpUrl

class RatesClientConfig(
    appConfiguration: IAppConfiguration,
    private val sharedStorage: ISharedStorage
) : IRatesClientConfig {
    override var historicalIpfsConfig: String = "https://${appConfiguration.ipfsMainGateway}/ipns/${appConfiguration.ipfsId}/"

    override var ipfsUrl: String
        get() = sharedStorage.getPreference(PREF_LAST_IPFS_URL, "")
        set(value) = sharedStorage.setPreference(PREF_LAST_IPFS_URL, value)

    override val ipnsPath: String
        get() = HttpUrl.get(ipfsUrl).encodedPath()

    companion object {
        private const val PREF_LAST_IPFS_URL = "ipfs_url"
    }
}