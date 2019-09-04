package com.blocksdecoded.dex.core.model

import androidx.annotation.StringRes
import java.io.Serializable

data class Coin(
    val title: String,
    val code: String,
    val type: CoinType,
    @StringRes val shortInfoRes: Int? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (other is Coin) {
            return title == other.title && code == other.code
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return title.hashCode() * 31 + code.hashCode()
    }
}