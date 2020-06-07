package com.nitiaayog.apnesaathi.utility

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns

object BaseUtility {

    fun validateEmailFormat(emailId: String) = Patterns.EMAIL_ADDRESS.matcher(emailId).matches()

    fun navigateToUrl(context: Context, url: String) =
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            this.data = Uri.parse(url)
        })
}