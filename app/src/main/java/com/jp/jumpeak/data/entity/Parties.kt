package com.jp.jumpeak.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jp.jumpeak.enums.PartiesType

@Entity(
    tableName = "parties",
    indices = [Index(value = ["phone_number","type"], unique = true)]
)
data class Parties(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    val type: PartiesType
)