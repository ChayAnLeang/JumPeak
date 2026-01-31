package com.jp.jumpeak.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.jp.jumpeak.enums.Action

@Dao
interface BaseDao<T> {
    @Insert
    suspend fun insert(obj: T)

    @Update
    suspend fun update(obj: T)

    @Delete
    suspend fun delete(obj: T)

    suspend fun manage(action: Action,obj: T){
        when(action){
            Action.ADD -> insert(obj)
            Action.EDIT -> update(obj)
            Action.DELETE -> delete(obj)
        }
    }
}