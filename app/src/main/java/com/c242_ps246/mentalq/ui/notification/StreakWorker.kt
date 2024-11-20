package com.c242_ps246.mentalq.ui.notification

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.c242_ps246.mentalq.data.manager.MentalQAppPreferences
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class StreakWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val WORK_NAME = "streak_checker"

        fun schedule(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<StreakWorker>(
                1, TimeUnit.DAYS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        try {
            val streakInfo = runBlocking { getStreakInfo().first() }
            val lastEntryDate = streakInfo.first
            val streakCount = streakInfo.second

            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE

            if (lastEntryDate.isNotEmpty()) {
                val lastEntryLocalDate = LocalDate.parse(lastEntryDate, formatter)
                val hoursSinceLastEntry = ChronoUnit.HOURS.between(
                    lastEntryLocalDate.atStartOfDay(),
                    today.atStartOfDay()
                )

                if (hoursSinceLastEntry >= 23 && hoursSinceLastEntry < 24) {
                    NotificationHelper.showStreakNotification(streakCount, applicationContext)
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun getStreakInfo(): Flow<Pair<String, Int>> {
        return MentalQAppPreferences(applicationContext).getStreakInfo()
    }
}
