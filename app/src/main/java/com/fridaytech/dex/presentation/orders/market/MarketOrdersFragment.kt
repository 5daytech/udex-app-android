package com.fridaytech.dex.presentation.orders.market

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.CoreFragment
import com.fridaytech.dex.presentation.main.IFocusListener
import com.fridaytech.dex.presentation.orders.OrdersHostFragment
import com.fridaytech.dex.presentation.orders.OrdersViewModel
import com.fridaytech.dex.utils.ui.toPercentFormat
import kotlinx.android.synthetic.main.fragment_market_orders.*

class MarketOrdersFragment : CoreFragment(R.layout.fragment_market_orders), MarketOrderViewHolder.Listener,
    IFocusListener {

    private lateinit var buyAdapter: MarketOrdersAdapter
    private lateinit var sellAdapter: MarketOrdersAdapter
    private lateinit var viewModel: OrdersViewModel

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buyAdapter = MarketOrdersAdapter(this)
        sellAdapter = MarketOrdersAdapter(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(OrdersViewModel::class.java)

            viewModel.availablePairs.observe(this, Observer { availablePairs ->
                market_orders_pair_picker?.refreshPairs(availablePairs)
            })

            viewModel.selectedPairPosition.observe(this, Observer { selectedPair ->
                market_orders_pair_picker?.selectedPair = selectedPair
            })

            viewModel.exchangeCoinSymbol.observe(this, Observer { coinCode ->
                market_orders_buy_title?.text = "Buy ($coinCode)"
                market_orders_sell_title?.text = "Sell ($coinCode)"
            })

            viewModel.fillOrderEvent.observe(this, Observer { fillInfo ->
                (context as? OrdersHostFragment.OrderFillListener)?.requestFill(fillInfo)
            })

            viewModel.buyOrders.observe(this, Observer { buyOrders ->
                buyAdapter.setOrders(buyOrders)
            })

            viewModel.sellOrders.observe(this, Observer { sellOrders ->
                sellAdapter.setOrders(sellOrders)
            })

            viewModel.spreadPercent.observe(this, Observer { spread ->
                market_orders_spread.text = "${spread.toPercentFormat()}%"
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        market_orders_pair_picker?.init {
            viewModel.onPickPair(it)
        }

        market_orders_sell.layoutManager = LinearLayoutManager(context)
        market_orders_sell.adapter = sellAdapter

        market_orders_buy.layoutManager = LinearLayoutManager(context)
        market_orders_buy.adapter = buyAdapter
    }

    //endregion

    override fun onFocused() {
    }

    //region ViewHolder

    override fun onClick(position: Int) {

    }

    //endregion

    companion object {
        fun newInstance() = MarketOrdersFragment()
    }
}
