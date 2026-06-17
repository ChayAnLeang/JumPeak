package com.jp.jumpeak.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "customers",
    indices = [
        Index(value = ["full_name"]),
        Index(value = ["phone_number"], unique = true)
    ]
)
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "full_name")
    val fullName: String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    @ColumnInfo(name = "address")
    val address: String
)