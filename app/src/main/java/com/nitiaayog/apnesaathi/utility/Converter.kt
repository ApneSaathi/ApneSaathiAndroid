package com.nitiaayog.apnesaathi.utility

import androidx.room.TypeConverter
import com.google.gson.Gson

object Converter {
    @TypeConverter
    @JvmStatic
    fun listToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    @JvmStatic
    fun jsonToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()
}