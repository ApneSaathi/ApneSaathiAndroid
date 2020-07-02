package com.nitiaayog.apnesaathi.database.converters

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

object DateTimeConverter {

    private const val FORMAT_SERVER_DATE_TIME: String = "yyyy-MM-dd'T'HH:mm:ss"
    private const val FORMAT_LOCAL_DATE_TIME: String = "yyyy-MM-dd HH:mm:ss"

    private val localDateFormatter by lazy {
        SimpleDateFormat(FORMAT_LOCAL_DATE_TIME, Locale.ENGLISH)
    }

    private val serverDateFormatter by lazy {
        SimpleDateFormat(FORMAT_SERVER_DATE_TIME, Locale.ENGLISH)
    }

    @TypeConverter
    fun toDateTime(value: String?): Date? = if (!value.isNullOrBlank()) {
        val serverDate: Date? = serverDateFormatter.parse(value)
        if (serverDate != null) {
            val localDateFormat: String = localDateFormatter.format(serverDate)
            println("TAG -- MyData -- ServerToLocal --> $localDateFormat")
            localDateFormatter.parse(localDateFormat)
        } else Date(System.currentTimeMillis())
    } else Date(System.currentTimeMillis())

    @TypeConverter
    fun fromDateTime(dateTime: Date?): String? {
        return if (dateTime != null) {
            val serverDate: String = serverDateFormatter.format(dateTime)
            println("TAG -- MyData -- LocalToServer --> $serverDate")
            serverDate
        } else {
            val localDate = localDateFormatter.format(Date(System.currentTimeMillis()))
            println("TAG -- MyData -- LocalToServer --> $localDate")
            localDate
        }
    }
}