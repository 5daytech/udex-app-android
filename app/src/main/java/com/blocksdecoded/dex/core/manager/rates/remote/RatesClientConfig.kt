package com.blocksdecoded.dex.core.manager.rates.remote

import com.blocksdecoded.dex.core.AppConfiguration
import com.blocksdecoded.dex.core.shared.ISharedStorage

class RatesClientConfig(
    appConfiguration: AppConfiguration,
    private val sharedStorage: ISharedStorage
) : IRatesClientConfig {
    override var historicalIpfsConfig: String = "https://${appConfiguration.ipfsMainGateway}/ipns/${appConfiguration.ipfsId}/"

    override var ipfsUrl: String
        get() = sharedStorage.getPreference(PREF_LAST_IPFS_URL, "")
        set(value) = sharedStorage.setPreference(PREF_LAST_IPFS_URL, value)

    companion object {
        private const val PREF_LAST_IPFS_URL = "ipfs_url"
    }
}