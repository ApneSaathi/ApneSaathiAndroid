package com.nitiaayog.apnesaathi.utility

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.nitiaayog.apnesaathi.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object BaseUtility {

    const val FORMAT_SERVER_DATE_TIME: String = "yyyy-MM-dd'T'HH:mm:ss"
    const val FORMAT_LOCAL_DATE_TIME: String = "yyyy-MM-dd HH:mm:ss"

    private const val ALLOWED_NUMBERS = "0123456789"
    private const val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"

    fun validateEmailFormat(emailId: String) = Patterns.EMAIL_ADDRESS.matcher(emailId).matches()

    fun validatePhoneNumber(phoneNumber: String) = Patterns.PHONE.matcher(phoneNumber).matches()

    fun navigateToUrl(context: Context, url: String) =
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            this.data = Uri.parse(url)
        })

    fun dateTimeFormatConversion(date: String, inputFormat: String): Date {
        try {
            val inputFormatter: DateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
            val outputDate: Date? = inputFormatter.parse(date)
            return outputDate!!
        } catch (e: Exception) {
            println("TAG -- MyData -- ServerToLocal --> ${e.message}")
        }
        return Date(System.currentTimeMillis())
    }

    fun format(date: String, inputFormat: String, outputFormat: String): String {
        val outFormatter: DateFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)
        try {
            val inputFormatter: DateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
            val outputDate: Date? = inputFormatter.parse(date)
            return outFormatter.format(outputDate)
        } catch (e: Exception) {
            println("TAG -- MyData -- ServerToLocal --> ${e.message}")
        }
        return outFormatter.format(Date(System.currentTimeMillis()))
    }

    fun getRandomNumber(numberLength: Int): String {
        val random = Random()
        val sb = StringBuilder(numberLength)
        for (i in 0 until numberLength)
            sb.append(ALLOWED_NUMBERS[random.nextInt(ALLOWED_NUMBERS.length)])
        return sb.toString()
    }

    fun getRandomString(sizeOfRandomString: Int): String {
        val random = Random()
        val sb = StringBuilder(sizeOfRandomString)
        for (i in 0 until sizeOfRandomString)
            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        return sb.toString()
    }

    /*fun fromDateTime(dateTime: Date?): String? {
        return if (dateTime != null) {
            val serverDate: String = DateTimeConverter.serverDateFormatter.format(dateTime)
            println("TAG -- MyData -- LocalToServer --> $serverDate")
            serverDate
        } else {
            val localDate =
                DateTimeConverter.localDateFormatter.format(Date(System.currentTimeMillis()))
            println("TAG -- MyData -- LocalToServer --> $localDate")
            localDate
        }
    }*/

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