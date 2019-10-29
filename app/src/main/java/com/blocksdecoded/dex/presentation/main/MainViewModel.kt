package com.blocksdecoded.dex.presentation.main

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel

class MainViewModel : CoreViewModel() {

    private val appPreferences = App.appPreferences
    private val networkManager = App.networkStateManager

    val settingsNotificationsAmount = MutableLiveData<Int>()
    val isConnectionEnabled = MutableLiveData<Boolean>()

    init {
        refreshBackedUpState()
        isConnectionEnabled.postValue(networkManager.isConnected)

        App.networkStateManager.networkAvailabilitySubject
            .subscribe {
                isConnectionEnabled.postValue(networkManager.isConnected)
            }.let { disposables.add(it) }
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
