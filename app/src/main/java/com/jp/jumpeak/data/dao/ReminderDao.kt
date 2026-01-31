package com.jp.jumpeak.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.jp.jumpeak.data.entity.Reminder

@Dao
interface ReminderDao : BaseDao<Reminder> {
    @Query("SELECT * FROM reminders ORDER BY id DESC")
    fun getAll(): PagingSource<Int,Reminder>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): Reminder
}