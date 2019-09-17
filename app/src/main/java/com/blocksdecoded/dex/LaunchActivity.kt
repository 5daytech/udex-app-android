package com.blocksdecoded.dex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blocksdecoded.dex.presentation.main.MainActivity
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.dialogs.AlertDialogFragment
import com.blocksdecoded.dex.presentation.guest.GuestActivity
import com.blocksdecoded.dex.presentation.keystore.KeyStoreActivity
import com.blocksdecoded.dex.presentation.pin.PinActivity
import com.blocksdecoded.dex.utils.Logger
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

            !App.authManager.isLoggedIn -> startGuest()

            App.pinManager.isPinSet -> requestPin()

            else -> startMain(false)
        }
    }

    private fun requestPin() {
        try {
            App.pinManager.validate("123")
            PinActivity.startForUnlock(this, REQUEST_CODE_UNLOCK_PIN)
        } catch (e: Exception) {
            Logger.e(e)
            showAuthDataLoadFailed()
        }
    }

    private fun startMain(asNew: Boolean = true) {
        try {
            App.authManager.safeLoad()
            MainActivity.start(this, asNew)
            if (!asNew) {
                finish()
            }
        } catch (e: IllegalArgumentException) {
            Logger.e(e)
            showAuthDataLoadFailed()
        } catch (e: Exception) {
            showSomethingWentWrong()
        }
    }

    private fun startGuest() {
        GuestActivity.start(this)
        finish()
    }

    private fun showAuthDataLoadFailed() {
        AlertDialogFragment.newInstance(
            R.string.auth_load_error_title,
            R.string.auth_load_error_description,
            R.string.ok) {
            App.cleanupManager.logout()
            startGuest()
        }.apply {
            isCancelable = false
        }.show(supportFragmentManager, "auth_decryption_failed")
    }

    private fun showSomethingWentWrong() {

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