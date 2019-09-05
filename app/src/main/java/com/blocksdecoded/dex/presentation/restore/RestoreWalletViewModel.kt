package com.blocksdecoded.dex.presentation.restore

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.manager.auth.AuthManager
import com.blocksdecoded.dex.core.manager.IWordsManager
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import io.horizontalsystems.hdwalletkit.Mnemonic

class RestoreWalletViewModel: CoreViewModel() {
    
	private val authManager: AuthManager = App.authManager
    private val wordsManager: IWordsManager = App.wordsManager
	
	val successEvent = SingleLiveEvent<Int>()
	val navigateToMain = SingleLiveEvent<Unit>()

    fun onRestoreClick(words: List<String>) {
        try {
            wordsManager.validate(words)
	        successEvent.value = R.string.message_words_validated
	        authManager.login(words)
	        navigateToMain.call()
        } catch (e: Mnemonic.MnemonicException) {
            errorEvent.value = R.string.error_invalid_mnemonic
        }
    }
}