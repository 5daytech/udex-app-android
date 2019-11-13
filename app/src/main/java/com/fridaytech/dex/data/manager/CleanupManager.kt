package com.fridaytech.dex.data.manager

import com.fridaytech.dex.core.shared.IAppPreferences
import com.fridaytech.dex.data.manager.auth.IAuthManager
import com.fridaytech.dex.data.security.IKeyStoreManager

class CleanupManager(
    private val authManager: IAuthManager,
    private val appPreferences: IAppPreferences,
    private val keyStoreManager: IKeyStoreManager
) : ICleanupManager {

    override fun logout() {
        cleanUserData()
        removeKey()
    }

    override fun cleanUserData() {
        appPreferences.clear()
        authManager.logout()
    }

    override fun removeKey() {
        keyStoreManager.removeKey()
    }
}
