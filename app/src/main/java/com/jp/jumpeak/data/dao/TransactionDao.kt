package com.jp.jumpeak.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.jp.jumpeak.data.dto.Summary
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.enums.Action

@Dao
interface TransactionDao : BaseDao<Transaction> {
    @Query("""
        SELECT *
        FROM transactions
        WHERE parties_id = :partiesId
        AND is_settled = :isSettled
        ORDER BY id DESC
    """)
    fun getByPartiesId(partiesId: String,isSettled: Boolean): PagingSource<Int, Transaction>

    @Query("""
        SELECT * 
        FROM transactions 
        WHERE parties_id = :partiesId 
        AND is_settled = :isSettled 
        ORDER BY id DESC
    """)
    suspend fun getAllByPartiesId(partiesId: String,isSettled: Boolean): List<Transaction>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): Transaction

    @Query("UPDATE transactions SET is_settled = 1 WHERE parties_id = :partiesId AND is_settled = 0")
    suspend fun clearIsSettledByPartiesId(partiesId: String)

    @androidx.room.Transaction
    suspend fun manage(action: Action,transaction: Transaction,isClear: Boolean){
        manage(action,transaction)
        if(isClear){
            clearIsSettledByPartiesId(transaction.partiesId)
        }
    }

    @Query("""
        SELECT
            COALESCE(SUM(CASE WHEN type = 'DEBT' THEN amount ELSE 0 END),0) AS totalDebt,
            COALESCE(SUM(CASE WHEN type = 'REPAYMENT' THEN amount ELSE 0 END),0) AS totalRepayment
        FROM transactions 
        WHERE parties_id = :partiesId
        AND is_settled = :isSettled
    """)
    fun getSummaryByPartiesId(partiesId: String,isSettled: Boolean): LiveData<Summary>
}