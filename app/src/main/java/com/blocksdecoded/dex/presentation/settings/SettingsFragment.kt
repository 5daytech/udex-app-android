package com.blocksdecoded.dex.presentation.settings

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import com.blocksdecoded.dex.BuildConfig

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.settings.security.SecurityCenterActivity
import com.blocksdecoded.dex.presentation.coinmanager.CoinManagerActivity
import com.blocksdecoded.dex.presentation.exchangehistory.ExchangeHistoryActivity
import com.blocksdecoded.dex.presentation.main.IFocusListener
import com.blocksdecoded.dex.presentation.settings.addressbook.AddressBookActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar.ToolbarState.*
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : CoreFragment(R.layout.fragment_settings), IFocusListener {

    private lateinit var viewModel: SettingsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)

        viewModel.openAboutAppEvent.observe(this, Observer {
            activity?.let { AboutAppActivity.start(it) }
        })

        viewModel.openSecurityCenterEvent.observe(this, Observer {
            activity?.let { SecurityCenterActivity.start(it) }
        })

        viewModel.lightMode.observe(this, Observer { lightMode ->
            lightMode?.let {
                light_mode?.apply {
                    isChecked = it

                    switchOnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
                        viewModel.onLightModeSwitch(isChecked)
                    }
                }
            }
        })

        toolbar?.bind(NONE)

        security_center?.setOnClickListener { viewModel.onSecurityCenterClick() }
        about_app?.setOnClickListener { viewModel.onAboutAppClick() }

        share_app?.setOnClickListener {

        }

        light_mode?.setOnClickListener {
            light_mode?.toggleSwitch()
        }

        app_version?.text = "v ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\n${BuildConfig.FLAVOR}"

        address_book?.setOnClickListener {
            activity?.let { AddressBookActivity.start(it) }
        }

        coin_manager?.setOnClickListener {
            activity?.let { CoinManagerActivity.start(it) }
        }

        exchange_history?.setOnClickListener {
            activity?.let { ExchangeHistoryActivity.start(it) }
        }
    }

    override fun onFocused() {
        coordinator?.visible = true
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }

}
