package com.blocksdecoded.dex.presentation.guest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.main.MainActivity
import com.blocksdecoded.dex.presentation.restore.RestoreWalletActivity
import com.blocksdecoded.dex.presentation.widgets.click.setSingleClickListener
import kotlinx.android.synthetic.main.activity_guest.*

class GuestActivity : CoreActivity() {

    private lateinit var viewModel: GuestViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest)
        viewModel = ViewModelProviders.of(this).get(GuestViewModel::class.java)
        
        viewModel.openBackupEvent.observe(this, Observer {
            MainActivity.start(this)
        })
    
        viewModel.openRestoreEvent.observe(this, Observer {
            RestoreWalletActivity.start(this)
        })
        
        viewModel.finishEvent.observe(this, Observer {
            finish()
        })
        
        guest_create_wallet.setSingleClickListener { viewModel.onCreateClick() }
        guest_restore_wallet.setSingleClickListener { viewModel.onRestoreClick() }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, GuestActivity::class.java))
        }
    }

}