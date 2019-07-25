package com.blocksdecoded.dex.presentation.transactions

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.ui.CoreFragment

class TransactionsFragment : CoreFragment(R.layout.fragment_transactions) {

    private lateinit var viewModel: TransactionsViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TransactionsViewModel::class.java)
    }

    companion object {
        fun newInstance() = TransactionsFragment()
    }

}
