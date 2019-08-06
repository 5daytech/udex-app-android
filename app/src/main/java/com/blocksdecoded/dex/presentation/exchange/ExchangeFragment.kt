package com.blocksdecoded.dex.presentation.exchange

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.widgets.NumPadItem
import com.blocksdecoded.dex.presentation.widgets.NumPadItemType
import com.blocksdecoded.dex.presentation.widgets.NumPadItemsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_exchange.*
import kotlinx.android.synthetic.main.view_amount_input.*
import java.util.concurrent.TimeUnit

class ExchangeFragment : CoreFragment(R.layout.fragment_exchange), NumPadItemsAdapter.Listener {

    private lateinit var viewModel: ExchangeViewModel
    private val disposables = CompositeDisposable()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ExchangeViewModel::class.java)
        
        viewModel.sendCoins.observe(this, Observer {
            exchange_view?.updateSendCoins(it)
        })
    
        viewModel.receiveCoins.observe(this, Observer {
            exchange_view?.updateReceiveCoins(it)
        })
        
        viewModel.viewState.observe(this, Observer {
            exchange_view?.updateState(it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exchange_numpad?.bind(this, NumPadItemType.DOT, false)
        exchange_confirm?.setOnClickListener { viewModel.onExchangeClick() }
        
        exchange_view?.bind(
            onMaxClick = { viewModel.onMaxClick() },
            onSendCoinPick = { viewModel.onSendCoinPick(it) },
            onReceiveCoinPick = { viewModel.onReceiveCoinPick(it) },
            onSwitchClick = { viewModel.onSwitchClick() }
        )
        
        exchange_view?.sendAmountChangeSubject?.debounce(300, TimeUnit.MILLISECONDS)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { viewModel.onSendAmountChange(it) }
            ?.let { disposables.add(it) }
    }

    override fun onItemClick(item: NumPadItem) {
        when (item.type) {
            NumPadItemType.NUMBER -> exchange_view?.inputConnection?.commitText(item.number.toString(), 1)
            NumPadItemType.DELETE -> exchange_view?.inputConnection?.deleteSurroundingText(1, 0)
            NumPadItemType.DOT -> {
                if (amount_input?.text?.toString()?.contains(".") != true) {
                    exchange_view?.inputConnection?.commitText(".", 1)
                }
            }
        }
    }

    companion object {
        fun newInstance() = ExchangeFragment()
    }

}
