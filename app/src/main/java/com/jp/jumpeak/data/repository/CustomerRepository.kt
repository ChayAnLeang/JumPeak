package com.jp.jumpeak.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.jp.jumpeak.data.entity.Customer
import com.jp.jumpeak.enums.Action

interface CustomerRepository{
    suspend fun getById(id: Long): Result<Customer>
    fun getByName(name: String): LiveData<PagingData<Customer>>
    suspend fun manage(customer: Customer,action: Action): Result<String>
}