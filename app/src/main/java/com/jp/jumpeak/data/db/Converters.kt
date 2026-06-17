package com.jp.jumpeak.data.db

import androidx.room.TypeConverter
import com.jp.jumpeak.enums.Currency

class Converters {
    @TypeConverter
    fun fromCurrency(value:Currency):String{
        return value.name
    }

    @TypeConverter
    fun toCurrency(value:String):Currency{
        return Currency.valueOf(value)
    }
}