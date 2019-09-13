package com.blocksdecoded.dex.presentation.main

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel

class MainViewModel : CoreViewModel() {

    private val appPreferences = App.appPreferences

    val settingsNotificationsAmount = MutableLiveData<Int>()

    init {
        refreshBackedUpState()
    }

    private fun refreshBackedUpState() {
        var notifications = 0

        if (!appPreferences.isBackedUp) {
            notifications++
        }

        settingsNotificationsAmount.value = notifications
    }

    fun onResume() {
        refreshBackedUpState()
    }
}