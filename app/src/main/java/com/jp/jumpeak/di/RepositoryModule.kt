package com.jp.jumpeak.di

import com.jp.jumpeak.data.repository.PartiesRepository
import com.jp.jumpeak.data.repository.ReminderRepository
import com.jp.jumpeak.data.repository.TransactionRepository
import com.jp.jumpeak.data.repositoryImpl.PartiesRepositoryImpl
import com.jp.jumpeak.data.repositoryImpl.ReminderRepositoryImpl
import com.jp.jumpeak.data.repositoryImpl.TransactionRepositoryImpl
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
    abstract fun bindPartiesRepository(
        partiesRepositoryImpl: PartiesRepositoryImpl
    ): PartiesRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        reminderRepositoryImpl: ReminderRepositoryImpl
    ): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository
}