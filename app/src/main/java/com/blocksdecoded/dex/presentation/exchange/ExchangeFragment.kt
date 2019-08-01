package com.blocksdecoded.dex.presentation.exchange

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.widgets.NumPadItem
import com.blocksdecoded.dex.presentation.widgets.NumPadItemType
import com.blocksdecoded.dex.presentation.widgets.NumPadItemsAdapter
import com.blocksdecoded.dex.presentation.widgets.NumPadView
import kotlinx.android.synthetic.main.fragment_exchange.*
import kotlinx.android.synthetic.main.view_amount_input.*

class ExchangeFragment : CoreFragment(R.layout.fragment_exchange), NumPadItemsAdapter.Listener {

    private lateinit var viewModel: ExchangeViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ExchangeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exchange_numpad?.bind(this, NumPadItemType.DOT, false)
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
