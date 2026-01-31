package com.jp.jumpeak.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jp.jumpeak.R
import com.jp.jumpeak.presentation.ui.parties.activity.PartiesActivity
import com.jp.jumpeak.util.objects.NotificationUtil

class ReminderWorker(context: Context,params: WorkerParameters) : CoroutineWorker(context, params) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val message = inputData.getString("message") ?: return Result.failure()
        val notification = NotificationCompat.Builder(applicationContext,NotificationUtil.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(getIntentPending())
            .build()
        NotificationManagerCompat
            .from(applicationContext)
            .notify(System.currentTimeMillis().toInt(), notification)
        return Result.success()
    }

    private fun getIntentPending(): PendingIntent{
        val intent = Intent(applicationContext, PartiesActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
