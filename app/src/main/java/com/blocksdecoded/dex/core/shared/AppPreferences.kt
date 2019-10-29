package com.blocksdecoded.dex.core.shared

class AppPreferences(
    private val sharedStorage: ISharedStorage
) : IAppPreferences {

    private val IS_BACKED_UP = "is_backed_up"
    private val IS_LIGHT_MODE_ENABLED = "is_light_mode_enabled"
    private val IS_FINGERPRINT_ENABLED = "is_fingerprint_enabled"
    private val SELECTED_CHART_PERIOD = "selected_chart_period"

    override var isBackedUp: Boolean
        get() = sharedStorage.getPreference(IS_BACKED_UP, false)
        set(value) { sharedStorage.setPreference(IS_BACKED_UP, value) }

    override var isFingerprintEnabled: Boolean
        get() = sharedStorage.getPreference(IS_FINGERPRINT_ENABLED, false)
        set(value) { sharedStorage.setPreference(IS_FINGERPRINT_ENABLED, value) }

    override var isLightModeEnabled: Boolean
        get() = sharedStorage.getPreference(IS_LIGHT_MODE_ENABLED, false)
        set(value) { sharedStorage.setPreference(IS_LIGHT_MODE_ENABLED, value) }

    override var selectedChartPeriod: String
        get() = sharedStorage.getPreference(SELECTED_CHART_PERIOD, "")
        set(value) { sharedStorage.setPreference(SELECTED_CHART_PERIOD, value) }

    override var iUnderstand: Boolean
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override var baseEthereumProvider: String?
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun clear() {
        sharedStorage.clear()
    }
}
