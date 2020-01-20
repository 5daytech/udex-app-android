package com.fridaytech.dex.utils.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat

object SocialUtils {

    private const val TELEGRAM_LINK = "https://t.me/udexapp"
    private const val TELEGRAM_BOT_LINK = "https://t.me/udex_bot"

    fun openTwitter(activity: Activity?) {
        val intent: Intent = try {
            // Check if the Twitter app is installed on the phone.
            activity?.packageManager?.getPackageInfo("com.twitter.android", 0)

            Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=1192409942713606144"))
        } catch (e: Exception) {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/udexapp"))
        }
        activity?.startActivity(intent)
    }

    fun openTelegram(activity: Activity?) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(TELEGRAM_LINK)
        )
        activity?.startActivity(intent)
    }

    fun openTelegramBot(activity: Activity?) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(TELEGRAM_BOT_LINK)
        )
        activity?.startActivity(intent)
    }

    fun shareMessage(activity: Activity?, message: String) {
        ShareCompat.IntentBuilder.from(activity)
            .setType("text/plain")
            .setText(message)
            .startChooser()
    }
}
