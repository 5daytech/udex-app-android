package com.blocksdecoded.dex.presentation.settings.coinmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import kotlinx.android.synthetic.main.activity_coin_manager.*

class CoinManagerActivity : CoreActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_manager)
        toolbar.bind(MainToolbar.ToolbarState.BACK) { finish() }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CoinManagerActivity::class.java)
            context.startActivity(intent)
        }
    }
}