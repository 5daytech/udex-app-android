package com.blocksdecoded.dex.data.manager.system

import android.app.Activity
import android.app.KeyguardManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.BuildConfig

class SystemInfoManager : ISystemInfoManager {
    private val biometricManager = BiometricManager.from(App.instance)
    override val appVersion: String = BuildConfig.VERSION_NAME

    override val isSystemLockOff: Boolean
        get() {
            val keyguardManager = App.instance.getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager
            return !keyguardManager.isDeviceSecure
        }

    override val biometricAuthSupported: Boolean
        get() = biometricManager.canAuthenticate() == BIOMETRIC_SUCCESS
}