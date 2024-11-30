package com.c242_ps246.mentalq

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.c242_ps246.mentalq.ui.notification.StreakWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MentalQApp : Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        StreakWorker.scheduleNextNotification(this)
    }
}