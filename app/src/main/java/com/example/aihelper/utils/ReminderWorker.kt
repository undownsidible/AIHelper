package com.example.aihelper.utils

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.aihelper.R

class ReminderWorker(context: Context, params: WorkerParameters)
    : Worker(context, params) {

    override fun doWork(): Result {

        if (Build.VERSION.SDK_INT >= 33) {
            val permission = applicationContext.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                return Result.success()
            }
        }

        val title = inputData.getString("title") ?: "日程提醒"

        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "remind")
            .setContentTitle("日程提醒")
            .setContentText(title)
            .setSmallIcon(R.drawable.ic_session)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)

        return Result.success()
    }
}