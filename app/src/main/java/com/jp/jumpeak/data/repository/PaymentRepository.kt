package com.jp.jumpeak.data.repository

import androidx.lifecycle.LiveData
import com.jp.jumpeak.data.entity.Payment
import com.jp.jumpeak.enums.Action

interface PaymentRepository {
    suspend fun getById(id: Long): Result<Payment>
    fun getByInvoiceId(invoiceId:Long):LiveData<List<Payment>>
    suspend fun manage(payment: Payment,action: Action):Result<String>
}