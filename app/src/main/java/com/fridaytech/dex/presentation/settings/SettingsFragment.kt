package com.fridaytech.dex.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fridaytech.dex.App
import com.fridaytech.dex.BuildConfig
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.CoreFragment
import com.fridaytech.dex.presentation.coinmanager.CoinManagerActivity
import com.fridaytech.dex.presentation.howitworks.guide.GuideDialog
import com.fridaytech.dex.presentation.main.IFocusListener
import com.fridaytech.dex.presentation.main.MainActivity
import com.fridaytech.dex.presentation.settings.addressbook.AddressBookActivity
import com.fridaytech.dex.presentation.settings.security.SecurityCenterActivity
import com.fridaytech.dex.utils.openUrl
import com.fridaytech.dex.utils.ui.ShareUtils
import com.fridaytech.dex.utils.visible
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : CoreFragment(R.layout.fragment_settings),
    IFocusListener {

    private lateinit var viewModel: SettingsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)

        viewModel.openAboutAppEvent.observe(this, Observer {
            activity?.let {
                AboutAppActivity.start(
                    it
                )
            }
        })

        viewModel.openSecurityCenterEvent.observe(this, Observer {
            activity?.let { SecurityCenterActivity.start(it) }
        })

        viewModel.selectedTheme.observe(this, Observer { lightMode ->
            lightMode?.let {
                light_mode?.apply {
                    selectedTheme = it

                    switchOnCheckedChangeListener = object :
                        ThemeSwitchView.ThemeSwitchListener {
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

        how_it_works?.setOnClickListener {
            GuideDialog.show(childFragmentManager)
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

        company_logo?.setOnClickListener {
            activity?.let { it.openUrl(viewModel.companyUrl) }
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
