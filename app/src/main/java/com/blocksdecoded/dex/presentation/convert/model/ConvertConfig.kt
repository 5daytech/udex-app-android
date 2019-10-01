package com.blocksdecoded.dex.presentation.convert.model

data class ConvertConfig(
    val coinCode: String,
    val type: ConvertType
)

enum class ConvertType {
    WRAP,
    UNWRAP
}