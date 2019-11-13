package com.fridaytech.dex.presentation.backup

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.App
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent
import com.fridaytech.dex.data.manager.clipboard.ClipboardManager

class BackupViewModel : CoreViewModel() {

    private val authManager = App.authManager
    private val appPreferences = App.appPreferences

    val words = MutableLiveData<List<String>>()

    val successEvent = SingleLiveEvent<Int>()
    val finishEvent = SingleLiveEvent<Int>()

    init {
        appPreferences.isBackedUp = true

        authManager.authData?.words?.let {
            words.value = it
        } ?: errorEvent.postValue(R.string.error_invalid_mnemonic)
    }

    fun onCopyClick() {
        val text = words.value?.joinToString(" ") ?: ""
        ClipboardManager.copyText(text)
        successEvent.postValue(R.string.message_copied)
    }
}
