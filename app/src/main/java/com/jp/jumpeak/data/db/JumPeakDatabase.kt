package com.jp.jumpeak.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jp.jumpeak.data.dao.PartiesDao
import com.jp.jumpeak.data.dao.ReminderDao
import com.jp.jumpeak.data.dao.TransactionDao
import com.jp.jumpeak.data.entity.Parties
import com.jp.jumpeak.data.entity.Reminder
import com.jp.jumpeak.data.entity.Transaction

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        Parties::class,
        Reminder::class,
        Transaction::class
    ]
)
@TypeConverters(Converters::class)
abstract class JumPeakDatabase : RoomDatabase() {
    abstract fun partiesDao(): PartiesDao
    abstract fun reminderDao(): ReminderDao
    abstract fun transactionDao(): TransactionDao
}