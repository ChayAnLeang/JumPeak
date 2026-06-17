package com.jp.jumpeak.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.jp.jumpeak.data.entity.Invoice
import com.jp.jumpeak.data.pojo.InvoicePojo
import com.jp.jumpeak.data.projection.InvoiceWithCustomer

@Dao
interface InvoiceDao : BaseDao<Invoice>{
    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getById(id: Long): InvoicePojo

    @Transaction
    @Query("""
        SELECT COUNT(*)
        FROM (
            SELECT i.id
            FROM invoices i
            LEFT JOIN payments p
            ON i.id = p.invoice_id
            GROUP BY i.id
            HAVING i.total_due > COALESCE(SUM(p.amount),0)
        )
    """)
    fun countUnpaidInvoices(): LiveData<Int>

    @Transaction
    @Query("""
        SELECT 
            i.id AS invoiceId,
            c.full_name AS fullName,
            c.phone_number AS phoneNumber,
            i.total_due AS totalDue,
            COALESCE(SUM(p.amount),0) AS totalPaid,
            i.date,
            i.currency
        FROM invoices i 
        JOIN customers c 
        ON i.customer_id = c.id
        LEFT JOIN payments p 
        ON i.id = p.invoice_id
        WHERE c.full_name LIKE '%' || :customerName || '%'
        GROUP BY i.id
        HAVING i.total_due > totalPaid
        ORDER BY c.full_name , i.id ASC
    """)
    fun getAllUnpaidInvoices(customerName: String): PagingSource<Int, InvoiceWithCustomer>

    @Transaction
    @Query("""
        SELECT 
            i.id AS invoiceId,
            c.full_name AS fullName,
            c.phone_number AS phoneNumber,
            i.total_due AS totalDue,
            COALESCE(SUM(p.amount),0) AS totalPaid,
            i.date,
            i.currency
        FROM invoices i 
        JOIN customers c 
        ON i.customer_id = c.id
        LEFT JOIN payments p 
        ON i.id = p.invoice_id
        WHERE i.date BETWEEN :startDate AND :endDate 
        AND c.full_name LIKE '%' || :customerName || '%'
        GROUP BY i.id
        ORDER BY i.id DESC
    """)
    fun getDailyInvoice(startDate: Long,endDate:Long,customerName: String): PagingSource<Int, InvoiceWithCustomer>
}