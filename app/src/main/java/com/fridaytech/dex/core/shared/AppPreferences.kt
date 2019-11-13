package com.fridaytech.dex.core.shared

class AppPreferences(
    private val sharedStorage: ISharedStorage
) : IAppPreferences {

    private val IS_BACKED_UP = "is_backed_up"
    private val SELECTED_THEME = "selected_theme"
    private val IS_FINGERPRINT_ENABLED = "is_fingerprint_enabled"
    private val IS_GUIDE_SHOWN = "is_guide_shown"
    private val SELECTED_CHART_PERIOD = "selected_chart_period"

    override var isBackedUp: Boolean
        get() = sharedStorage.getPreference(IS_BACKED_UP, false)
        set(value) { sharedStorage.setPreference(IS_BACKED_UP, value) }

    override var isFingerprintEnabled: Boolean
        get() = sharedStorage.getPreference(IS_FINGERPRINT_ENABLED, false)
        set(value) { sharedStorage.setPreference(IS_FINGERPRINT_ENABLED, value) }

    override var selectedTheme: Int
        get() = sharedStorage.getPreference(SELECTED_THEME, 0)
        set(value) { sharedStorage.setPreference(SELECTED_THEME, value) }

    override var selectedChartPeriod: String
        get() = sharedStorage.getPreference(SELECTED_CHART_PERIOD, "")
        set(value) { sharedStorage.setPreference(SELECTED_CHART_PERIOD, value) }

    override var isGuideShown: Boolean
        get() = sharedStorage.getPreference(IS_GUIDE_SHOWN, false)
        set(value) { sharedStorage.setPreference(IS_GUIDE_SHOWN, value) }

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
