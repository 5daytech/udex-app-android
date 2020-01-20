package com.fridaytech.dex.presentation.exchange.limit

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
import com.fridaytech.dex.presentation.exchange.limit.LimitOrderFragment.InputField.LIMIT_AMOUNT
import com.fridaytech.dex.presentation.exchange.limit.LimitOrderFragment.InputField.LIMIT_PRICE
import com.fridaytech.dex.presentation.main.IFocusListener
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
import kotlinx.android.synthetic.main.fragment_limit_exchange.*
import kotlinx.android.synthetic.main.view_limit_order.*
import java.math.BigDecimal

class LimitOrderFragment : CoreFragment(R.layout.fragment_limit_exchange),
    NumPadItemsAdapter.Listener,
    IFocusListener {

    private lateinit var limitOrderViewModel: LimitOrderViewModel

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

        initLimitViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exchange_numpad?.bind(this, NumPadItemType.DOT, false, scrollable = true)

        exchange_confirm?.setSingleClickListener {
            limitOrderViewModel.onExchangeClick()
        }

        // Limit view
        exchange_limit_view?.bind(
            onMaxClick = { limitOrderViewModel.onMaxClick() },
            onSendCoinPick = {
                limitOrderViewModel.onSendCoinPick(it)
            },
            onReceiveCoinPick = {
                limitOrderViewModel.onReceiveCoinPick(it)
            },
            onSwitchClick = {
                limitOrderViewModel.onSwitchClick()
            }
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

        limitOrderViewModel.averagePrice.observe(this, Observer {
            exchange_limit_view?.updateAveragePrice(it)
        })

        limitOrderViewModel.exchangePrice.observe(this, exchangePriceObserver)
        limitOrderViewModel.transactionsSentEvent.observe(this, transactionSentObserver)
        limitOrderViewModel.exchangeEnabled.observe(this, exchangeEnableObserver)
        limitOrderViewModel.confirmEvent.observe(this, confirmObserver)
        limitOrderViewModel.messageEvent.observe(this, messageObserver)
        limitOrderViewModel.successEvent.observe(this, successObserver)
        limitOrderViewModel.errorEvent.observe(this, errorObserver)
        limitOrderViewModel.showProcessingEvent.observe(this, processingObserver)
        limitOrderViewModel.processingDismissEvent.observe(this, processingDismissObserver)
    }

    //endregion

    override fun onFocused() {
        Handler().post {
            fragment_exchange_container?.visible = true

            limit_amount_input?.requestFocus()
        }
    }

    override fun onItemClick(item: NumPadItem) {
        val inputType = getInputField()

        val inputField = when (inputType) {
            LIMIT_AMOUNT -> limit_amount_input
            LIMIT_PRICE -> limit_price_input
        }

        val inputConnection = when (inputType) {
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
        val inputField = when (getInputField()) {
            LIMIT_AMOUNT -> limit_amount_input
            LIMIT_PRICE -> limit_price_input
        }

        when (item.type) {
            NumPadItemType.DELETE -> inputField?.setText("", TextView.BufferType.EDITABLE)
        }
    }

    private fun getInputField(): InputField = when (currentFocus?.id) {
        R.id.limit_amount_input -> LIMIT_AMOUNT
        R.id.limit_price_input -> LIMIT_PRICE
        else -> LIMIT_AMOUNT
    }

    companion object {
        fun newInstance() = LimitOrderFragment()
    }

    enum class InputField {
        LIMIT_AMOUNT,
        LIMIT_PRICE
    }
}