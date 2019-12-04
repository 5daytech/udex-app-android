package com.fridaytech.dex.utils

import com.fridaytech.dex.App
import com.fridaytech.dex.R

object ThemeHelper {
    private val selectedThemePosition: Int
        get() = App.appPreferences.selectedTheme

    fun isLightTheme(): Boolean = when (selectedThemePosition) {
        1 -> true

        else -> false
    }

    fun getActivityTheme(): Int = when (selectedThemePosition) {
        1 -> R.style.DarkMode

        2 -> R.style.LightMode

        else -> R.style.GoldMode
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
