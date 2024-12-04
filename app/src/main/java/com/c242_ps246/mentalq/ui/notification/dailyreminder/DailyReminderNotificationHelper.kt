package com.c242_ps246.mentalq.ui.notification.dailyreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.c242_ps246.mentalq.MainActivity
import com.c242_ps246.mentalq.R

class DailyReminderNotificationHelper(private val context: Context) {
    companion object {
        private const val CHANNEL_ID = "daily_reminder_channel"
        private const val NOTIFICATION_ID = 714925
        const val DAILY_REMINDER_WORK_NAME = "DAILY_NOTES_REMINDER_WORK"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Daily Notes Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily reminder to take notes"
            enableLights(true)
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showDailyReminderNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.mentalq)
            .setContentTitle(context.getString(R.string.daily_reminder_title))
            .setContentText(context.getString(R.string.daily_reminder_content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}