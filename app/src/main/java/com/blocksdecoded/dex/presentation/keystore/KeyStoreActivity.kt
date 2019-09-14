package com.blocksdecoded.dex.presentation.keystore

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.LaunchActivity
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.dialogs.AlertDialogFragment
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_keystore.*

class KeyStoreActivity : CoreActivity() {

    lateinit var viewModel: KeyStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keystore)

        viewModel = ViewModelProviders.of(this).get(KeyStoreViewModel::class.java)
        viewModel.init(intent.getParcelableExtra(EXTRA_MODE))

        viewModel.title.observe(this, Observer {
            toolbar?.setTitle(it)
        })

        viewModel.promptUserAuthentication.observe(this, Observer {
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val intent: Intent? = keyguardManager.createConfirmDeviceCredentialIntent(
                getString(R.string.os_pin_confirm_title),
                getString(R.string.os_pin_prompt_description)
            )
            startActivityForResult(intent, REQUEST_CODE_AUTHENTICATION)
        })

        viewModel.showInvalidKeyWarning.observe(this, Observer {
            AlertDialogFragment.newInstance(
                R.string.message_keys_invalidated_title,
                R.string.message_keys_invalidated_description,
                R.string.ok,
                object : AlertDialogFragment.Listener {
                    override fun onConfirmClick() {
                        viewModel.onCloseInvalidKeyWarning()
                    }
                }).show(supportFragmentManager, "keys_invalidated_alert")
        })

        viewModel.showNoSystemLockWarning.observe(this, Observer {
            keystore_system_lock_off?.visible = true
        })

        viewModel.openLaunchScreen.observe(this, Observer {
            LaunchActivity.start(this)
            finish()
        })

        viewModel.closeApplication.observe(this, Observer {
            finishAffinity()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_AUTHENTICATION) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    viewModel.onAuthenticationSuccess()
                }
                Activity.RESULT_CANCELED -> {
                    viewModel.onAuthenticationCanceled()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_AUTHENTICATION = 1
        private const val EXTRA_MODE = "mode"

        fun startForSystemLockOff(context: Context) {
            start(context, ModeType.NO_SYSTEM_LOCK)
        }

        fun startForInvalidKey(context: Context) {
            start(context, ModeType.INVALID_KEY)
        }

        fun startForUserAuthentication(context: Context) {
            start(context, ModeType.USER_AUTHENTICATION)
        }

        fun start(context: Context, mode: ModeType) {
            val intent = Intent(context, KeyStoreActivity::class.java)
            intent.putExtra(EXTRA_MODE, mode as Parcelable)
            context.startActivity(intent)
        }

        @Parcelize
        enum class ModeType : Parcelable {
            NO_SYSTEM_LOCK,
            INVALID_KEY,
            USER_AUTHENTICATION
        }
    }
}