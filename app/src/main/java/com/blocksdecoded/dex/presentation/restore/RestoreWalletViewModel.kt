package com.blocksdecoded.dex.presentation.restore

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.manager.IWordsManager
import com.blocksdecoded.dex.core.ui.CoreViewModel
import io.horizontalsystems.hdwalletkit.Mnemonic

class RestoreWalletViewModel: CoreViewModel() {
    
    private val wordsManager: IWordsManager = App.wordsManager
    private val words = MutableList(12) { "" }

    val errorEvent = MutableLiveData<Int>()

    fun onRestoreClick() {
        try {
            wordsManager.validate(words)
            
        } catch (e: Mnemonic.MnemonicException) {
            errorEvent.value = R.string.error_invalid_mnemonic
        }
    }
    
    fun onWordChanged(position: Int, word: String) {
        words[position] = word
    }
}