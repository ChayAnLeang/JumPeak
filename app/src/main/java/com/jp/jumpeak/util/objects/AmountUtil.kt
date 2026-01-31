package com.jp.jumpeak.util.objects

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object AmountUtil {
    fun format(amount: Double): String{
        val decimalFormat = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))
        return String.format("%s USD",decimalFormat.format(amount))
    }
}