package com.blocksdecoded.dex.core.manager

class AppConfiguration(
        val networkType: EtherNetwork = EtherNetwork.Kovan,
        val infuraKey: String = "57b6615fb10b4749a54b29c2894a00df",
        val etherscanKey: String = "GKNHXT22ED7PRVCKZATFZQD1YI7FK9AAYE"
) {
    val etherScanUrl: String
        get() {
            return when (networkType) {
                EtherNetwork.MainNet -> "https://api.etherscan.io"
                EtherNetwork.Ropsten -> "https://api-ropsten.etherscan.io"
                EtherNetwork.Kovan -> "https://api-kovan.etherscan.io"
                EtherNetwork.Rinkeby -> "https://api-rinkeby.etherscan.io"
            }
        }

    private val subDomain = when (networkType) {
        EtherNetwork.MainNet -> "mainnet"
        EtherNetwork.Kovan -> "kovan"
        EtherNetwork.Rinkeby -> "rinkeby"
        EtherNetwork.Ropsten -> "ropsten"
    }

    val infuraUrl: String = "https://$subDomain.infura.io/$infuraKey"

    companion object {
        val DEFAULT = AppConfiguration()
    }

    enum class EtherNetwork { MainNet, Ropsten, Kovan, Rinkeby }
}