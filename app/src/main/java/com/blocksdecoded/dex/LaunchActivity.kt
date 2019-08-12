package com.blocksdecoded.dex

import android.os.Bundle
import com.blocksdecoded.dex.presentation.main.MainActivity
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.restore.RestoreWalletActivity

class LaunchActivity: CoreActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        redirect()
    }
    
    private fun redirect() {
        when {
            !App.authManager.isLoggedIn -> { RestoreWalletActivity.start(this) }
            
            else -> {
                App.authManager.safeLoad()
                MainActivity.start(this)
            }
        }
    
        finish()
    }

}