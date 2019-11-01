package com.blocksdecoded.dex.core.shared

interface IAppPreferences {
    var isBackedUp: Boolean
    var isFingerprintEnabled: Boolean
    var selectedTheme: Int
    var iUnderstand: Boolean
    var baseEthereumProvider: String?
    var selectedChartPeriod: String

    fun clear()
}
