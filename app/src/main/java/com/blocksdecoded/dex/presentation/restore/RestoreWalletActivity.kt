package com.blocksdecoded.dex.presentation.restore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity

class RestoreWalletActivity: CoreActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore_wallet)
    }


    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, RestoreWalletActivity::class.java))
        }
    }
}