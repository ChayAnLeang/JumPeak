package com.jp.jumpeak.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.jp.jumpeak.data.entity.Customer

@Dao
interface CustomerDao : BaseDao<Customer>{
    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getById(id: Long): Customer

    @Query("SELECT * FROM customers WHERE full_name LIKE '%' || :name || '%' ORDER BY id DESC")
    fun getByName(name: String): PagingSource<Int, Customer>
}