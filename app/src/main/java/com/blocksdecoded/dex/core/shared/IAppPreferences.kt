package com.blocksdecoded.dex.core.shared

interface IAppPreferences {
    var isBackedUp: Boolean
    var isFingerprintEnabled: Boolean
    var isLightModeEnabled: Boolean
    var iUnderstand: Boolean
    var baseEthereumProvider: String?
    var selectedChartPeriod: String

    fun clear()
}
