package com.jp.jumpeak.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.jp.jumpeak.data.dto.PartiesDTO
import com.jp.jumpeak.data.entity.Parties
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.enums.PartiesType

interface PartiesRepository {
    fun getByPartiesType(partiesType: PartiesType): LiveData<PagingData<PartiesDTO>>
    suspend fun getById(id: String): Result<Parties>
    suspend fun exportToExcel(partiesType: PartiesType): Result<String>
    suspend fun manage(action: Action,parties: Parties,transaction: Transaction ?= null): Result<String>
}