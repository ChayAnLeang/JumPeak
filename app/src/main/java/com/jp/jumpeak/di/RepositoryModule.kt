package com.jp.jumpeak.di

import com.jp.jumpeak.data.repository.CustomerRepository
import com.jp.jumpeak.data.repository.InvoiceRepository
import com.jp.jumpeak.data.repository.PaymentRepository
import com.jp.jumpeak.data.repositoryImpl.CustomerRepositoryImpl
import com.jp.jumpeak.data.repositoryImpl.InvoiceRepositoryImpl
import com.jp.jumpeak.data.repositoryImpl.PaymentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPaymentRepository(paymentRepositoryImpl: PaymentRepositoryImpl): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindInvoiceRepository(invoiceRepositoryImpl: InvoiceRepositoryImpl): InvoiceRepository

    @Binds
    @Singleton
    abstract fun bindCustomerRepository(customerRepositoryImpl: CustomerRepositoryImpl): CustomerRepository
}