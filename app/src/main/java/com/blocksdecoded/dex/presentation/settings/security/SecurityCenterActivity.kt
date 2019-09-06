package com.blocksdecoded.dex.presentation.settings.security

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.backup.BackupActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import kotlinx.android.synthetic.main.activity_security_center.*

class SecurityCenterActivity : CoreActivity() {

    private lateinit var viewModel: SecurityCenterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_center)

        toolbar.bind(MainToolbar.ToolbarState.BACK) { finish() }

        viewModel = ViewModelProviders.of(this).get(SecurityCenterViewModel::class.java)

        security_center_backup?.setOnClickListener {
            BackupActivity.start(this)
        }

        viewModel.passcodeEnabled.observe(this, Observer { passcodeEnabled ->
            security_fingerprint_switch?.isEnabled = passcodeEnabled
            security_edit_passcode?.isEnabled = passcodeEnabled

            val alpha = if (passcodeEnabled) 1f else 0.3f

            security_fingerprint_switch?.alpha = alpha
            security_edit_passcode?.alpha = alpha
        })

    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SecurityCenterActivity::class.java))
        }
    }
}