package com.jp.jumpeak.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jp.jumpeak.data.entity.Item

@Dao
interface ItemDao : BaseDao<Item>{
    @Insert
    suspend fun insertAll(items: List<Item>)

    @Query("DELETE FROM items WHERE invoice_id = :invoiceId")
    suspend fun deleteAllByInvoiceId(invoiceId: Long)
}