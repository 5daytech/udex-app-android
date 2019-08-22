package com.blocksdecoded.dex.presentation.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.backup.BackupActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import kotlinx.android.synthetic.main.activity_security_center.*

class SecurityCenterActivity : CoreActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_center)

        toolbar.bind(MainToolbar.ToolbarState.BACK) { finish() }

        security_center_backup?.setOnClickListener {
            BackupActivity.start(this)
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SecurityCenterActivity::class.java))
        }
    }
}