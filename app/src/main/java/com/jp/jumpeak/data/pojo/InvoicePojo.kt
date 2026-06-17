package com.jp.jumpeak.data.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.jp.jumpeak.data.entity.Customer
import com.jp.jumpeak.data.entity.Invoice
import com.jp.jumpeak.data.entity.Item

data class InvoicePojo(
    @Embedded
    val invoice: Invoice,

    @Relation(
        parentColumn = "customer_id",
        entityColumn = "id"
    )
    val customer: Customer,

    @Relation(
        parentColumn = "id",
        entityColumn = "invoice_id"
    )
    val items: List<Item>,
)