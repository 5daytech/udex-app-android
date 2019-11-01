package com.blocksdecoded.dex.presentation.settings

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent

class SettingsViewModel : CoreViewModel() {

    private val appPreferences = App.appPreferences

    val selectedTheme = MutableLiveData<Int>()
    val isBackedUp = MutableLiveData<Boolean>()

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
