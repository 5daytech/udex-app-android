package com.fridaytech.dex.presentation.convert.model

import com.fridaytech.dex.core.model.EConvertType

data class ConvertConfig(
    val coinCode: String,
    val type: EConvertType
)
