package com.jp.jumpeak.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jp.jumpeak.enums.TransactionType

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Parties::class,
            parentColumns = ["id"],
            childColumns = ["parties_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["datetime"]),
        Index(value = ["parties_id"]),
        Index(value = ["is_settled"]),
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "parties_id")
    val partiesId: String,
    val amount: Double,
    val note: String,
    val datetime: Long,
    val type: TransactionType,
    @ColumnInfo(name = "is_settled")
    val isSettled: Boolean = false
)