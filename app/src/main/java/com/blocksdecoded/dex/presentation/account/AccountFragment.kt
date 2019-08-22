package com.blocksdecoded.dex.presentation.account

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.lifecycle.Observer
import com.blocksdecoded.dex.BuildConfig

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.backup.BackupActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar.ToolbarState.*
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : CoreFragment(R.layout.fragment_account) {

    private lateinit var viewModel: AccountViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        
        viewModel.openBackupEvent.observe(this, Observer {
            activity?.let { BackupActivity.start(it) }
        })
    
        toolbar?.bind(NONE)

        account_security_center?.setOnClickListener {
            activity?.let {
                SecurityCenterActivity.start(it)
            }
        }
        account_about_app?.setOnClickListener {
            activity?.let {
                AboutAppActivity.start(it)
            }
        }
        account_language?.setOnClickListener {
            activity?.let {
                LanguageActivity.start(it)
            }
        }
        account_share_app?.setOnClickListener {  }

        account_light_mode?.setOnClickListener { account_light_mode_switch.isChecked = !account_light_mode_switch.isChecked }

        account_light_mode_switch?.setOnCheckedChangeListener { compoundButton, b ->

        }

        app_version?.text = "v ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\n${BuildConfig.FLAVOR}"
    }

    companion object {
        fun newInstance() = AccountFragment()
    }

}
