package com.fridaytech.dex.data.security

import android.app.Activity
import com.fridaytech.dex.data.manager.BackgroundManager
import com.fridaytech.dex.data.manager.system.ISystemInfoManager

class KeyStoreChangeListener(
    private val systemInfoManager: ISystemInfoManager,
    private val keyStoreManager: IKeyStoreManager
) : BackgroundManager.Listener {

    override fun willEnterForeground(activity: Activity) {
        when {
            systemInfoManager.isSystemLockOff -> {
            }
            keyStoreManager.isKeyInvalidated -> {
            }
            keyStoreManager.isUserNotAuthenticated -> {
            }
        }
    }

    override fun didEnterBackground() {}
}
