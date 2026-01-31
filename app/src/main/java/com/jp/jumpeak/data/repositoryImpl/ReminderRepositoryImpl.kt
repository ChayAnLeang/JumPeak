package com.jp.jumpeak.data.repositoryImpl

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.jp.jumpeak.data.dao.ReminderDao
import com.jp.jumpeak.data.entity.Reminder
import com.jp.jumpeak.data.repository.ReminderRepository
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.worker.ReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao,
    @ApplicationContext private val context: Context
) : ReminderRepository {
    override fun getAll(): LiveData<PagingData<Reminder>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { reminderDao.getAll() }
        ).liveData
    }

    override suspend fun getById(id: Long): Result<Reminder> {
        return runCatching { reminderDao.getById(id) }
    }

    override suspend fun manage(action: Action,reminder: Reminder): Result<String> {
        return runCatching {
            when(action){
                Action.ADD -> {
                    val request = getRequest(reminder.datetime,reminder.message)
                    WorkManager.getInstance(context).enqueue(request)
                    reminderDao.insert(reminder.copy(workRequestId = request.id.toString()))
                }
                Action.EDIT -> {
                    val request = getRequest(reminder.datetime,reminder.message)
                    WorkManager.getInstance(context).apply {
                        cancelWorkById(UUID.fromString(reminder.workRequestId))
                        enqueue(request)
                    }
                    reminderDao.update(reminder)
                }
                Action.DELETE -> {
                    WorkManager.getInstance(context).cancelWorkById(UUID.fromString(reminder.workRequestId))
                    reminderDao.delete(reminder)
                }
            }
            "Reminder ${action.displayName}"
        }
    }

    private fun getRequest(dateTime: Long,message: String): OneTimeWorkRequest{
        val delay = dateTime - System.currentTimeMillis()
        return OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("message" to message))
            .build()
    }
}