package com.blocksdecoded.dex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blocksdecoded.dex.presentation.main.MainActivity
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.guest.GuestActivity
import com.blocksdecoded.dex.presentation.keystore.KeyStoreActivity
import com.blocksdecoded.dex.presentation.pin.PinActivity
import com.blocksdecoded.dex.utils.ui.ToastHelper

class LaunchActivity: CoreActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        redirect()
    }
    
    private fun redirect() {
        when {
            App.systemInfoManager.isSystemLockOff -> {
                KeyStoreActivity.startForSystemLockOff(this)
                ToastHelper.showInfoMessage("System lock is off")
            }

            App.keyStoreManager.isUserNotAuthenticated -> {
                KeyStoreActivity.startForUserAuthentication(this)
                ToastHelper.showInfoMessage("User is not authenticated")
            }

            App.keyStoreManager.isKeyInvalidated -> {
                KeyStoreActivity.startForInvalidKey(this)
                ToastHelper.showInfoMessage("Key is invalidated")
            }

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
        MainActivity.start(this)
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

        fun start(context: Context) {
            val intent = Intent(context, LaunchActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}