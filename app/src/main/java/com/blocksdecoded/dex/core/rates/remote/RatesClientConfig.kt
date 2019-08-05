package com.blocksdecoded.dex.core.rates.remote

import com.blocksdecoded.dex.core.shared.ISharedStorage

class RatesClientConfig(
    private val sharedStorage: ISharedStorage
) : IRatesClientConfig {
    override var ipfsUrl: String
        get() = sharedStorage.getPreference(PREF_LAST_IPFS_URL, "")
        set(value) = sharedStorage.setPreference(PREF_LAST_IPFS_URL, value)

    companion object {
        private const val PREF_LAST_IPFS_URL = "ipfs_url"
    }
}