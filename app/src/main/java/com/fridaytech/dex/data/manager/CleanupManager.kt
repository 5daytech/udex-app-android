package com.fridaytech.dex.data.manager

import com.fridaytech.dex.core.shared.IAppPreferences
import com.fridaytech.dex.data.manager.auth.IAuthManager
import com.fridaytech.dex.data.security.IKeyStoreManager
import com.fridaytech.dex.data.zrx.IZrxKitManager

class CleanupManager(
    private val authManager: IAuthManager,
    private val appPreferences: IAppPreferences,
    private val keyStoreManager: IKeyStoreManager,
    private val zrxKitManager: IZrxKitManager
) : ICleanupManager {

    override fun logout() {
        cleanUserData()
        removeKey()
        zrxKitManager.unlink()
    }

    override fun cleanUserData() {
        appPreferences.clear()
        authManager.logout()
    }

    override fun removeKey() {
        keyStoreManager.removeKey()
    }
}
