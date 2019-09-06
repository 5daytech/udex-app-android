package com.blocksdecoded.dex.presentation.settings.security

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent

class SecurityCenterViewModel : CoreViewModel() {

    private val appPreferences = App.appPreferences
    private val pinManager = App.pinManager

    val passcodeEnabled = SingleLiveEvent<Boolean>()

    init {
        passcodeEnabled.value = pinManager.isPinSet
    }

    fun onPasscodeSwitch(isEnabled: Boolean) {

    }
}