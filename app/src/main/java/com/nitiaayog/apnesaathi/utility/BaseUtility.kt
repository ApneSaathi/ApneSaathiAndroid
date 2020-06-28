package com.nitiaayog.apnesaathi.utility

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.nitiaayog.apnesaathi.R

object BaseUtility {

    fun validateEmailFormat(emailId: String) = Patterns.EMAIL_ADDRESS.matcher(emailId).matches()

    fun navigateToUrl(context: Context, url: String) =
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            this.data = Uri.parse(url)
        })

    fun showAlertMessage(
        context: Context, @StringRes title: Int, @StringRes message: Int,
        @StringRes positiveTitle: Int = R.string.okay
    ) = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog).setTitle(title)
        .setMessage(message).setPositiveButton(positiveTitle) { dialog, _ -> dialog.dismiss() }
        .show()

    fun showAlertMessage(
        context: Context, @StringRes title: Int, @StringRes message: Int,
        @StringRes positiveTitle: Int = R.string.okay,
        onPositiveBtnClick: (dialog: DialogInterface, which: Int) -> Unit
    ) = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog).setTitle(title)
        .setMessage(message).setPositiveButton(positiveTitle, onPositiveBtnClick).show()

    fun showAlertMessage(
        context: Context, title: String, message: String, positiveTitle: String
    ) = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog).setTitle(title)
        .setMessage(message).setPositiveButton(positiveTitle) { dialog, _ -> dialog.dismiss() }
        .show()

    fun showAlertMessage(
        context: Context, title: String, message: String, positiveTitle: String,
        onPositiveBtnClick: (dialog: DialogInterface, which: Int) -> Unit
    ) = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog).setTitle(title)
        .setMessage(message).setPositiveButton(positiveTitle, onPositiveBtnClick).show()

    fun showAlertMessage(
        context: Context, @StringRes title: Int, @StringRes message: Int,
        @StringRes positiveTitle: Int = R.string.yes, @StringRes negativeTitle: Int = R.string.no
    ) = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog).setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveTitle) { dialog, _ -> dialog.dismiss() }
        .setNegativeButton(negativeTitle) { dialog, _ -> dialog.dismiss() }
        .show()

    fun showAlertMessage(
        context: Context, @StringRes title: Int, @StringRes message: Int,
        @StringRes positiveTitle: Int = R.string.yes, @StringRes negativeTitle: Int = R.string.no,
        onPositiveBtnClick: (dialog: DialogInterface, which: Int) -> Unit,
        onNegativeBtnClick: (dialog: DialogInterface, which: Int) -> Unit
    ) = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog).setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveTitle, onPositiveBtnClick)
        .setNegativeButton(negativeTitle, onNegativeBtnClick)
        .show()

    fun showAlertMessage(
        context: Context, title: String, message: String, positiveTitle: String,
        negativeTitle: String
    ) = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog).setTitle(title)
        .setMessage(message).setPositiveButton(positiveTitle) { dialog, _ -> dialog.dismiss() }
        .setNegativeButton(negativeTitle) { dialog, _ -> dialog.dismiss() }
        .show()

    fun showAlertMessage(
        context: Context, title: String, message: String, positiveTitle: String,
        negativeTitle: String, onPositiveBtnClick: (dialog: DialogInterface, which: Int) -> Unit,
        onNegativeBtnClick: (dialog: DialogInterface, which: Int) -> Unit
    ) = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog).setTitle(title)
        .setMessage(message).setPositiveButton(positiveTitle, onPositiveBtnClick)
        .setNegativeButton(negativeTitle, onNegativeBtnClick).show()
}