package com.blocksdecoded.dex.presentation.backup

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.utils.clipboard.ClipboardManager

class BackupViewModel : CoreViewModel() {
	
	val authManager = App.authManager
	
	val words = MutableLiveData<List<String>>()
	
	val successEvent = SingleLiveEvent<Int>()
	val errorEvent = SingleLiveEvent<Int>()
	val finishEvent = SingleLiveEvent<Int>()
	
	init {
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