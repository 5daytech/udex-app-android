package com.blocksdecoded.dex.presentation.convert.model

import com.blocksdecoded.dex.core.model.EConvertType

data class ConvertConfig(
    val coinCode: String,
    val type: EConvertType
)