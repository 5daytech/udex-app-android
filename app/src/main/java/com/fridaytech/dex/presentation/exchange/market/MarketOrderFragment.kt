package com.fridaytech.dex.presentation.exchange.market

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.CoreFragment
import com.fridaytech.dex.presentation.common.ProcessingDialog
import com.fridaytech.dex.presentation.common.TransactionSentDialog
import com.fridaytech.dex.presentation.exchange.confirm.ExchangeConfirmDialog
import com.fridaytech.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.fridaytech.dex.presentation.exchange.market.MarketOrderFragment.InputField.MARKET_AMOUNT
import com.fridaytech.dex.presentation.exchange.market.MarketOrderFragment.InputField.MARKET_RECEIVE_AMOUNT
import com.fridaytech.dex.presentation.main.IFocusListener
import com.fridaytech.dex.presentation.widgets.MainToolbar
import com.fridaytech.dex.presentation.widgets.NumPadItem
import com.fridaytech.dex.presentation.widgets.NumPadItemType
import com.fridaytech.dex.presentation.widgets.NumPadItemsAdapter
import com.fridaytech.dex.presentation.widgets.click.setSingleClickListener
import com.fridaytech.dex.utils.currentFocus
import com.fridaytech.dex.utils.rx.subscribeToInput
import com.fridaytech.dex.utils.ui.ToastHelper
import com.fridaytech.dex.utils.ui.toDisplayFormat
import com.fridaytech.dex.utils.visible
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_market_exchange.*
import kotlinx.android.synthetic.main.view_market_order.*
import java.math.BigDecimal

class MarketOrderFragment : CoreFragment(R.layout.fragment_market_exchange),
    NumPadItemsAdapter.Listener,
    IFocusListener {

    private lateinit var marketOrderViewModel: MarketOrderViewModel

    private val disposables = CompositeDisposable()
    private var processingDialog: DialogFragment? = null

    //region Observers

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

    private val successObserver = Observer<Int> {
        ToastHelper.showSuccessMessage(it)
    }

    private val messageObserver = Observer<Int> {
        ToastHelper.showInfoMessage(it)
    }

    private val transactionSentObserver = Observer<String> {
        TransactionSentDialog.show(childFragmentManager, it)
    }

    private val exchangePriceObserver = Observer<BigDecimal> {
        val info = "Price per token: ${it.toDisplayFormat()}" + if (it == BigDecimal.ZERO) {
            "\nOrderbook is empty"
        } else { "" }

        exchange_info?.text = info
    }

    //endregion

    //region Lifecycle

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initMarketViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.bind(leftActionButton = MainToolbar.ActionInfo(
            R.drawable.ic_market_buy,
            0,
            0
        ) {})

        exchange_numpad?.bind(this, NumPadItemType.DOT, false, scrollable = true)

        exchange_confirm?.setSingleClickListener {
            marketOrderViewModel.onExchangeClick()
        }

        // Market view
        exchange_market_view?.bind(
            onMaxClick = { marketOrderViewModel.onMaxClick() },
            onSendCoinPick = {
                marketOrderViewModel.onSendCoinPick(it)
            },
            onReceiveCoinPick = {
                marketOrderViewModel.onReceiveCoinPick(it)
            },
            onSwitchClick = {
                marketOrderViewModel.onSwitchClick()
            }
        )

        exchange_market_view?.sendAmountChangeSubject?.subscribeToInput {
            marketOrderViewModel.onSendAmountChange(it)
        }?.let { disposables.add(it) }

        exchange_market_view?.receiveAmountChangeSubject?.subscribeToInput {
            marketOrderViewModel.onReceiveAmountChange(it)
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

        marketOrderViewModel.sendAvailableAmount.observe(this, Observer {
            exchange_market_view?.updateSendAvailableAmount(it)
        })

        marketOrderViewModel.receiveAvailableAmount.observe(this, Observer {
            exchange_market_view?.updateReceiveAvailableAmount(it)
        })

        marketOrderViewModel.viewState.observe(this, Observer {
            exchange_market_view?.updateState(it)
        })

        marketOrderViewModel.sendHintInfo.observe(this, Observer {
            exchange_market_view?.updateSendHint(it)
        })

        marketOrderViewModel.messageEvent.observe(this, Observer {
            ToastHelper.showInfoMessage(R.string.message_exchange_in_progress)
        })

        marketOrderViewModel.exchangePrice.observe(this, exchangePriceObserver)
        marketOrderViewModel.transactionsSentEvent.observe(this, transactionSentObserver)
        marketOrderViewModel.errorEvent.observe(this, errorObserver)
        marketOrderViewModel.exchangeEnabled.observe(this, exchangeEnableObserver)
        marketOrderViewModel.confirmEvent.observe(this, confirmObserver)
        marketOrderViewModel.showProcessingEvent.observe(this, processingObserver)
        marketOrderViewModel.processingDismissEvent.observe(this, processingDismissObserver)
    }

    //endregion

    override fun onFocused() {
        Handler().post {
            fragment_exchange_container?.visible = true

            market_amount_input?.requestFocus()
        }
    }

    override fun onItemClick(item: NumPadItem) {
        val inputType = getInputField()

        val inputField = when (inputType) {
            MARKET_AMOUNT -> market_amount_input
            MARKET_RECEIVE_AMOUNT -> market_receive_input
        }

        val inputConnection = when (inputType) {
            MARKET_AMOUNT -> exchange_market_view?.sendInputConnection
            MARKET_RECEIVE_AMOUNT -> exchange_market_view?.receiveInputConnection
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
        val inputField = when (getInputField()) {
            MARKET_AMOUNT -> market_amount_input
            MARKET_RECEIVE_AMOUNT -> market_receive_input
        }

        when (item.type) {
            NumPadItemType.DELETE -> inputField?.setText("", TextView.BufferType.EDITABLE)
        }
    }

    private fun getInputField(): InputField = when (currentFocus?.id) {
        R.id.market_amount_input -> MARKET_AMOUNT
        R.id.market_receive_input -> MARKET_RECEIVE_AMOUNT
        else -> MARKET_AMOUNT
    }

    companion object {
        fun newInstance() = MarketOrderFragment()
    }

    enum class InputField {
        MARKET_AMOUNT,
        MARKET_RECEIVE_AMOUNT
    }
}