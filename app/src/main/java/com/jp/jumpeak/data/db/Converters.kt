package com.jp.jumpeak.data.db

import androidx.room.TypeConverter
import com.jp.jumpeak.enums.PartiesType
import com.jp.jumpeak.enums.TransactionType

class Converters {
    @TypeConverter
    fun fromPartiesType(value: PartiesType): String {
        return value.name
    }

    @TypeConverter
    fun toPartiesType(value: String): PartiesType {
        return PartiesType.valueOf(value)
    }

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}