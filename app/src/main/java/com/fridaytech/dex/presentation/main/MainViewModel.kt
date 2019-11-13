package com.fridaytech.dex.presentation.main

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.App
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent

class MainViewModel : CoreViewModel() {

    private val appPreferences = App.appPreferences
    private val networkManager = App.networkStateManager

    val settingsNotificationsAmount = MutableLiveData<Int>()
    val isConnectionEnabled = MutableLiveData<Boolean>()

    val showGuideEvent = SingleLiveEvent<Unit>()

    init {
        refreshBackedUpState()
        isConnectionEnabled.postValue(networkManager.isConnected)

        networkManager.networkAvailabilitySubject
            .subscribe {
                isConnectionEnabled.postValue(networkManager.isConnected)
            }.let { disposables.add(it) }

        if (!appPreferences.isGuideShown) {
            appPreferences.isGuideShown = true
            showGuideEvent.call()
        }
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
