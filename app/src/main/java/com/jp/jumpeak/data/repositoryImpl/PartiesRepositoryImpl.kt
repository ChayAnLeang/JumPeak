package com.jp.jumpeak.data.repositoryImpl

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.jp.jumpeak.data.dao.PartiesDao
import com.jp.jumpeak.data.dto.PartiesDTO
import com.jp.jumpeak.data.entity.Parties
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.data.repository.PartiesRepository
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.enums.PartiesType
import com.jp.jumpeak.util.classes.ExportUtil
import com.jp.jumpeak.util.objects.AmountUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import jxl.write.Label
import javax.inject.Inject

class PartiesRepositoryImpl @Inject constructor(
    private val partiesDao: PartiesDao,
    @ApplicationContext private val context: Context
) : PartiesRepository {
    private val exportUtil by lazy { ExportUtil(context,listOf("ឈ្មោះ","លេខទូរស័ព្ទ","លុយជំពាក់នៅសល់")) }

    override fun getByPartiesType(partiesType: PartiesType): LiveData<PagingData<PartiesDTO>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { partiesDao.getByPartiesType(partiesType) }
        ).liveData
    }

    override suspend fun getById(id: String): Result<Parties> {
        return runCatching { partiesDao.getById(id) }
    }

    override suspend fun exportToExcel(partiesType: PartiesType): Result<String> {
        try {
            val allParties = partiesDao.getAllByPartiesType(partiesType)
            if(allParties.isEmpty()){
                return Result.failure(Exception("No data yet"))
            }
            exportUtil.excel(partiesType.displayName){ sheet ->
                allParties.forEachIndexed{ row, partiesDTO ->
                    val parties = partiesDTO.parties
                    sheet.addCell(Label(0,row+1,parties.name))
                    sheet.addCell(Label(1,row+1,parties.phoneNumber))
                    sheet.addCell(Label(2,row+1, AmountUtil.format(partiesDTO.balance)))
                }
            }
            return Result.success("Exported to Excel")
        }catch (e: Exception){
            return Result.failure(e)
        }
    }

    override suspend fun manage(
        action: Action,
        parties: Parties,
        transaction: Transaction?
    ): Result<String> {
        return runCatching {
            partiesDao.manage(action,parties,transaction)
            "Parties ${action.displayName}"
        }
    }
}