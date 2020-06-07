package com.nitiaayog.apnesaathi.base.extensions

import android.content.SharedPreferences

fun SharedPreferences.putInt(key: String, value: Int) = edit().putInt(key, value).apply()

fun SharedPreferences.putString(key: String, value: String) = edit().putString(key, value).apply()

fun SharedPreferences.putLong(key: String, value: Long) = edit().putLong(key, value).apply()

fun SharedPreferences.putFloat(key: String, value: Float) = edit().putFloat(key, value).apply()

fun SharedPreferences.putBoolean(key: String, value: Boolean) =
    edit().putBoolean(key, value).apply()

fun SharedPreferences.getInt(key: String) = getInt(key, -1)

fun SharedPreferences.getString(key: String) = getString(key, "")

fun SharedPreferences.getFloat(key: String) = getFloat(key, 0F)

fun SharedPreferences.getLong(key: String) = getLong(key, 0L)

fun SharedPreferences.getBoolean(key: String) = getBoolean(key, false)
