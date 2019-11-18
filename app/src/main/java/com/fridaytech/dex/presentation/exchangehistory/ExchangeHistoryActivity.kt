package com.fridaytech.dex.presentation.exchangehistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.fridaytech.dex.core.ui.SwipeableActivity

class ExchangeHistoryActivity : SwipeableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, ExchangeHistoryFragment.newInstance())
            .commit()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ExchangeHistoryActivity::class.java)

            context.startActivity(intent)
        }
    }
}
