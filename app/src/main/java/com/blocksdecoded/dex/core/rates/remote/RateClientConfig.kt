package com.blocksdecoded.dex.core.rates.remote

import com.blocksdecoded.dex.core.shared.ISharedStorage

class RateClientConfig(
    private val sharedStorage: ISharedStorage
) : IRateClientConfig {
    override var ipfsUrl: String
        get() = sharedStorage.getPreference(PREF_LAST_IPFS_URL, "")
        set(value) = sharedStorage.setPreference(PREF_LAST_IPFS_URL, value)

    companion object {
        private const val PREF_LAST_IPFS_URL = "ipfs_url"
    }
}