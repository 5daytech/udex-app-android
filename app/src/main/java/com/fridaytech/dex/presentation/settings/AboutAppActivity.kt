package com.fridaytech.dex.presentation.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.SwipeableActivity
import com.fridaytech.dex.presentation.widgets.MainToolbar
import kotlinx.android.synthetic.main.activity_about_app.*

class AboutAppActivity : SwipeableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        toolbar.bind(MainToolbar.getBackAction { finish() })
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, AboutAppActivity::class.java))
        }
    }
}
