package com.blocksdecoded.dex.presentation.settings.security

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.backup.BackupIntroActivity
import com.blocksdecoded.dex.presentation.pin.PinActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import com.blocksdecoded.dex.presentation.widgets.dialogs.AlertDialogFragment
import kotlinx.android.synthetic.main.activity_security_center.*
import kotlinx.android.synthetic.main.activity_security_center.toolbar

class SecurityCenterActivity : CoreActivity() {

    private lateinit var viewModel: SecurityCenterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_center)

        toolbar.bind(MainToolbar.ToolbarState.BACK) { finish() }

        viewModel = ViewModelProviders.of(this).get(SecurityCenterViewModel::class.java)

        security_center_backup?.setOnClickListener {
            BackupIntroActivity.start(this)
        }

        viewModel.passcodeEnabled.observe(this, Observer { passcodeEnabled ->
            security_passcode_switch?.apply {
                isChecked = passcodeEnabled

                switchOnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
                    viewModel.onPasscodeSwitch(isChecked)
                }
            }
        })

        viewModel.passcodeOptionsEnabled.observe(this, Observer {
            security_fingerprint_switch?.isEnabled = it
            security_edit_passcode?.isEnabled = it

            val alpha = if (it) 1f else 0.25f

            security_fingerprint_switch?.alpha = alpha
            security_edit_passcode?.alpha = alpha
        })

        viewModel.fingerprintEnabled.observe(this, Observer {
            security_fingerprint_switch?.apply {
                isChecked = it

                switchOnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
                    viewModel.onFingerprintSwitch(isChecked)
                }
            }
        })

        viewModel.openEditPinEvent.observe(this, Observer {
            PinActivity.startForEditPin(this)
        })

        viewModel.openSetPinEvent.observe(this, Observer {
            PinActivity.startForSetPin(this, REQUEST_CODE_SET_PIN)
        })

        viewModel.openUnlockPinEvent.observe(this, Observer {
            PinActivity.startForUnlock(this, REQUEST_CODE_UNLOCK_PIN_TO_DISABLE_PIN, true)
        })

        viewModel.showNoEnrolledFingerprints.observe(this, Observer {
            AlertDialogFragment.newInstance(
                R.string.error_biometric_not_enabled,
                R.string.error_biometric_not_added_yet,
                R.string.ok
            ).show(supportFragmentManager, "fingerprint_not_enabled")
        })

        viewModel.isBackedUp.observe(this, Observer {
            security_center_backup?.setInfoBadgeVisible(!it)
        })

        security_passcode_switch?.setOnClickListener {
            security_passcode_switch?.toggleSwitch()
        }

        security_fingerprint_switch?.setOnClickListener {
            security_fingerprint_switch?.toggleSwitch()
        }

        security_edit_passcode?.setOnClickListener {
            viewModel.onEditPasscodeClick()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SET_PIN) {
            when (resultCode) {
                PinActivity.RESULT_OK -> viewModel.onPinUpdated()
                PinActivity.RESULT_CANCELLED -> viewModel.onPinUpdateCancel()
            }
        }

        if (requestCode == REQUEST_CODE_UNLOCK_PIN_TO_DISABLE_PIN) {
            when (resultCode) {
                PinActivity.RESULT_OK -> viewModel.onPinForDisableUnlocked()
                PinActivity.RESULT_CANCELLED -> viewModel.onPinUnlockCancel()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_SET_PIN = 1
        const val REQUEST_CODE_UNLOCK_PIN_TO_DISABLE_PIN = 2

        fun start(context: Context) {
            context.startActivity(Intent(context, SecurityCenterActivity::class.java))
        }
    }
}