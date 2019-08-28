package com.blocksdecoded.dex.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.account.AccountFragment
import com.blocksdecoded.dex.presentation.exchange.ExchangeFragment
import com.blocksdecoded.dex.presentation.markets.MarketsFragment
import com.blocksdecoded.dex.presentation.orders.OrdersHostFragment
import com.blocksdecoded.dex.presentation.balance.BalanceFragment
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.send.SendViewModel
import com.blocksdecoded.dex.presentation.exchange.view.market.MarketOrderViewModel
import com.blocksdecoded.dex.presentation.orders.model.FillOrderInfo
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity :
    CoreActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    OrdersHostFragment.OrderFillListener {

    private lateinit var adapter: MainPagerAdapter
    private lateinit var sendViewModel: SendViewModel
    private lateinit var marketOrderViewModel: MarketOrderViewModel

    override fun onBackPressed() {
        if (main_view_pager.currentItem != 0) {
            main_view_pager.setCurrentItem(0, true)
        } else {
            super.onBackPressed()
        }
    }

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sendViewModel = ViewModelProviders.of(this).get(SendViewModel::class.java)
        marketOrderViewModel = ViewModelProviders.of(this).get(MarketOrderViewModel::class.java)

        adapter = MainPagerAdapter(supportFragmentManager)
        main_view_pager.adapter = adapter
        main_view_pager.offscreenPageLimit = 3
        main_bottom_nav.setOnNavigationItemSelectedListener(this)

        main_view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                main_bottom_nav.selectedItemId = when(position) {
                    0 -> R.id.nav_balance
                    1 -> R.id.nav_orders
                    2 -> R.id.nav_exchange
                    3 -> R.id.nav_markets
                    else -> R.id.nav_account
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null && !TextUtils.isEmpty(scanResult.contents)) {
            sendViewModel.onScanResult(scanResult.contents)
        }
    }

    //endregion

    override fun requestFill(fillInfo: FillOrderInfo) {
        main_view_pager.setCurrentItem(2, false)
        marketOrderViewModel.requestFillOrder(fillInfo.pair, fillInfo.amount, fillInfo.side)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val itemPosition = when (item.itemId) {
            R.id.nav_balance -> 0
            R.id.nav_orders -> 1
            R.id.nav_exchange -> 2
            R.id.nav_markets -> 3
            R.id.nav_account -> 4
            else -> 0
        }
        main_view_pager?.setCurrentItem(itemPosition, false)

        return true
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }

    private class MainPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
        override fun getItem(p0: Int): Fragment = when(p0) {
            0 -> BalanceFragment.newInstance()
            1 -> OrdersHostFragment.newInstance()
            2 -> ExchangeFragment.newInstance()
            3 -> MarketsFragment.newInstance()
            else -> AccountFragment.newInstance()
        }

        override fun getCount(): Int = 5
    }
}
