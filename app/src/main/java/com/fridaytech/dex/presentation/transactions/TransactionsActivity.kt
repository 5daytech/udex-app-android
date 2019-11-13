package com.fridaytech.dex.presentation.transactions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.fridaytech.dex.core.ui.SwipeableActivity

class TransactionsActivity : SwipeableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
                .add(android.R.id.content,
                    TransactionsFragment.newInstance(
                        getCoinCode(
                            intent
                        )
                    )
                )
                .commit()
    }

    companion object {
        private const val EXTRA_COIN_CODE = "coin_code"

        fun start(context: Context, coinCode: String) {
            val intent = Intent(context, TransactionsActivity::class.java)
            intent.putExtra(EXTRA_COIN_CODE, coinCode)
            context.startActivity(intent)
        }

        fun getCoinCode(intent: Intent) = intent.getStringExtra(EXTRA_COIN_CODE) ?: throw Exception("Invalid transactions coin code")
    }
}
