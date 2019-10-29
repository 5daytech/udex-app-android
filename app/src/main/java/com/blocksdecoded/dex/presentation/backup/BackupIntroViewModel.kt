package com.blocksdecoded.dex.presentation.backup

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent

class BackupIntroViewModel : CoreViewModel() {
    private val pinManager = App.pinManager

    val openBackupEvent = SingleLiveEvent<Unit>()
    val openUnlockEvent = SingleLiveEvent<Unit>()
    val finishEvent = SingleLiveEvent<Unit>()

    fun onShowClick() {
        if (pinManager.isPinSet) {
            openUnlockEvent.call()
        } else {
            openBackupEvent.call()
            finishEvent.call()
        }
    }

    fun onUnlocked() {
        openBackupEvent.call()
        finishEvent.call()
    }
}
