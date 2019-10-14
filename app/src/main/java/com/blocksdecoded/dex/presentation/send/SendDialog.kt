package com.blocksdecoded.dex.presentation.send

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog
import com.blocksdecoded.dex.presentation.widgets.NumPadItem
import com.blocksdecoded.dex.presentation.widgets.NumPadItemType
import com.blocksdecoded.dex.presentation.widgets.NumPadItemsAdapter
import com.blocksdecoded.dex.presentation.widgets.click.setSingleClickListener
import com.blocksdecoded.dex.core.ui.reObserve
import com.blocksdecoded.dex.presentation.models.AmountInfo
import com.blocksdecoded.dex.presentation.send.confirm.SendConfirmDialog
import com.blocksdecoded.dex.presentation.send.model.ReceiveAddressInfo
import com.blocksdecoded.dex.utils.subscribeToInput
import com.blocksdecoded.dex.utils.ui.ToastHelper
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_send.*
import kotlinx.android.synthetic.main.view_amount_input.*
import java.math.BigDecimal

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

    private val addressObserver = Observer<ReceiveAddressInfo> {
        send_address.updateInput(it.address ?: "", it.error)
    }

    private val barcodeObserver = Observer<Unit> { startScanner() }

    private val sendEnabledObserver = Observer<Boolean> {
        send_confirm?.isEnabled = it
    }

    private val infoObserver = Observer<AmountInfo> { info ->
        context?.let {
            send_amount?.updateHint(info)
        }
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
        send_coin_name?.text = coin.title
        send_coin_icon?.bind(coinCode)
        send_amount?.updateAmountPrefix(coin.code)
    }

    //endregion

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(SendViewModel::class.java)
            viewModel.init(coinCode)
        }

        disposable = amountChangeSubject.subscribeToInput {
            viewModel.onAmountChanged(it)
        }
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

        inputConnection = amount_input?.bind ( onChange = { amount ->
                send_amount?.setMaxBtnVisible(amount <= BigDecimal.ZERO)
                amountChangeSubject.onNext(amount)
            }, decimalProvider = { viewModel.decimalSize })

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

    override fun onItemLongClick(item: NumPadItem) {
        when (item.type) {
            NumPadItemType.DELETE -> amount_input?.setText("", TextView.BufferType.EDITABLE)
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