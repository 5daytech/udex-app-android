package com.blocksdecoded.dex.presentation.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.settings.SettingsFragment
import com.blocksdecoded.dex.presentation.exchange.ExchangeFragment
import com.blocksdecoded.dex.presentation.markets.MarketsFragment
import com.blocksdecoded.dex.presentation.orders.OrdersHostFragment
import com.blocksdecoded.dex.presentation.balance.BalanceFragment
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.send.SendViewModel
import com.blocksdecoded.dex.presentation.exchange.view.market.MarketOrderViewModel
import com.blocksdecoded.dex.presentation.orders.model.FillOrderInfo
import com.blocksdecoded.dex.utils.Logger
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
        adapter = MainPagerAdapter(supportFragmentManager)

        main_view_pager_stub?.let {
            it.setOnInflateListener { _, _ -> initViewPager() }
            it.inflate()
        }

        mainViewModel.settingsNotificationsAmount.observe(this, Observer {
            updateSettingsTabCounter(it)
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

    private fun updateSettingsTabCounter(count: Int) {
        val countText = if (count < 1) "" else "!"
        main_bottom_nav?.setNotification(countText, SETTINGS_TAB_POSITION)
    }

    private fun initViewPager()  {
        main_view_pager?.adapter = adapter
        main_view_pager?.offscreenPageLimit = 4

        main_bottom_nav?.defaultBackgroundColor = ContextCompat.getColor(this, R.color.dark_main)

        main_bottom_nav?.addItem(AHBottomNavigationItem(R.string.title_wallet, R.drawable.tab_balance, 0))
        main_bottom_nav?.addItem(AHBottomNavigationItem(R.string.title_orders, R.drawable.tab_orders, 0))
        main_bottom_nav?.addItem(AHBottomNavigationItem(R.string.title_exchange, R.drawable.tab_exchange, 0))
        main_bottom_nav?.addItem(AHBottomNavigationItem(R.string.title_markets, R.drawable.tab_markets, 0))
        main_bottom_nav?.addItem(AHBottomNavigationItem(R.string.title_settings, R.drawable.tab_settings, 0))

        main_bottom_nav?.accentColor = ContextCompat.getColor(this, R.color.turquoise)
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
    }

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
        private const val SETTINGS_TAB_POSITION = 4
        fun start(context: Context, asNewTask: Boolean = true) {
            val intent = Intent(context, MainActivity::class.java)

            if (asNewTask) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            context.startActivity(intent)

//            if (asNewTask)
            (context as Activity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private class MainPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
        override fun getItem(p0: Int): Fragment = when(p0) {
            0 -> BalanceFragment.newInstance()
            1 -> OrdersHostFragment.newInstance()
            2 -> ExchangeFragment.newInstance()
            3 -> MarketsFragment.newInstance()
            else -> SettingsFragment.newInstance()
        }

        override fun getCount(): Int = 5
    }
}
