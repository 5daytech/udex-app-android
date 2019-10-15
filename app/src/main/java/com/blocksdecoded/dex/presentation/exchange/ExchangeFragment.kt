package com.blocksdecoded.dex.presentation.exchange

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.common.ProcessingDialog
import com.blocksdecoded.dex.presentation.common.TransactionSentDialog
import com.blocksdecoded.dex.presentation.exchange.ExchangeFragment.ExchangeType.*
import com.blocksdecoded.dex.presentation.exchange.ExchangeFragment.InputField.*
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmDialog
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.presentation.exchange.view.limit.LimitOrderViewModel
import com.blocksdecoded.dex.presentation.exchange.view.market.MarketOrderViewModel
import com.blocksdecoded.dex.presentation.main.IFocusListener
import com.blocksdecoded.dex.presentation.widgets.NumPadItem
import com.blocksdecoded.dex.presentation.widgets.NumPadItemType
import com.blocksdecoded.dex.presentation.widgets.NumPadItemsAdapter
import com.blocksdecoded.dex.presentation.widgets.click.setSingleClickListener
import com.blocksdecoded.dex.utils.currentFocus
import com.blocksdecoded.dex.utils.subscribeToInput
import com.blocksdecoded.dex.utils.ui.ToastHelper
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import com.blocksdecoded.dex.utils.visible
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_exchange.*
import kotlinx.android.synthetic.main.view_limit_order.*
import kotlinx.android.synthetic.main.view_market_order.*
import java.math.BigDecimal

