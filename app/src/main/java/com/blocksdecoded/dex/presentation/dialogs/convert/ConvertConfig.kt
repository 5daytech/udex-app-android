package com.blocksdecoded.dex.presentation.dialogs.convert

data class ConvertConfig(
    val coinCode: String,
    val type: ConvertType
) {
    enum class ConvertType {
        WRAP,
        UNWRAP
    }
}