package com.jp.jumpeak.di

import android.content.Context
import androidx.room.Room
import com.jp.jumpeak.data.dao.PartiesDao
import com.jp.jumpeak.data.dao.ReminderDao
import com.jp.jumpeak.data.dao.TransactionDao
import com.jp.jumpeak.data.db.JumPeakDatabase
import com.jp.jumpeak.data.migration.MIGRATION_1_2
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
    fun provideDatabase(@ApplicationContext context: Context): JumPeakDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            JumPeakDatabase::class.java,
            "jum_peak_db"
        ).addMigrations(MIGRATION_1_2).build()
    }

    @Provides
    @Singleton
    fun providePartiesDao(db: JumPeakDatabase): PartiesDao{
        return db.partiesDao()
    }

    @Provides
    @Singleton
    fun provideReminderDao(db: JumPeakDatabase): ReminderDao{
        return db.reminderDao()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: JumPeakDatabase): TransactionDao{
        return db.transactionDao()
    }
}