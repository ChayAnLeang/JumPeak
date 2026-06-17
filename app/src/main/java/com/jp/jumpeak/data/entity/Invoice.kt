package com.jp.jumpeak.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jp.jumpeak.enums.Currency

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customer_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["date"]),
        Index(value = ["total_due"]),
        Index(value = ["customer_id"])
    ]
)
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "customer_id")
    val customerId: Long,
    val discount: Int,
    @ColumnInfo(name = "delivery_fee")
    val deliveryFee: Double,
    @ColumnInfo(name = "total_due")
    val totalDue: Double,
    val date: Long,
    val currency: Currency
)