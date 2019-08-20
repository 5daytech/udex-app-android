package com.blocksdecoded.dex.presentation.markets

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.markets.recycler.MarketViewHolder
import com.blocksdecoded.dex.presentation.markets.recycler.MarketsAdapter
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import com.blocksdecoded.dex.presentation.widgets.MainToolbar.ToolbarState.*
import kotlinx.android.synthetic.main.fragment_markets.*

class MarketsFragment : CoreFragment(R.layout.fragment_markets), MarketViewHolder.Listener {

    private lateinit var adapter: MarketsAdapter
    private lateinit var viewModel: MarketsViewModel
    
    //region Lifecycle
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MarketsAdapter(this)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarketsViewModel::class.java)
        
        viewModel.markets.observe(this, Observer { adapter.setMarkets(it) })
        
        viewModel.loading.observe(this, Observer { markets_refresh?.isRefreshing = it })
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        toolbar?.bind(NONE)
        
        markets_recycler?.layoutManager = LinearLayoutManager(context)
        markets_recycler?.adapter = adapter
        
        markets_refresh?.setOnRefreshListener { viewModel.refresh() }
    }
    
    //endregion
    
    //region ViewHolder
    
    override fun onClick(position: Int) {
    
    }
    
    //endregion
    
    companion object {
        fun newInstance() = MarketsFragment()
    }

}
