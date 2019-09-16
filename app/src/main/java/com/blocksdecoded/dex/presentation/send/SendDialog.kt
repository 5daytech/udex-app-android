package com.blocksdecoded.dex.presentation.send

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog
import com.blocksdecoded.dex.presentation.widgets.NumPadItem
import com.blocksdecoded.dex.presentation.widgets.NumPadItemType
import com.blocksdecoded.dex.presentation.widgets.NumPadItemsAdapter
import com.blocksdecoded.dex.presentation.widgets.listeners.SimpleTextWatcher
import com.blocksdecoded.dex.presentation.widgets.click.setSingleClickListener
import com.blocksdecoded.dex.core.ui.reObserve
import com.blocksdecoded.dex.utils.ui.ToastHelper
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_send.*
import kotlinx.android.synthetic.main.view_amount_input.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

class SendDialog private constructor()
    : BaseBottomDialog(R.layout.dialog_send), NumPadItemsAdapter.Listener {

    private lateinit var viewModel: SendViewModel
    private var coinCode: String = ""
    private var inputConnection: InputConnection? = null
    private val amountChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()
    private var disposable: Disposable? = null

    //region Observers
    
    private val messageObserver = Observer<Int?> { error ->  error?.let { ToastHelper.showErrorMessage(it) } }

    private val dismissObserver = Observer<Unit> { dismiss() }

    private val dismissWithSuccessObserver = Observer<Unit> {
        ToastHelper.showSuccessMessage(R.string.send_success)
        dismiss()
    }

    private val confirmObserver = Observer<SendConfirmDialog.SendConfirmData> {
        SendConfirmDialog.show(childFragmentManager, it)
    }

    private val addressObserver = Observer<String> { send_address.updateInput(it) }

    private val barcodeObserver = Observer<Unit> { startScanner() }

    private val sendEnabledObserver = Observer<Boolean> {
        send_confirm?.isEnabled = it
    }

    private val infoObserver = Observer<SendInfo> { info ->
        context?.let {
            amount_input?.setTextColor(ContextCompat.getColor(it, if (info.error) R.color.red else R.color.main_light_text))
        }

        amount_hint?.text = "You send $${info.fiatAmount.toFiatDisplayFormat()}"
    }

    private val amountObserver = Observer<BigDecimal> { amount ->
        if (amount > BigDecimal.ZERO) {
            amount_input?.setText(amount.stripTrailingZeros().toPlainString())
            amount_input?.setSelection(amount_input?.text?.length ?: 0)
        } else {
            amount_input?.setText("")
        }
    }
    
    private val coinObserver = Observer<Coin> { coin ->
        send_title?.text = "Send ${coin.title}"
        send_coin_icon?.bind(coinCode)
        send_amount?.updateAmountPrefix(coin.code)
    }

    private val amountChangeListener = object: SimpleTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            val amountText = s?.toString() ?: ""
            var amountNumber = when {
                amountText != "" -> amountText.toBigDecimalOrNull() ?: BigDecimal.ZERO
                else -> BigDecimal.ZERO
            }

            viewModel.decimalSize?.let {
                if (amountNumber.scale() > it) {
                    amountNumber = amountNumber.setScale(it, RoundingMode.FLOOR)
                    val newString = amountNumber.toPlainString()
                    amount_input?.setText(newString)
                    amount_input?.setSelection(newString.length)
                }
            }

            send_amount?.setMaxBtnVisible(amountText.isEmpty())
            amountChangeSubject.onNext(amountNumber)
        }
    }

    //endregion

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(SendViewModel::class.java)
            viewModel.init(coinCode)
        }

        disposable = amountChangeSubject.debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.onAmountChanged(it) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.dismissEvent.reObserve(this, dismissObserver)
        viewModel.dismissWithSuccessEvent.reObserve(this, dismissWithSuccessObserver)
        viewModel.messageEvent.reObserve(this, messageObserver)
        viewModel.receiveAddress.reObserve(this, addressObserver)
        viewModel.sendEnabled.reObserve(this, sendEnabledObserver)
        viewModel.amount.reObserve(this, amountObserver)
        viewModel.coin.reObserve(this, coinObserver)
        viewModel.openBarcodeScannerEvent.reObserve(this, barcodeObserver)
        viewModel.sendInfo.reObserve(this, infoObserver)
        viewModel.confirmEvent.reObserve(this, confirmObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        send_amount?.bindInitial( onMaxClick = {
            viewModel.onMaxClicked()
        }, onSwitchClick = {
            viewModel.onSwitchClick()
        })

        send_address?.bindAddressInputInitial(
                onBarcodeClick = viewModel::onBarcodeClick,
                onPasteClick = viewModel::onPasteClick,
                onDeleteClick = viewModel::onDeleteAddressClick
        )

        send_numpad?.bind(this, NumPadItemType.DOT, false)

        amount_input?.addTextChangedListener(amountChangeListener)
        amount_input?.showSoftInputOnFocus = false
        inputConnection = amount_input?.onCreateInputConnection(EditorInfo())
        focusInput()

        send_confirm?.setSingleClickListener { viewModel.onSendClicked() }
    }

    //endregion

    private fun focusInput() {
        amount_input?.requestFocus()
    }

    private fun startScanner() {
        activity?.let {
            QRScannerActivity.start(it)
        }
    }

    override fun onItemClick(item: NumPadItem) {
        when (item.type) {
            NumPadItemType.NUMBER -> inputConnection?.commitText(item.number.toString(), 1)
            NumPadItemType.DELETE -> inputConnection?.deleteSurroundingText(1, 0)
            NumPadItemType.DOT -> {
                if (amount_input?.text?.toString()?.contains(".") != true) {
                    inputConnection?.commitText(".", 1)
                }
            }
        }
    }

    companion object {
        fun open(fm: FragmentManager, coinCode: String) {
            val fragment = SendDialog()

            fragment.coinCode = coinCode

            fragment.show(fm, "send")
        }
    }
}