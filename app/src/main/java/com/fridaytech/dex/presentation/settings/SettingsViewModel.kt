package com.fridaytech.dex.presentation.settings

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.App
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent

class SettingsViewModel : CoreViewModel() {

    private val appPreferences = App.appPreferences
    private val appConfiguration = App.appConfiguration

    val selectedTheme = MutableLiveData<Int>()
    val isBackedUp = MutableLiveData<Boolean>()
    val companyUrl: String
        get() = appConfiguration.companySiteUrl

    val openSecurityCenterEvent = SingleLiveEvent<Unit>()
    val openAboutAppEvent = SingleLiveEvent<Unit>()
    val restartAppEvent = SingleLiveEvent<Unit>()

    init {
        selectedTheme.value = appPreferences.selectedTheme
        isBackedUp.value = appPreferences.isBackedUp
    }

    fun onSecurityCenterClick() {
        openSecurityCenterEvent.call()
    }

    fun onAboutAppClick() {
        openAboutAppEvent.call()
    }

    fun onResume() {
        isBackedUp.value = appPreferences.isBackedUp
    }

    fun onThemeChanged(theme: Int) {
        if (appPreferences.selectedTheme != theme) {
            appPreferences.selectedTheme = theme
            restartAppEvent.call()
        }
    }
}
