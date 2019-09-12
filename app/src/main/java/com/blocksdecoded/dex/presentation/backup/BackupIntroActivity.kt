package com.blocksdecoded.dex.presentation.backup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.pin.PinActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import kotlinx.android.synthetic.main.activity_backup_intro.*

class BackupIntroActivity : CoreActivity() {

    lateinit var viewModel: BackupIntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup_intro)

        viewModel = ViewModelProviders.of(this).get(BackupIntroViewModel::class.java)

        viewModel.openUnlockEvent.observe(this, Observer {
            PinActivity.startForUnlock(this, REQUEST_CODE_UNLOCK, showCancel = true)
        })

        viewModel.openBackupEvent.observe(this, Observer {
            BackupActivity.start(this)
        })

        viewModel.finishEvent.observe(this, Observer { finish() })

        toolbar?.bind(MainToolbar.ToolbarState.BACK) { finish() }

        backup_intro_show_key?.setOnClickListener {
            viewModel.onShowClick()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_UNLOCK) {
            when (resultCode) {
                PinActivity.RESULT_OK -> viewModel.onUnlocked()
                PinActivity.RESULT_CANCELLED -> {}
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_UNLOCK = 1

        fun start(context: Context) {
            val intent = Intent(context, BackupIntroActivity::class.java)
            context.startActivity(intent)
        }
    }

}