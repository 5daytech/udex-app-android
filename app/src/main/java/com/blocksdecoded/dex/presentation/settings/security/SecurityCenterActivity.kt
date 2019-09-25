package com.blocksdecoded.dex.presentation.settings.security

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.LaunchActivity
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.backup.BackupIntroActivity
import com.blocksdecoded.dex.presentation.pin.PinActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import com.blocksdecoded.dex.presentation.dialogs.AlertDialogFragment
import com.blocksdecoded.dex.presentation.dialogs.ConfirmActionDialog
import kotlinx.android.synthetic.main.activity_security_center.*
import kotlinx.android.synthetic.main.activity_security_center.toolbar

class SecurityCenterActivity : CoreActivity(), ConfirmActionDialog.Listener {

    private lateinit var viewModel: SecurityCenterViewModel

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_center)

        initViewModel()

        initView()
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

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SecurityCenterViewModel::class.java)

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

        viewModel.showLogoutConfirm.observe(this, Observer {
            ConfirmActionDialog.show(
                this,
                R.string.logout_title,
                listOf(R.string.logout_confirm_wallet_remove, R.string.logout_confirm_loose_funds),
                this
            )
        })

        viewModel.openLaunchScreenEvent.observe(this, Observer {
            LaunchActivity.start(this)
        })
    }

    private fun initView() {
        toolbar.bind(MainToolbar.getBackAction { finish() })

        security_passcode_switch?.setOnClickListener {
            security_passcode_switch?.toggleSwitch()
        }

        security_fingerprint_switch?.setOnClickListener {
            security_fingerprint_switch?.toggleSwitch()
        }

        security_edit_passcode?.setOnClickListener {
            viewModel.onEditPasscodeClick()
        }

        security_center_backup?.setOnClickListener {
            BackupIntroActivity.start(this)
        }

        security_logout?.setOnClickListener {
            viewModel.onLogoutClick()
        }
    }

    //endregion

    override fun onConfirmationSuccess() {
        viewModel.onLogoutConfirm()
    }

    companion object {
        const val REQUEST_CODE_SET_PIN = 1
        const val REQUEST_CODE_UNLOCK_PIN_TO_DISABLE_PIN = 2

        fun start(context: Context) {
            context.startActivity(Intent(context, SecurityCenterActivity::class.java))
        }
    }
}