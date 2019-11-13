package com.fridaytech.dex.presentation.restore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.SwipeableActivity
import com.fridaytech.dex.presentation.main.MainActivity
import com.fridaytech.dex.presentation.widgets.MainToolbar
import com.fridaytech.dex.presentation.widgets.words.WordInputViewHolder
import com.fridaytech.dex.presentation.widgets.words.WordsInputAdapter
import com.fridaytech.dex.utils.removeFocus
import com.fridaytech.dex.utils.showKeyboard
import com.fridaytech.dex.utils.ui.ToastHelper
import kotlinx.android.synthetic.main.activity_restore_wallet.*

class RestoreWalletActivity : SwipeableActivity(), WordInputViewHolder.OnWordChangeListener {

    private lateinit var viewModel: RestoreWalletViewModel
    private val words = MutableList(12) { "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore_wallet)

        viewModel = ViewModelProviders.of(this).get(RestoreWalletViewModel::class.java)

        toolbar.bind(MainToolbar.getBackAction { finish() })

        viewModel.errorEvent.observe(this, Observer {
            ToastHelper.showErrorMessage(it)
            focusInput()
        })

        viewModel.successEvent.observe(this, Observer { ToastHelper.showSuccessMessage(it) })
        viewModel.navigateToMain.observe(this, Observer {
            MainActivity.start(this)
            this@RestoreWalletActivity.finish()
        })

        restore_recycler.isNestedScrollingEnabled = false
        restore_recycler.layoutManager = GridLayoutManager(this, 2)
        restore_recycler.adapter =
            WordsInputAdapter(this)

        restore_confirm.setOnClickListener {
            if (!restore_single_line_input?.text.isNullOrEmpty()) {
                restore_single_line_input.text.toString().split(" ").forEachIndexed { index, s ->
                    words[index] = s
                }
            }

            restore_single_line_input?.removeFocus()
            viewModel.onRestoreClick(words)
        }

        focusInput()
    }

    private fun focusInput() {
        Handler().postDelayed({
            restore_single_line_input?.isEnabled = true
            restore_single_line_input?.showKeyboard(false)
        }, 0)
    }

    override fun onChange(position: Int, value: String) {
        words[position] = value
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, RestoreWalletActivity::class.java))
        }
    }
}
