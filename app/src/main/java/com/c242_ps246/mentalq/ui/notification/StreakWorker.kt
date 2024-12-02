package com.c242_ps246.mentalq.ui.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.c242_ps246.mentalq.data.manager.MentalQAppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class StreakWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val streakInfo = getStreakInfo(applicationContext).firstOrNull()

            streakInfo?.let { (lastEntryDate, streakCount) ->
                val today = LocalDate.now()
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE

                if (lastEntryDate.isNotEmpty()) {
                    val lastEntryLocalDate = LocalDate.parse(lastEntryDate, formatter)
                    if (lastEntryLocalDate != today) {
                        showNotification(streakCount)
                    }
                }
            }
            scheduleNextNotification(applicationContext)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun showNotification(streakCount: Int) {
        val streakNotificationHelper = StreakNotificationHelper(applicationContext)
        streakNotificationHelper.showStreakNotification(streakCount)
    }

    private suspend fun getStreakInfo(context: Context) = withContext(Dispatchers.IO) {
        MentalQAppPreferences(context).getStreakInfo()
    }

    companion object {
        fun scheduleNextNotification(context: Context) {
            val now = LocalDateTime.now()
            val nextMidnight = now.plusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)

            val initialDelay = Duration.between(now, nextMidnight).toMillis()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val dailyWorkRequest = OneTimeWorkRequestBuilder<StreakWorker>()
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    StreakNotificationHelper.WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    dailyWorkRequest
                )
        }
    }
}
