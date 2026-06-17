package com.jp.jumpeak.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.jp.jumpeak.data.entity.Payment

@Dao
interface PaymentDao :BaseDao<Payment>{
    @Query("SELECT * FROM payments WHERE id = :id")
    suspend fun getById(id:Long): Payment

    @Query("SELECT * FROM payments WHERE invoice_id = :invoiceId ORDER BY id DESC")
    fun getByInvoiceId(invoiceId:Long):LiveData<List<Payment>>
}