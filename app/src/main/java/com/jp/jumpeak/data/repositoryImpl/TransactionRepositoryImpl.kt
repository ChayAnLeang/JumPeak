package com.jp.jumpeak.data.repositoryImpl

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.jp.jumpeak.data.dao.TransactionDao
import com.jp.jumpeak.data.dto.Summary
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.data.repository.TransactionRepository
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.enums.TransactionType
import com.jp.jumpeak.util.classes.ExportUtil
import com.jp.jumpeak.util.objects.AmountUtil
import com.jp.jumpeak.util.objects.DateTimeUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import jxl.write.Label
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    @ApplicationContext private val context: Context
) : TransactionRepository {
    private val exportUtil by lazy {
        ExportUtil(context,listOf("កាលបរិច្ឆេទ & ពេលវេលា","លុយសង","លុយជំពាក់","ចំណាំ"))
    }

    override fun getByPartiesId(partiesId: String,isSettled: Boolean): LiveData<PagingData<Transaction>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { transactionDao.getByPartiesId(partiesId,isSettled) }
        ).liveData
    }

    override suspend fun getById(id: Long): Result<Transaction> {
        return runCatching { transactionDao.getById(id) }
    }

    override suspend fun exportToExcel(
        partiesId: String,
        partiesName: String,
        isSettled: Boolean
    ): Result<String> {
        try {
            val transactions = transactionDao.getAllByPartiesId(partiesId,isSettled)
            if(transactions.isEmpty()){
                return Result.failure(Exception("No data yet"))
            }
            val debtType = if(isSettled){
                "លុយជំពាក់ដែលសងរួច"
            }else{
                "លុយជំពាក់ដែលមិនទាន់សងរួច"
            }
            val sheetName = String.format("%s_%s",partiesName,debtType)
            exportUtil.excel(sheetName){ sheet ->
                transactions.forEachIndexed{ row, transaction ->
                    val amount = AmountUtil.format(transaction.amount)
                    val dateTime = DateTimeUtil.format(transaction.datetime)
                    sheet.addCell(Label(0,row+1,dateTime))
                    val (repayment,debt) = when(transaction.type){
                        TransactionType.DEBT -> Pair("",amount)
                        TransactionType.REPAYMENT -> Pair(amount,"")
                    }
                    sheet.addCell(Label(1,row+1,repayment))
                    sheet.addCell(Label(2,row+1,debt))
                    sheet.addCell(Label(3,row+1,transaction.note))
                }
            }
            return Result.success("Exported to Excel")
        }catch (e: Exception){
            return Result.failure(e)
        }
    }

    override suspend fun manage(action: Action,transaction: Transaction,isClear: Boolean): Result<String> {
        return runCatching {
            transactionDao.manage(action,transaction,isClear)
            "Transaction ${action.displayName}"
        }
    }

    override fun getSummaryByPartiesId(partiesId: String,isSettled: Boolean): LiveData<Summary> {
        return transactionDao.getSummaryByPartiesId(partiesId,isSettled)
    }
}