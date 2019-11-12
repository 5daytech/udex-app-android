package com.blocksdecoded.dex.presentation.statschart

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.rx.uiObserve
import io.horizontalsystems.xrateskit.entities.ChartInfo
import io.horizontalsystems.xrateskit.entities.ChartType
import io.horizontalsystems.xrateskit.entities.MarketInfo
import io.reactivex.disposables.Disposable
import java.math.BigDecimal

class ChartInfoViewModel : CoreViewModel() {
    private val coinManager = App.coinManager
    private val ratesManager = App.ratesManager
    private val appPreferences = App.appPreferences

    private lateinit var coinCode: String
    private var chartType = ChartType.DAILY
    private var latestRate: BigDecimal? = null
    private var marketInfo: MarketInfo? = null
    private var chartInfo: ChartInfo? = null
    private var chartDisposable: Disposable? = null

    val coin = MutableLiveData<Coin>()
    val chartData = MutableLiveData<ChartViewItem>()
    val currentPeriod = MutableLiveData<Int>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Int>()

    fun init(coinCode: String) {
        this.coinCode = coinCode
        loading.value = true
        error.value = 0
        chartType = ChartType.fromString(appPreferences.selectedChartPeriod) ?: ChartType.DAILY
        coin.value = coinManager.getCoin(coinCode)
        marketInfo = ratesManager.getMarketInfo(coinCode)
        currentPeriod.value = chartType.ordinal

        ratesManager.getLatestRateSingle(coinCode)
            .uiObserve()
            .subscribe({
                latestRate = it
                showChart()
            }, { Logger.e(it) }
            ).let { disposables.add(it) }

        loadChartType(chartType)
    }

    private fun showChart() {
        if (latestRate != null && chartInfo != null) {
            displayChartInfo()
        }
    }

    private fun displayChartInfo() {
        loading.postValue(false)
        val marketInfo = ratesManager.getMarketInfo(coin.value?.code ?: "")
        chartData.postValue(RateChartViewFactory().createViewItem(chartType, chartInfo, marketInfo))
    }

    private fun loadChartType(type: ChartType) {
        chartInfo = ratesManager.chartInfo(coinCode, type)

        if (chartInfo == null) {
            loading.postValue(true)
            chartDisposable?.dispose()
            chartDisposable = ratesManager.chartInfoObservable(coinCode, type)
                .subscribe({
                    chartInfo = it
                    displayChartInfo()
                    chartDisposable?.dispose()
                    chartDisposable = null
                }, { error.postValue(R.string.chart_loading_error) })
        } else {
            displayChartInfo()
        }
    }

    fun onPeriodSelect(position: Int) {
        val newType = ChartType.values()[position]
        if (chartType != newType) {
            chartType = newType
            currentPeriod.value = newType.ordinal
            appPreferences.selectedChartPeriod = newType.toString()
            loadChartType(newType)
        }
    }
}
