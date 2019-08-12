package com.blocksdecoded.dex.presentation.restore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.main.MainActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import com.blocksdecoded.dex.presentation.widgets.words.WordInputViewHolder
import com.blocksdecoded.dex.presentation.widgets.words.WordsInputAdapter
import com.blocksdecoded.dex.utils.ui.ToastHelper
import kotlinx.android.synthetic.main.activity_restore_wallet.*

class RestoreWalletActivity: CoreActivity(), WordInputViewHolder.OnWordChangeListener {

    private lateinit var viewModel: RestoreWalletViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore_wallet)
    
        viewModel = ViewModelProviders.of(this).get(RestoreWalletViewModel::class.java)
        
        toolbar.bind(MainToolbar.ToolbarState.BACK) {
            finish()
        }
    
        viewModel.errorEvent.observe(this, Observer { ToastHelper.showErrorMessage(it) })
        viewModel.successEvent.observe(this, Observer { ToastHelper.showSuccessMessage(it) })
        viewModel.navigateToMain.observe(this, Observer {
            MainActivity.start(this)
            this@RestoreWalletActivity.finish()
        })
        
        restore_recycler.isNestedScrollingEnabled = false
        restore_recycler.layoutManager = GridLayoutManager(this, 2)
        restore_recycler.adapter = WordsInputAdapter(this)
        
        restore_confirm.setOnClickListener {
            viewModel.onRestoreClick()
        }
    }

    override fun onChange(position: Int, value: String) {
        viewModel.onWordChanged(position, value)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, RestoreWalletActivity::class.java))
        }
    }
}