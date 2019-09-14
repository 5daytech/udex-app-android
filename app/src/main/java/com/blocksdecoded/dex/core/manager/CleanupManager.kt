package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.dex.core.manager.auth.IAuthManager
import com.blocksdecoded.dex.core.security.IKeyStoreManager
import com.blocksdecoded.dex.core.shared.IAppPreferences

class CleanupManager(
    private val authManager: IAuthManager,
    private val appPreferences: IAppPreferences,
    private val keyStoreManager: IKeyStoreManager
): ICleanupManager {

    override fun logout() {
        cleanUserData()
        removeKey()
    }

    override fun cleanUserData() {
        authManager.logout()
        appPreferences.clear()
    }

    override fun removeKey() {
        keyStoreManager.removeKey()
    }
}