package com.jp.jumpeak.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.jp.jumpeak.data.entity.Invoice
import com.jp.jumpeak.data.entity.Item
import com.jp.jumpeak.data.pojo.InvoicePojo
import com.jp.jumpeak.data.projection.InvoiceWithCustomer

interface InvoiceRepository{
    fun countUnpaidInvoices(): LiveData<Int>
    suspend fun getById(id: Long): Result<InvoicePojo>
    suspend fun delete(invoice: Invoice): Result<String>
    suspend fun manage(invoice: Invoice,items: List<Item>): Result<Long>
    fun getAllUnpaidInvoices(customerName: String): LiveData<PagingData<InvoiceWithCustomer>>
    fun getDailyInvoice(startDate: Long,endDate:Long,customerName: String): LiveData<PagingData<InvoiceWithCustomer>>
}