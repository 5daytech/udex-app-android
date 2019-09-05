package com.blocksdecoded.dex.core.shared

interface IAppLocalStorage {
    var isBackedUp: Boolean
    var isBiometricOn: Boolean
    var isLightModeOn: Boolean
    var iUnderstand: Boolean
    var blockTillDate: Long?
    var failedAttempts: Int?
    var lockoutUptime: Long?
    var baseEthereumProvider: String?

    fun clear()
}