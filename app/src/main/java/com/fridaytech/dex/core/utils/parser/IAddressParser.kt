package com.fridaytech.dex.core.utils.parser

import com.fridaytech.dex.core.model.AddressData

interface IAddressParser {
    fun parse(paymentAddress: String): AddressData
}