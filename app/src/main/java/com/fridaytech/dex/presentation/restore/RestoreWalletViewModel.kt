package com.fridaytech.dex.presentation.restore

import com.fridaytech.dex.App
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent
import io.horizontalsystems.hdwalletkit.Mnemonic

class RestoreWalletViewModel : CoreViewModel() {

    private val appPreferences = App.appPreferences
    private val authManager = App.authManager
    private val wordsManager = App.wordsManager

    val successEvent = SingleLiveEvent<Int>()
    val navigateToMain = SingleLiveEvent<Unit>()

    fun onRestoreClick(words: List<String>) {
        try {
            wordsManager.validate(words)
            successEvent.value = R.string.message_words_validated
            authManager.login(words)
            appPreferences.isBackedUp = true
            navigateToMain.call()
        } catch (e: Mnemonic.MnemonicException) {
            errorEvent.value = R.string.error_invalid_mnemonic
        }
    }
}
