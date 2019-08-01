package com.blocksdecoded.dex.presentation.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import kotlinx.android.synthetic.main.fragment_orders_host.*

class OrdersHostFragment : CoreFragment(R.layout.fragment_orders_host) {

    private var adapter: OrdersHostAdapter? = null
    private lateinit var viewModel: OrdersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager?.let {
            adapter = OrdersHostAdapter(it)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OrdersViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orders_view_pager?.adapter = adapter
        orders_tab_layout?.setupWithViewPager(orders_view_pager)
    }

    companion object {
        fun newInstance() = OrdersHostFragment()
    }

    private class OrdersHostAdapter(
            fm: FragmentManager
    ): FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> OrdersFragment()
                1 -> OrdersFragment()
                else -> throw Exception("Orders host adapter fragment at position not exist $position")
            }
        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position) {
                0 -> "My Orders"
                1 -> "Orders"
                else -> ""
            }
        }

    }

}
