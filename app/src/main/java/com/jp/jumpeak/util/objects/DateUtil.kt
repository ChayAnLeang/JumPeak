package com.jp.jumpeak.util.objects

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtil {
    fun format(dateTime: Long): String{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(dateTime)
    }

    fun toDate(value: String): Date?{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTM")
        return dateFormat.parse(value)
    }
}