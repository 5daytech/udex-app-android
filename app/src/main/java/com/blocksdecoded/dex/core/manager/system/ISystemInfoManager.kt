package com.blocksdecoded.dex.core.manager.system

interface ISystemInfoManager {
    val appVersion: String
    val isSystemLockOff: Boolean
    val biometricAuthSupported: Boolean
}