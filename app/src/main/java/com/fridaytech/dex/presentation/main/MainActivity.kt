package com.fridaytech.dex.presentation.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.CoreActivity
import com.fridaytech.dex.presentation.balance.BalanceFragment
import com.fridaytech.dex.presentation.exchange.ExchangeFragment
import com.fridaytech.dex.presentation.exchange.market.MarketOrderViewModel
import com.fridaytech.dex.presentation.howitworks.guide.GuideDialog
import com.fridaytech.dex.presentation.markets.MarketsFragment
import com.fridaytech.dex.presentation.orders.OrdersHostFragment
import com.fridaytech.dex.presentation.orders.model.FillOrderInfo
import com.fridaytech.dex.presentation.send.SendViewModel
import com.fridaytech.dex.presentation.settings.SettingsFragment
import com.fridaytech.dex.presentation.widgets.statusinfo.StatusInfoView
import com.fridaytech.dex.utils.Logger
import com.fridaytech.dex.utils.getAttr
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*

class MainActivity :
    CoreActivity(),
    OrdersHostFragment.OrderFillListener {

    private lateinit var adapter: MainPagerAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sendViewModel: SendViewModel
    private lateinit var marketOrderViewModel: MarketOrderViewModel

    private var statusInfoView: StatusInfoView? = null

    override fun onBackPressed() {
        if (main_view_pager?.currentItem != 0) {
            main_view_pager?.setCurrentItem(0, true)
        } else {
            super.onBackPressed()
        }
    }

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        sendViewModel = ViewModelProviders.of(this).get(SendViewModel::class.java)
        marketOrderViewModel = ViewModelProviders.of(this).get(MarketOrderViewModel::class.java)
        adapter = MainPagerAdapter(
            supportFragmentManager
        )

        main_view_pager_stub?.let {
            it.setOnInflateListener { _, _ -> initViewPager() }
            it.inflate()
        }

        mainViewModel.settingsNotificationsAmount.observe(this, Observer {
            updateSettingsTabCounter(it)
        })

        mainViewModel.isConnectionEnabled.observe(this, Observer { isConnected ->
            statusInfoView = if (isConnected) {
                statusInfoView?.setProgressVisible(false)
                statusInfoView?.setText(R.string.connection_restored)
                Handler().postDelayed({
                    StatusInfoView.hide(this, null)
                }, 2000)
                null
            } else {
                StatusInfoView.addStatusBarInfoText(
                    this,
                    textRes = R.string.connection_no_available
                )
            }
        })

        mainViewModel.showGuideEvent.observe(this, Observer {
            GuideDialog.show(supportFragmentManager)
        })
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null && !TextUtils.isEmpty(scanResult.contents)) {
            sendViewModel.onScanResult(scanResult.contents)
        }
    }

    //endregion

    //region Init

    private fun updateSettingsTabCounter(count: Int) {
        val countText = if (count < 1) "" else "!"
        main_bottom_nav?.setNotification(countText,
            SETTINGS_TAB_POSITION
        )
    }

    private fun initViewPager() {
        main_view_pager?.adapter = adapter
        main_view_pager?.offscreenPageLimit = 4

        main_bottom_nav?.defaultBackgroundColor = theme.getAttr(R.attr.NavigationColor) ?: Color.BLACK

        main_bottom_nav?.addItem(AHBottomNavigationItem(R.string.title_wallet, R.drawable.tab_balance, 0))
        main_bottom_nav?.addItem(AHBottomNavigationItem(R.string.title_orders, R.drawable.tab_orders, 0))
        main_bottom_nav?.addItem(AHBottomNavigationItem(R.string.title_exchange, R.drawable.tab_exchange, 0))
        main_bottom_nav?.addItem(AHBottomNavigationItem(R.string.title_markets, R.drawable.tab_markets, 0))
        main_bottom_nav?.addItem(AHBottomNavigationItem(R.string.title_settings, R.drawable.tab_settings, 0))

        main_bottom_nav?.accentColor = theme.getAttr(R.attr.AccentTextColor) ?: Color.WHITE
        main_bottom_nav?.inactiveColor = ContextCompat.getColor(this, R.color.tab_inactive)
        main_bottom_nav?.titleState = AHBottomNavigation.TitleState.ALWAYS_HIDE
        main_bottom_nav?.setUseElevation(false)

        main_bottom_nav?.setOnTabSelectedListener { position, wasSelected ->
            if (!wasSelected) {
                focusCurrentFragment(position)
                main_view_pager?.setCurrentItem(position, false)
            }
            true
        }

        main_view_pager?.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                main_bottom_nav?.currentItem = position
            }
        })

        val activeTab = intent.getIntExtra(EXTRA_ACTIVE_TAB, 0)
        main_view_pager?.setCurrentItem(activeTab, false)
    }

    //endregion

    override fun requestFill(fillInfo: FillOrderInfo) {
        main_view_pager?.setCurrentItem(2, false)
        marketOrderViewModel.requestFillOrder(fillInfo.pair, fillInfo.amount, fillInfo.side)
    }

    private fun focusCurrentFragment(itemPosition: Int) = try {
        val page =
            supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.main_view_pager + ":" + itemPosition)
        (page as? IFocusListener)?.onFocused()
    } catch (e: Exception) {
        Logger.e(e)
    }

    companion object {
        private const val EXTRA_ACTIVE_TAB = "active_tab"
        const val SETTINGS_TAB_POSITION = 4

        fun startWithTab(context: Context, activeTab: Int) {
            start(
                context,
                true,
                activeTab
            )
        }

        fun start(context: Context, asNewTask: Boolean = true, activeTab: Int? = null) {
            val intent = Intent(context, MainActivity::class.java)

            activeTab?.let {
                intent.putExtra(EXTRA_ACTIVE_TAB, it)
            }

            if (asNewTask) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            context.startActivity(intent)

            (context as Activity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(p0: Int): Fragment = when (p0) {
            0 -> BalanceFragment.newInstance()
            1 -> OrdersHostFragment.newInstance()
            2 -> ExchangeFragment.newInstance()
            3 -> MarketsFragment.newInstance()
            else -> SettingsFragment.newInstance()
        }

        override fun getCount(): Int = 5
    }
}
