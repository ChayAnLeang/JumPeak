package com.jp.jumpeak.util.objects

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtil {
    fun format(dateTime: Long): String{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy, hh : mm a", Locale.getDefault())
        return dateFormat.format(dateTime)
    }

    fun toDate(value: String): Date{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy, hh : mm a", Locale.getDefault())
        return dateFormat.parse(value)
    }
}