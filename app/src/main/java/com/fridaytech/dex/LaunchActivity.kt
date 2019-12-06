package com.fridaytech.dex

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fridaytech.dex.presentation.dialogs.AlertDialogFragment
import com.fridaytech.dex.presentation.guest.GuestActivity
import com.fridaytech.dex.presentation.keystore.KeyStoreActivity
import com.fridaytech.dex.presentation.main.MainActivity
import com.fridaytech.dex.presentation.pin.PinActivity
import com.fridaytech.dex.utils.Logger
import com.fridaytech.dex.utils.ui.ToastHelper
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class LaunchActivity : AppCompatActivity() {

    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG || !App.networkStateManager.isConnected) {
            redirect()
        } else {
            appUpdateManager = AppUpdateManagerFactory.create(this)
            checkForUpdates()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_UNLOCK_PIN -> {
                if (resultCode == PinActivity.RESULT_OK) {
                    startMain()
                } else {
                    finish()
                }
            }

            REQUEST_CODE_UPDATE -> {
                if (resultCode != Activity.RESULT_OK) {
                    checkForUpdates()
                }
            }
        }
    }

    private fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                requestUpdate(appUpdateInfo)
            } else {
                redirect()
            }
        }.addOnFailureListener {
            redirect()
        }
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            this,
            REQUEST_CODE_UPDATE
        )
    }

    private fun redirect() {
        when {
            App.systemInfoManager.isSystemLockOff -> {
                KeyStoreActivity.startForSystemLockOff(this)
                ToastHelper.showInfoMessage("System lock is off")
                finish()
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
            PinActivity.startForUnlock(this,
                REQUEST_CODE_UNLOCK_PIN
            )
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
            R.string.ok,
            cancelable = false) {
            App.cleanupManager.logout()
            startGuest()
        }.show(supportFragmentManager, "auth_decryption_failed")
    }

    private fun showSomethingWentWrong() {
    }

    companion object {
        private const val REQUEST_CODE_UNLOCK_PIN = 1
        private const val REQUEST_CODE_UPDATE = 147

        fun start(context: Context) {
            val intent = Intent(context, LaunchActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}
