package com.blocksdecoded.dex.presentation.backup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import com.blocksdecoded.dex.presentation.widgets.words.WordsAdapter
import com.blocksdecoded.dex.utils.ui.ToastHelper
import kotlinx.android.synthetic.main.activity_backup.*

class BackupActivity : CoreActivity() {
    
    private lateinit var viewModel: BackupViewModel
    private lateinit var adapter: WordsAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)
    
        adapter = WordsAdapter()
        viewModel = ViewModelProviders.of(this).get(BackupViewModel::class.java)
        
        viewModel.successEvent.observe(this, Observer {
            ToastHelper.showSuccessMessage(it)
        })
        
        viewModel.errorEvent.observe(this, Observer {
            ToastHelper.showErrorMessage(it)
        })
        
        viewModel.words.observe(this, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })
        
        toolbar.bind(MainToolbar.ToolbarState.BACK) {
            finish()
        }
        backup_copy.setOnClickListener { viewModel.onCopyClick() }
        
        backup_recycler.adapter = adapter
        backup_recycler.layoutManager = GridLayoutManager(this, 2)
    }
    
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, BackupActivity::class.java))
        }
    }
}