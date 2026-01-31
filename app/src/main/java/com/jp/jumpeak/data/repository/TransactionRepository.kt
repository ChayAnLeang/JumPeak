package com.jp.jumpeak.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.jp.jumpeak.data.dto.Summary
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.enums.Action

interface TransactionRepository {
    fun getByPartiesId(partiesId: String,isSettled: Boolean): LiveData<PagingData<Transaction>>
    suspend fun getById(id: Long): Result<Transaction>
    suspend fun exportToExcel(partiesId: String, partiesName: String,isSettled: Boolean): Result<String>
    suspend fun manage(action: Action,transaction: Transaction,isClear: Boolean): Result<String>
    fun getSummaryByPartiesId(partiesId: String,isSettled: Boolean): LiveData<Summary>
}