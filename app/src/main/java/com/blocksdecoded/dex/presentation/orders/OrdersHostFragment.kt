package com.blocksdecoded.dex.presentation.orders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.main.IFocusListener
import com.blocksdecoded.dex.presentation.orders.info.OrderInfoDialog
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.FillOrderInfo
import com.blocksdecoded.dex.utils.ui.ToastHelper
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.synthetic.main.fragment_orders_host.*

class OrdersHostFragment : CoreFragment(R.layout.fragment_orders_host), IFocusListener {

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

            viewModel.fillOrderEvent.observe(this, Observer { fillInfo ->
                (context as? OrderFillListener)?.requestFill(fillInfo)
            })

            viewModel.exchangeCoinSymbol.observe(this, Observer {
                if (orders_tab_layout.tabCount > 0) {
                    val firstTab = orders_tab_layout.getTabAt(0)
                    firstTab?.text = "SELL $it"
                    val secondTab = orders_tab_layout.getTabAt(1)
                    secondTab?.text = "BUY $it"
                }
            })

            viewModel.messageEvent.observe(this, Observer {
                ToastHelper.showSuccessMessage(it)
            })

            viewModel.errorEvent.observe(this, Observer {
                ToastHelper.showErrorMessage(it)
            })

            viewModel.cancelAllConfirmEvent.observe(this, Observer { cancelInfo ->
                fragmentManager?.let { CancelOrderConfirmDialog.show(it, cancelInfo) }
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

    override fun onFocused() {
        coordinator?.visible = true
    }

    companion object {
        fun newInstance() = OrdersHostFragment()
    }

    interface OrderFillListener {
        fun requestFill(fillInfo: FillOrderInfo)
    }

    private class OrdersHostAdapter(
        fm: FragmentManager
    ) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> OrdersFragment.newInstance(EOrderSide.BUY)
                1 -> OrdersFragment.newInstance(EOrderSide.SELL)
                2 -> OrdersFragment.newInstance(EOrderSide.MY)
                else -> throw Exception("Orders host adapter fragment at position not exist $position")
            }
        }

        override fun getCount(): Int = 3

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                2 -> "My Orders"
                else -> ""
            }
        }
    }
}
