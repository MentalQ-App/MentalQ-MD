package com.c242_ps246.mentalq.ui.notification.streak

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            context?.let {
                StreakWorker.scheduleNextNotification(it)
            }
        }
    }
}