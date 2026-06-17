package com.jp.jumpeak.di

import android.content.Context
import androidx.room.Room
import com.jp.jumpeak.data.dao.CustomerDao
import com.jp.jumpeak.data.dao.InvoiceDao
import com.jp.jumpeak.data.dao.ItemDao
import com.jp.jumpeak.data.dao.PaymentDao
import com.jp.jumpeak.data.db.KotBungDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KotBungDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            KotBungDatabase::class.java,
            "kot_bung_db"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    @Singleton
    fun provideItemDao(db: KotBungDatabase): ItemDao{
        return db.itemDao()
    }

    @Provides
    @Singleton
    fun providePaymentDao(db: KotBungDatabase): PaymentDao{
        return db.paymentDao()
    }

    @Provides
    @Singleton
    fun provideInvoiceDao(db: KotBungDatabase): InvoiceDao{
        return db.invoiceDao()
    }

    @Provides
    @Singleton
    fun provideCustomerDao(db: KotBungDatabase): CustomerDao{
        return db.customerDao()
    }
}