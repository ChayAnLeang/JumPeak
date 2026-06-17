package com.jp.jumpeak.data.repositoryImpl

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.room.withTransaction
import com.jp.jumpeak.data.dao.InvoiceDao
import com.jp.jumpeak.data.dao.ItemDao
import com.jp.jumpeak.data.db.KotBungDatabase
import com.jp.jumpeak.data.entity.Invoice
import com.jp.jumpeak.data.entity.Item
import com.jp.jumpeak.data.pojo.InvoicePojo
import com.jp.jumpeak.data.projection.InvoiceWithCustomer
import com.jp.jumpeak.data.repository.InvoiceRepository
import javax.inject.Inject

class InvoiceRepositoryImpl @Inject constructor (
    private val itemDao: ItemDao,
    private val db: KotBungDatabase,
    private val invoiceDao: InvoiceDao
) : InvoiceRepository {
    override fun countUnpaidInvoices(): LiveData<Int> {
        return invoiceDao.countUnpaidInvoices()
    }

    override suspend fun getById(id: Long): Result<InvoicePojo> {
        return runCatching { invoiceDao.getById(id) }
    }

    override suspend fun delete(invoice: Invoice): Result<String> {
        return runCatching {
            invoiceDao.delete(invoice)
            "Invoice Deleted"
        }
    }

    override suspend fun manage(invoice: Invoice,items: List<Item>): Result<Long> {
        return db.withTransaction {
            runCatching {
                val editMode = invoice.id != 0L
                val invoiceId = if(!editMode){
                    invoiceDao.insertThenReturnId(invoice)
                }
                else{
                    invoiceDao.update(invoice)
                    itemDao.deleteAllByInvoiceId(invoice.id)
                    invoice.id
                }
                itemDao.insertAll(items.map { item -> item.copy(invoiceId = invoiceId) })
                invoiceId
            }
        }
    }

    override fun getDailyInvoice(
        startDate: Long,
        endDate:Long,
        customerName: String
    ): LiveData<PagingData<InvoiceWithCustomer>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { invoiceDao.getDailyInvoice(startDate,endDate,customerName) }
        ).liveData
    }

    override fun getAllUnpaidInvoices(customerName: String): LiveData<PagingData<InvoiceWithCustomer>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { invoiceDao.getAllUnpaidInvoices(customerName) }
        ).liveData
    }
}