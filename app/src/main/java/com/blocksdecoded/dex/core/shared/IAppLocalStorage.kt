package com.blocksdecoded.dex.core.shared

interface IAppLocalStorage {
    var currentLanguage: String?
    var isBackedUp: Boolean
    var isBiometricOn: Boolean
    var isLightModeOn: Boolean
    var iUnderstand: Boolean
    var baseCurrencyCode: String?
    var blockTillDate: Long?
    var failedAttempts: Int?
    var lockoutUptime: Long?
    var baseEthereumProvider: String?

    fun clear()
}