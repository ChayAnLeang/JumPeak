package com.jp.jumpeak.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jp.jumpeak.data.dao.CustomerDao
import com.jp.jumpeak.data.dao.InvoiceDao
import com.jp.jumpeak.data.dao.ItemDao
import com.jp.jumpeak.data.dao.PaymentDao
import com.jp.jumpeak.data.entity.Customer
import com.jp.jumpeak.data.entity.Invoice
import com.jp.jumpeak.data.entity.Item
import com.jp.jumpeak.data.entity.Payment

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        Item::class,
        Payment::class,
        Invoice::class,
        Customer::class
    ]
)
@TypeConverters(Converters::class)
abstract class KotBungDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun paymentDao(): PaymentDao
    abstract fun customerDao(): CustomerDao
}