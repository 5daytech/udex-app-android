package com.blocksdecoded.dex.presentation.account

import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent

class AccountViewModel : CoreViewModel() {
 
	val openBackupEvent = SingleLiveEvent<Int>()
	
	fun onBackupClick() {
		openBackupEvent.call()
	}

	fun onLogoutClick() {

	}
	
}
