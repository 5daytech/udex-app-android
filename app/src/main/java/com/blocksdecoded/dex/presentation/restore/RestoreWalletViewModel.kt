package com.blocksdecoded.dex.presentation.restore

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.manager.AuthManager
import com.blocksdecoded.dex.core.manager.IWordsManager
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import io.horizontalsystems.hdwalletkit.Mnemonic

class RestoreWalletViewModel: CoreViewModel() {
    
	private val authManager: AuthManager = App.authManager
    private val wordsManager: IWordsManager = App.wordsManager
    private val words = MutableList(12) { "" }

    val errorEvent = SingleLiveEvent<Int>()
	val successEvent = SingleLiveEvent<Int>()
	val navigateToMain = SingleLiveEvent<Unit>()

    fun onRestoreClick() {
        try {
            wordsManager.validate(words)
	        successEvent.value = R.string.message_words_validated
	        authManager.login(words)
	        navigateToMain.call()
        } catch (e: Mnemonic.MnemonicException) {
            errorEvent.value = R.string.error_invalid_mnemonic
        }
    }
    
    fun onWordChanged(position: Int, word: String) {
        words[position] = word
    }
}