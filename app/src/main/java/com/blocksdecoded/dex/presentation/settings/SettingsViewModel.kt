package com.blocksdecoded.dex.presentation.settings

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent

class SettingsViewModel : CoreViewModel() {

    private val appPreferences = App.appPreferences

    val lightMode = MutableLiveData<Boolean>()
    val isBackedUp = MutableLiveData<Boolean>()

    val openSecurityCenterEvent = SingleLiveEvent<Unit>()
    val openAboutAppEvent = SingleLiveEvent<Unit>()
    val restartAppEvent = SingleLiveEvent<Unit>()

    init {
        lightMode.value = appPreferences.selectedTheme == 1
        isBackedUp.value = appPreferences.isBackedUp
    }

    fun onSecurityCenterClick() {
        openSecurityCenterEvent.call()
    }

    fun onAboutAppClick() {
        openAboutAppEvent.call()
    }

    fun onLightModeSwitch(isLightModeOn: Boolean) {
        appPreferences.selectedTheme = if (isLightModeOn) 1 else 0
        restartAppEvent.call()
    }

    fun onResume() {
        isBackedUp.value = appPreferences.isBackedUp
    }
}
