package com.jp.jumpeak.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = Invoice::class,
            parentColumns = ["id"],
            childColumns = ["invoice_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["amount"]),
        Index(value = ["invoice_id"])
    ]
)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    @ColumnInfo(name = "invoice_id")
    val invoiceId:Long,
    val amount:Double,
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String,
    val date:Long
)