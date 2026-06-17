package com.jp.jumpeak.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = Invoice::class,
            parentColumns = ["id"],
            childColumns = ["invoice_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoice_id"])]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "invoice_id")
    val invoiceId: Long,
    val goods: String,
    val qty: Int,
    val unit: String,
    val price: Double,
    val amount:Double
)