package com.jp.jumpeak.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jp.jumpeak.data.dto.PartiesDTO
import com.jp.jumpeak.data.entity.Parties
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.enums.PartiesType

@Dao
interface PartiesDao : BaseDao<Parties>{
    @Query("""
        SELECT 
            p.*,
            (COALESCE(SUM(CASE WHEN t.type = 'DEBT' THEN amount ELSE 0 END),0) - 
             COALESCE(SUM(CASE WHEN t.type = 'REPAYMENT' THEN amount ELSE 0 END),0)) AS balance
        FROM parties p 
        LEFT JOIN transactions t
        ON p.id = t.parties_id
        WHERE p.type = :partiesType
        GROUP BY p.id
        ORDER BY balance DESC
    """)
    fun getByPartiesType(partiesType: PartiesType): PagingSource<Int, PartiesDTO>

    @Query("""
        SELECT 
            p.*,
            (COALESCE(SUM(CASE WHEN t.type = 'DEBT' THEN amount ELSE 0 END),0) - 
             COALESCE(SUM(CASE WHEN t.type = 'REPAYMENT' THEN amount ELSE 0 END),0)) AS balance
        FROM parties p 
        LEFT JOIN transactions t
        ON p.id = t.parties_id
        WHERE p.type = :partiesType
        GROUP BY p.id
        ORDER BY balance DESC
    """)
    suspend fun getAllByPartiesType(partiesType: PartiesType): List<PartiesDTO>

    @Query("SELECT * FROM parties WHERE id = :id")
    suspend fun getById(id: String): Parties

    @Insert
    suspend fun addFirstTransaction(transaction: Transaction)

    @androidx.room.Transaction
    suspend fun manage(action: Action,parties: Parties,transaction: Transaction ?= null){
        manage(action,parties)
        transaction?.let { addFirstTransaction(transaction) }
    }
}