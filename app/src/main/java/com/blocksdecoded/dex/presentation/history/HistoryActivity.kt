package com.blocksdecoded.dex.presentation.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : CoreActivity() {

    lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        toolbar.bind(MainToolbar.ToolbarState.BACK) {
            finish()
        }

        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, HistoryActivity::class.java)

            context.startActivity(intent)
        }
    }
}