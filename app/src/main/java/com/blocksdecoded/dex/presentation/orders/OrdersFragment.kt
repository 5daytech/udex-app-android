package com.blocksdecoded.dex.presentation.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment

class OrdersFragment : CoreFragment(R.layout.fragment_orders) {

    private lateinit var viewModel: OrdersViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OrdersViewModel::class.java)
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance() = OrdersFragment()
    }

}
