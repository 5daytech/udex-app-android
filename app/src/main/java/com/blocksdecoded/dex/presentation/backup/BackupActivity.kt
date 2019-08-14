package com.blocksdecoded.dex.presentation.backup

import android.content.Context
import android.content.Intent
import com.blocksdecoded.dex.core.ui.CoreActivity

class BackupActivity : CoreActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, BackupActivity::class.java))
        }
    }
}