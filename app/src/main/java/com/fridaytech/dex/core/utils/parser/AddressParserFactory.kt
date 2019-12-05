package com.fridaytech.dex.core.utils.parser

import com.fridaytech.dex.core.model.Coin
import com.fridaytech.dex.core.model.CoinType

class AddressParserFactory {

    fun getParser(coin: Coin): IAddressParser = when (coin.type) {
        is CoinType.Ethereum, is CoinType.Erc20 -> AddressParser("ethereum", true)
    }

}