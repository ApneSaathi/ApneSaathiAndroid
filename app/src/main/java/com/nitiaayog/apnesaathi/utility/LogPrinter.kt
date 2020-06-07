package com.nitiaayog.apnesaathi.utility

import com.nitiaayog.apnesaathi.BuildConfig

object LogPrinter {

    fun printMessage(tag: String, message: String) {
        if (BuildConfig.DEBUG) println("TAG --- $tag ---> $message")
    }
}