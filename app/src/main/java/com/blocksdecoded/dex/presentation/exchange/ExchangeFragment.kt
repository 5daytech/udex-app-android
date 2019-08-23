package com.blocksdecoded.dex.presentation.exchange

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.dialogs.processing.ProcessingDialog
import com.blocksdecoded.dex.presentation.dialogs.sent.SentDialog
import com.blocksdecoded.dex.presentation.exchange.ExchangeFragment.InputField.*
import com.blocksdecoded.dex.presentation.exchange.ExchangeType.*
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmDialog
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.presentation.exchange.view.limit.LimitOrderViewModel
import com.blocksdecoded.dex.presentation.exchange.view.market.MarketOrderViewModel
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import com.blocksdecoded.dex.presentation.widgets.NumPadItem
import com.blocksdecoded.dex.presentation.widgets.NumPadItemType
import com.blocksdecoded.dex.presentation.widgets.NumPadItemsAdapter
import com.blocksdecoded.dex.presentation.widgets.click.setSingleClickListener
import com.blocksdecoded.dex.utils.currentFocus
import com.blocksdecoded.dex.utils.ui.ToastHelper
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_exchange.*
import kotlinx.android.synthetic.main.view_limit_order.*
import kotlinx.android.synthetic.main.view_market_order.*
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class ExchangeFragment : CoreFragment(R.layout.fragment_exchange), NumPadItemsAdapter.Listener {

    private lateinit var limitOrderViewModel: LimitOrderViewModel
    private lateinit var marketOrderViewModel: MarketOrderViewModel

	private lateinit var exchangeAdapter: ExchangeAdapter
    
    private val disposables = CompositeDisposable()
    private var processingDialog: DialogFragment? = null
    
    private val activeType: ExchangeType
        get() = if (exchange_pager.currentItem == 0) MARKET else LIMIT
    
    private val exchangeEnableObserver = Observer<Boolean> {
        exchange_confirm?.isEnabled = it
    }
    
    private val confirmObserver = Observer<ExchangeConfirmInfo> {
        ExchangeConfirmDialog.open(childFragmentManager, it)
    }

    private val processingObserver = Observer<Unit> {
        processingDialog = ProcessingDialog.open(childFragmentManager)
    }

    private val processingDismissObserver = Observer<Unit> {
        processingDialog?.dismiss()
    }

    //region Lifecycle
    
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		exchangeAdapter = ExchangeAdapter()
	}
	
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        initMarketViewModel()

        initLimitViewModel()
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        toolbar?.bind(MainToolbar.ToolbarState.NONE)
        
        exchange_numpad?.bind(this, NumPadItemType.DOT, false, scrollable = true)
        exchange_confirm?.setSingleClickListener {
            when(activeType) {
                MARKET -> marketOrderViewModel.onExchangeClick()
                LIMIT -> limitOrderViewModel.onExchangeClick()
            }
        }
        
        exchange_pager?.adapter = exchangeAdapter
        exchange_pager?.offscreenPageLimit = 2
        exchange_tab_layout?.setupWithViewPager(exchange_pager)
        
        exchange_pager?.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                exchange_confirm?.text = when(activeType) {
                    MARKET -> "Exchange"
                    LIMIT -> "Place order"
                }
            }
        })
    
        // Market view
        
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
    
        // Limit view
        
        exchange_limit_view?.bind(
            onMaxClick = { limitOrderViewModel.onMaxClick() },
            onSendCoinPick = { limitOrderViewModel.onSendCoinPick(it) },
            onReceiveCoinPick = { limitOrderViewModel.onReceiveCoinPick(it) },
            onSwitchClick = { limitOrderViewModel.onSwitchClick() }
        )
    
        exchange_limit_view?.sendAmountChangeSubject?.debounce(200, TimeUnit.MILLISECONDS)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { limitOrderViewModel.onSendAmountChange(it) }
            ?.let { disposables.add(it) }
    
        exchange_limit_view?.priceChangeSubject?.debounce(200, TimeUnit.MILLISECONDS)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { limitOrderViewModel.onPriceChange(it) }
            ?.let { disposables.add(it) }
    }
    
    //endregion
    
    //region Init

    private fun initMarketViewModel() {
        marketOrderViewModel = ViewModelProviders.of(this).get(MarketOrderViewModel::class.java)
    
        marketOrderViewModel.sendCoins.observe(this, Observer {
            exchange_market_view?.updateSendCoins(it)
        })
    
        marketOrderViewModel.receiveCoins.observe(this, Observer {
            exchange_market_view?.updateReceiveCoins(it)
        })

        marketOrderViewModel.receiveInfo.observe(this, Observer {
            exchange_market_view?.updateReceiveInfo(it)
        })
    
        marketOrderViewModel.viewState.observe(this, Observer {
            exchange_market_view?.updateState(it)
        })
    
        marketOrderViewModel.messageEvent.observe(this, Observer {
            ToastHelper.showInfoMessage("Coins unlock and fill started")
        })
    
        marketOrderViewModel.successEvent.observe(this, Observer {
            SentDialog.open(childFragmentManager, it)
        })

        marketOrderViewModel.errorEvent.observe(this, Observer {
            ToastHelper.showErrorMessage(it)
        })
    
        marketOrderViewModel.exchangeEnabled.observe(this, Observer {
            exchange_confirm?.isEnabled = it
        })
    
        marketOrderViewModel.confirmEvent.observe(this, confirmObserver)
    
        marketOrderViewModel.exchangePrice.observe(this, Observer {
            val info = "Price per token: ${it.toDisplayFormat()}" + if (it == BigDecimal.ZERO) {
                "\nOrderbook is empty"
            } else { "" }
        
            exchange_info?.text = info
        })

        marketOrderViewModel.showProcessingEvent.observe(this, processingObserver)
        marketOrderViewModel.processingDismissEvent.observe(this, processingDismissObserver)
    }

    private fun initLimitViewModel() {
        limitOrderViewModel = ViewModelProviders.of(this).get(LimitOrderViewModel::class.java)
    
        limitOrderViewModel.sendCoins.observe(this, Observer {
            exchange_limit_view?.updateSendCoins(it)
        })
    
        limitOrderViewModel.receiveCoins.observe(this, Observer {
            exchange_limit_view?.updateReceiveCoins(it)
        })
    
        limitOrderViewModel.viewState.observe(this, Observer {
            exchange_limit_view?.updateState(it)
        })

        limitOrderViewModel.priceInfo.observe(this, Observer {
            exchange_limit_view?.updatePrice(it)
        })

        limitOrderViewModel.receiveInfo.observe(this, Observer {
            exchange_limit_view?.updateTotal(it)
        })
    
        limitOrderViewModel.messageEvent.observe(this, Observer {
            ToastHelper.showSuccessMessage(it)
        })

        limitOrderViewModel.errorEvent.observe(this, Observer {
            ToastHelper.showErrorMessage(it)
        })
    
        limitOrderViewModel.successEvent.observe(this, Observer {
            SentDialog.open(childFragmentManager, it)
        })
    
        limitOrderViewModel.exchangeEnabled.observe(this, Observer {
            exchange_confirm?.isEnabled = it
        })
    
        limitOrderViewModel.confirmEvent.observe(this, confirmObserver)
    
        limitOrderViewModel.exchangePrice.observe(this, Observer {
            val info = "Price per token: ${it.toDisplayFormat()}" + if (it == BigDecimal.ZERO) {
                "\nOrderbook is empty"
            } else { "" }
        
            exchange_info?.text = info
        })

        limitOrderViewModel.averagePrice.observe(this, Observer {
            exchange_limit_view?.updateAveragePrice(it)
        })

        limitOrderViewModel.showProcessingEvent.observe(this, processingObserver)
        limitOrderViewModel.processingDismissEvent.observe(this, processingDismissObserver)
    }
    
    //endregion

    override fun onItemClick(item: NumPadItem) {
        val inputType = getInputField()
        
        val inputField = when(inputType) {
            MARKET_AMOUNT -> exchange_amount_input
            LIMIT_AMOUNT -> limit_amount_input
            LIMIT_PRICE -> limit_price_input
        }
        
        val inputConnection = when(inputType) {
            MARKET_AMOUNT -> exchange_market_view?.inputConnection
            LIMIT_AMOUNT -> exchange_limit_view?.amountInputConnection
            LIMIT_PRICE -> exchange_limit_view?.priceInputConnection
        }
        
        when (item.type) {
            NumPadItemType.NUMBER -> inputConnection?.commitText(item.number.toString(), 1)
            NumPadItemType.DELETE -> inputConnection?.deleteSurroundingText(1, 0)
            NumPadItemType.DOT -> {
                if (inputField?.text?.toString()?.contains(".") != true) {
                    inputConnection?.commitText(".", 1)
                }
            }
        }
    }
    
    private fun getInputField(): InputField = when(currentFocus?.id) {
        R.id.exchange_amount_input -> MARKET_AMOUNT
        R.id.limit_amount_input -> LIMIT_AMOUNT
        R.id.limit_price_input -> LIMIT_PRICE
        else -> when(activeType) {
            MARKET -> MARKET_AMOUNT
            LIMIT -> LIMIT_AMOUNT
        }
    }

    companion object {
        fun newInstance() = ExchangeFragment()
    }

    enum class InputField {
        MARKET_AMOUNT,
        LIMIT_AMOUNT,
        LIMIT_PRICE
    }
}
