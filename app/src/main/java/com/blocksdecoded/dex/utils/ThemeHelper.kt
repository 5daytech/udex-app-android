package com.blocksdecoded.dex.utils

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R

object ThemeHelper {
    private val selectedThemePosition: Int
        get() = App.appPreferences.selectedTheme

    fun isLightTheme(): Boolean = when (selectedThemePosition) {
        1 -> true

        else -> false
    }

    fun getActivityTheme(): Int = when (selectedThemePosition) {
        1 -> R.style.AppTheme_DarkMode

        2 -> R.style.AppTheme_LightMode

        else -> R.style.AppTheme_GoldMode
    }

    fun getBottomDialogTheme(): Int = when (selectedThemePosition) {
        0 -> R.style.DarkBottomSheet

        else -> R.style.LightBottomSheet
    }

    fun getFloatingDialogTheme(): Int = when (selectedThemePosition) {
        0 -> R.style.DarkFloatingDialog

        else -> R.style.LightFloatingDialog
    }
}
