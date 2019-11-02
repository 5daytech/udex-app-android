package com.blocksdecoded.dex.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.BuildConfig
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.coinmanager.CoinManagerActivity
import com.blocksdecoded.dex.presentation.exchangehistory.ExchangeHistoryActivity
import com.blocksdecoded.dex.presentation.main.IFocusListener
import com.blocksdecoded.dex.presentation.main.MainActivity
import com.blocksdecoded.dex.presentation.settings.addressbook.AddressBookActivity
import com.blocksdecoded.dex.presentation.settings.security.SecurityCenterActivity
import com.blocksdecoded.dex.utils.ui.ShareUtils
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

        viewModel.selectedTheme.observe(this, Observer { lightMode ->
            lightMode?.let {
                light_mode?.apply {
                    selectedTheme = it

                    switchOnCheckedChangeListener = object : ThemeSwitchView.ThemeSwitchListener {
                        override fun onChange(state: Int) {
                            viewModel.onThemeChanged(state)
                        }
                    }
                }
            }
        })

        viewModel.isBackedUp.observe(this, Observer {
            security_center?.setInfoBadgeVisible(!it)
        })

        viewModel.restartAppEvent.observe(this, Observer {
            activity?.let {
                MainActivity.startWithTab(it, MainActivity.SETTINGS_TAB_POSITION)
                it.finish()
            }
        })

        security_center?.setOnClickListener { viewModel.onSecurityCenterClick() }
        about_app?.setOnClickListener { viewModel.onAboutAppClick() }

        share_app?.setOnClickListener {
            activity?.let {
                ShareUtils.shareMessage(it, App.appConfiguration.appShareUrl)
            }
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

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onFocused() {
        coordinator?.visible = true
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
