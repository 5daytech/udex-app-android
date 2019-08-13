package com.blocksdecoded.dex.presentation.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.orders.info.OrderInfoDialog
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
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
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(OrdersViewModel::class.java)
    
            viewModel.availablePairs.observe(this, Observer { availablePairs ->
                orders_host_pair_picker?.refreshPairs(availablePairs)
            })
            
            viewModel.selectedPairPosition.observe(this, Observer { selectedPair ->
                orders_host_pair_picker?.selectedPair = selectedPair
            })
            
            viewModel.orderInfoEvent.observe(this, Observer {
                OrderInfoDialog.show(childFragmentManager, it)
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orders_view_pager?.adapter = adapter
        orders_tab_layout?.setupWithViewPager(orders_view_pager)
        
        orders_host_pair_picker?.init {
            viewModel.onPickPair(it)
        }
    }

    companion object {
        fun newInstance() = OrdersHostFragment()
    }

    private class OrdersHostAdapter(
            fm: FragmentManager
    ): FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> OrdersFragment.newInstance(EOrderSide.BUY)
                1 -> OrdersFragment.newInstance(EOrderSide.SELL)
                2 -> OrdersFragment.newInstance(EOrderSide.MY)
                else -> throw Exception("Orders host adapter fragment at position not exist $position")
            }
        }

        override fun getCount(): Int = 3

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position) {
                0 -> "Buy"
                1 -> "Sell"
                2 -> "My"
                else -> ""
            }
        }

    }

}
