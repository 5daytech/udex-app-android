package com.blocksdecoded.dex.presentation.exchange

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.dialogs.sent.SentDialog
import com.blocksdecoded.dex.presentation.exchange.ExchangeType.*
import com.blocksdecoded.dex.presentation.exchange.view.limit.LimitOrderViewModel
import com.blocksdecoded.dex.presentation.exchange.view.market.MarketOrderViewModel
import com.blocksdecoded.dex.presentation.widgets.NumPadItem
import com.blocksdecoded.dex.presentation.widgets.NumPadItemType
import com.blocksdecoded.dex.presentation.widgets.NumPadItemsAdapter
import com.blocksdecoded.dex.utils.ui.ToastHelper
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_exchange.*
import kotlinx.android.synthetic.main.view_market_order.*
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class ExchangeFragment : CoreFragment(R.layout.fragment_exchange), NumPadItemsAdapter.Listener {

    private lateinit var limitOrderViewModel: LimitOrderViewModel
    private lateinit var marketOrderViewModel: MarketOrderViewModel
	private lateinit var exchangeAdapter: ExchangeAdapter
    
    private val disposables = CompositeDisposable()
    
    private val activeType: ExchangeType
        get() = if (exchange_pager.currentItem == 0)
            MARKET
        else
            LIMIT

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		exchangeAdapter = ExchangeAdapter()
	}
	
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        initMarketViewModel()

        initLimitViewModel()
    }

    private fun initMarketViewModel() {
        marketOrderViewModel = ViewModelProviders.of(this).get(MarketOrderViewModel::class.java)
    
        marketOrderViewModel.sendCoins.observe(this, Observer {
            exchange_market_view?.updateSendCoins(it)
        })
    
        marketOrderViewModel.receiveCoins.observe(this, Observer {
            exchange_market_view?.updateReceiveCoins(it)
        })
    
        marketOrderViewModel.viewState.observe(this, Observer {
            exchange_market_view?.updateState(it)
        })
    
        marketOrderViewModel.messageEvent.observe(this, Observer {
            ToastHelper.showInfoMessage("Coins unlock and fill started")
        })
    
        marketOrderViewModel.successEvent.observe(this, Observer {
            SentDialog.show(childFragmentManager, it)
        })
    
        marketOrderViewModel.exchangeEnabled.observe(this, Observer {
            exchange_confirm?.isEnabled = it
        })
    
        marketOrderViewModel.exchangePrice.observe(this, Observer {
            val info = "Price per token: ${it.toDisplayFormat()}" + if (it == BigDecimal.ZERO) {
                "\nOrderbook is empty"
            } else { "" }
        
            exchange_info?.text = info
        })
    }

    private fun initLimitViewModel() {
        limitOrderViewModel = ViewModelProviders.of(this).get(LimitOrderViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exchange_numpad?.bind(this, NumPadItemType.DOT, false, scrollable = true)
        exchange_confirm?.setOnClickListener {
            when(activeType) {
                MARKET -> marketOrderViewModel.onExchangeClick()
                LIMIT -> {}
            }
        }
        
	    exchange_pager?.adapter = exchangeAdapter
        exchange_pager?.offscreenPageLimit = 2
	    exchange_tab_layout?.setupWithViewPager(exchange_pager)
    
        exchange_market_view?.bind(
            onMaxClick = { marketOrderViewModel.onMaxClick() },
            onSendCoinPick = { marketOrderViewModel.onSendCoinPick(it) },
            onReceiveCoinPick = { marketOrderViewModel.onReceiveCoinPick(it) },
            onSwitchClick = { marketOrderViewModel.onSwitchClick() }
        )
    
        exchange_market_view?.sendAmountChangeSubject?.debounce(200, TimeUnit.MILLISECONDS)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { marketOrderViewModel.onSendAmountChange(it) }
            ?.let { disposables.add(it) }
    }

    override fun onItemClick(item: NumPadItem) {
        when (item.type) {
            NumPadItemType.NUMBER -> exchange_market_view?.inputConnection?.commitText(item.number.toString(), 1)
            NumPadItemType.DELETE -> exchange_market_view?.inputConnection?.deleteSurroundingText(1, 0)
            NumPadItemType.DOT -> {
                if (exchange_amount_input?.text?.toString()?.contains(".") != true) {
                    exchange_market_view?.inputConnection?.commitText(".", 1)
                }
            }
        }
    }

    companion object {
        fun newInstance() = ExchangeFragment()
    }

}
