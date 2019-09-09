package com.blocksdecoded.dex.core.security

import android.app.Activity
import com.blocksdecoded.dex.core.manager.BackgroundManager
import com.blocksdecoded.dex.core.manager.system.ISystemInfoManager

class KeyStoreChangeListener(private val systemInfoManager: ISystemInfoManager,
                             private val keyStoreManager: IKeyStoreManager) : BackgroundManager.Listener {

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