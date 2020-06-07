package com.nitiaayog.apnesaathi.utility

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkProvider {

    fun isConnected(context: Context): Boolean {
        var result = false
        val cManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cManager?.run {
                cManager.getNetworkCapabilities(cManager.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            cManager?.run {
                cManager.activeNetworkInfo?.run {
                    if ((type == ConnectivityManager.TYPE_WIFI))
                        result = true
                    else if (type == ConnectivityManager.TYPE_MOBILE)
                        result = true
                }
            }
        }
        return result
    }
}