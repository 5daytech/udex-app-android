package com.fridaytech.dex.presentation.guest

import com.fridaytech.dex.App
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent

class GuestViewModel : CoreViewModel() {

    private val authManager = App.authManager
    private val wordsManager = App.wordsManager

    val openBackupEvent = SingleLiveEvent<Unit>()
    val openRestoreEvent = SingleLiveEvent<Unit>()
    val finishEvent = SingleLiveEvent<Unit>()

    fun onRestoreClick() {
        openRestoreEvent.call()
    }

    fun onCreateClick() {
        val words = wordsManager.generateWords()
        authManager.login(words)
        openBackupEvent.call()
    }
}
