package com.blocksdecoded.dex.core.shared

interface IAppPreferences {
    var isBackedUp: Boolean
    var isFingerprintEnabled: Boolean
    var isLightModeEnabled: Boolean
    var iUnderstand: Boolean
    var blockTillDate: Long?
    var failedAttempts: Int?
    var lockoutUptime: Long?
    var baseEthereumProvider: String?

    fun clear()
}