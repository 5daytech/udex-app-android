package com.blocksdecoded.dex.presentation.guest

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent

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