package com.blocksdecoded.dex.data.shared

interface IAppPreferences {
    var isBackedUp: Boolean
    var isFingerprintEnabled: Boolean
    var isLightModeEnabled: Boolean
    var iUnderstand: Boolean
    var baseEthereumProvider: String?
    var selectedChartPeriod: String

    fun clear()
}