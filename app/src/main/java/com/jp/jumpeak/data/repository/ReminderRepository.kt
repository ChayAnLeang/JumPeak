package com.jp.jumpeak.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.jp.jumpeak.data.entity.Reminder
import com.jp.jumpeak.enums.Action

interface ReminderRepository {
    fun getAll(): LiveData<PagingData<Reminder>>
    suspend fun getById(id: Long): Result<Reminder>
    suspend fun manage(action: Action,reminder: Reminder): Result<String>
}