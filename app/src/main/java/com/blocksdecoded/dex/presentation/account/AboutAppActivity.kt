package com.blocksdecoded.dex.presentation.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import kotlinx.android.synthetic.main.activity_about_app.*

class AboutAppActivity : CoreActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        toolbar.bind(MainToolbar.ToolbarState.BACK) {
            finish()
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, AboutAppActivity::class.java))
        }
    }
}