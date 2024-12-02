package com.c242_ps246.mentalq.ui.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class DailyReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val notificationHelper = DailyReminderNotificationHelper(applicationContext)
        notificationHelper.showDailyReminderNotification()
    }
}