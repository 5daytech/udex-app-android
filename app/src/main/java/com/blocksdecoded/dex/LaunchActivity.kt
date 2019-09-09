package com.blocksdecoded.dex

import android.content.Intent
import android.os.Bundle
import com.blocksdecoded.dex.presentation.main.MainActivity
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.guest.GuestActivity
import com.blocksdecoded.dex.presentation.pin.PinActivity

class LaunchActivity: CoreActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        redirect()
    }
    
    private fun redirect() {
        when {
            !App.authManager.isLoggedIn -> {
                GuestActivity.start(this)
                finish()
            }

            App.pinManager.isPinSet -> PinActivity.startForUnlock(this, REQUEST_CODE_UNLOCK_PIN)

            else -> startMain()
        }
    }

    private fun startMain() {
        App.authManager.safeLoad()
        MainActivity.start(this, false)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UNLOCK_PIN) {
            when (resultCode) {
                PinActivity.RESULT_OK -> startMain()
                PinActivity.RESULT_CANCELLED -> finish()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_UNLOCK_PIN = 1
    }
}