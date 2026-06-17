package com.jp.jumpeak.data.projection

import com.jp.jumpeak.enums.Currency

data class InvoiceWithCustomer(
    val invoiceId:Long,
    val fullName: String,
    val phoneNumber:String,
    val totalDue:Double,
    val totalPaid:Double,
    val date:Long,
    val currency: Currency
)