class ExchangeFragment : CoreFragment(R.layout.fragment_exchange), NumPadItemsAdapter.Listener, IFocusListener {

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
        processingDialog = ProcessingDialog.show(childFragmentManager)
    }

    private val processingDismissObserver = Observer<Unit> {
        processingDialog?.dismiss()
    }

    private val errorObserver = Observer<Int> {
        ToastHelper.showErrorMessage(it)
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
        
        exchange_numpad?.bind(this, NumPadItemType.DOT, false, scrollable = true)

        exchange_confirm?.setSingleClickListener {
            when(activeType) {
                MARKET -> marketOrderViewModel.onExchangeClick()
                LIMIT -> limitOrderViewModel.onExchangeClick()
            }
        }
        
        exchange_pager?.adapter = exchangeAdapter
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

        exchange_market_view?.sendAmountChangeSubject?.subscribeToInput {
            marketOrderViewModel.onSendAmountChange(it)
        }?.let { disposables.add(it) }

        exchange_market_view?.receiveAmountChangeSubject?.subscribeToInput {
            marketOrderViewModel.onReceiveAmountChange(it)
        }?.let { disposables.add(it) }
    
        // Limit view
        exchange_limit_view?.bind(
            onMaxClick = { limitOrderViewModel.onMaxClick() },
            onSendCoinPick = { limitOrderViewModel.onSendCoinPick(it) },
            onReceiveCoinPick = { limitOrderViewModel.onReceiveCoinPick(it) },
            onSwitchClick = { limitOrderViewModel.onSwitchClick() }
        )
    
        exchange_limit_view?.amountChangeSubject?.subscribeToInput {
            limitOrderViewModel.onSendAmountChange(it)
        }?.let { disposables.add(it) }
    
        exchange_limit_view?.priceChangeSubject?.subscribeToInput {
            limitOrderViewModel.onPriceChange(it)
        }?.let { disposables.add(it) }
    }

    //endregion

    //region Init

    private fun initMarketViewModel() {
        activity?.let {
            marketOrderViewModel = ViewModelProviders.of(it).get(MarketOrderViewModel::class.java)
        } ?: return
    
        marketOrderViewModel.sendCoins.observe(this, Observer {
            exchange_market_view?.updateSendCoins(it)
        })
    
        marketOrderViewModel.receiveCoins.observe(this, Observer {
            exchange_market_view?.updateReceiveCoins(it)
        })

        marketOrderViewModel.sendAmount.observe(this, Observer {
            exchange_market_view?.updateSendHint(it)
        })

        marketOrderViewModel.receiveAmount.observe(this, Observer {
            exchange_market_view?.updateReceiveAmount(it)
        })

        marketOrderViewModel.receiveHintInfo.observe(this, Observer {
            exchange_market_view?.updateReceiveHint(it)
        })
    
        marketOrderViewModel.viewState.observe(this, Observer {
            exchange_market_view?.updateState(it)
        })

        marketOrderViewModel.sendHintInfo.observe(this, Observer {
            exchange_market_view?.updateSendHint(it)
        })
    
        marketOrderViewModel.messageEvent.observe(this, Observer {
            ToastHelper.showInfoMessage("Coins unlock and fill started")
        })
    
        marketOrderViewModel.successEvent.observe(this, Observer {
            TransactionSentDialog.open(childFragmentManager, it)
        })
    
        marketOrderViewModel.exchangePrice.observe(this, Observer {
            val info = "Price per token: ${it.toDisplayFormat()}" + if (it == BigDecimal.ZERO) {
                "\nOrderbook is empty"
            } else { "" }
        
            exchange_info?.text = info
        })

        marketOrderViewModel.errorEvent.observe(this, errorObserver)
        marketOrderViewModel.exchangeEnabled.observe(this, exchangeEnableObserver)
        marketOrderViewModel.confirmEvent.observe(this, confirmObserver)
        marketOrderViewModel.showProcessingEvent.observe(this, processingObserver)
        marketOrderViewModel.processingDismissEvent.observe(this, processingDismissObserver)
        marketOrderViewModel.focusExchangeEvent.observe(this, Observer {
            exchange_pager.currentItem = 0
        })
    }

    private fun initLimitViewModel() {
        activity?.let {
            limitOrderViewModel = ViewModelProviders.of(it).get(LimitOrderViewModel::class.java)
        } ?: return

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

        limitOrderViewModel.receiveAmount.observe(this, Observer {
            exchange_limit_view?.updateTotal(it)
        })

        limitOrderViewModel.sendHintInfo.observe(this, Observer {
            exchange_limit_view?.updateSendInfo(it)
        })
    
        limitOrderViewModel.messageEvent.observe(this, Observer {
            ToastHelper.showSuccessMessage(it)
        })
    
        limitOrderViewModel.successEvent.observe(this, Observer {
            TransactionSentDialog.open(childFragmentManager, it)
        })
    
        limitOrderViewModel.exchangeEnabled.observe(this, exchangeEnableObserver)
    
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

        limitOrderViewModel.errorEvent.observe(this, errorObserver)
        limitOrderViewModel.showProcessingEvent.observe(this, processingObserver)
        limitOrderViewModel.processingDismissEvent.observe(this, processingDismissObserver)
        limitOrderViewModel.focusExchangeEvent.observe(this, Observer {
            exchange_pager.currentItem = 1
        })
    }
    
    //endregion

    override fun onFocused() {
        Handler().post {
            fragment_exchange_container?.visible = true

            when(activeType) {
                MARKET -> market_amount_input
                LIMIT -> limit_amount_input
            }?.requestFocus()
        }
    }

    override fun onItemClick(item: NumPadItem) {
        val inputType = getInputField()
        
        val inputField = when(inputType) {
            MARKET_AMOUNT -> market_amount_input
            MARKET_RECEIVE_AMOUNT -> market_receive_input
            LIMIT_AMOUNT -> limit_amount_input
            LIMIT_PRICE -> limit_price_input
        }
        
        val inputConnection = when(inputType) {
            MARKET_AMOUNT -> exchange_market_view?.sendInputConnection
            MARKET_RECEIVE_AMOUNT -> exchange_market_view?.receiveInputConnection
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

    override fun onItemLongClick(item: NumPadItem) {
        val inputField = when(getInputField()) {
            MARKET_AMOUNT -> market_amount_input
            MARKET_RECEIVE_AMOUNT -> market_receive_input
            LIMIT_AMOUNT -> limit_amount_input
            LIMIT_PRICE -> limit_price_input
        }

        when (item.type) {
            NumPadItemType.DELETE -> inputField?.setText("", TextView.BufferType.EDITABLE)
        }
    }

    private fun getInputField(): InputField = when(currentFocus?.id) {
        R.id.market_amount_input -> MARKET_AMOUNT
        R.id.market_receive_input -> MARKET_RECEIVE_AMOUNT
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
        MARKET_RECEIVE_AMOUNT,
        LIMIT_AMOUNT,
        LIMIT_PRICE
    }

    enum class ExchangeType {
        MARKET,
        LIMIT
    }
}
