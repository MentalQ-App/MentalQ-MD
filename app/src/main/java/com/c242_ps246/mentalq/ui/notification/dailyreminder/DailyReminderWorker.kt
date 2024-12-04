package com.c242_ps246.mentalq.ui.notification.dailyreminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.c242_ps246.mentalq.data.repository.NoteRepository
import dagger.hilt.android.EntryPointAccessors
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @Inject
    lateinit var noteRepository: NoteRepository

    init {
        val appContext = context.applicationContext
        EntryPointAccessors.fromApplication(appContext, DailyReminderWorkerEntryPoint::class.java)
            .injectDailyReminderWorker(this)
    }

    override suspend fun doWork(): Result {
        return try {
            val lastNoteAdded = noteRepository.getLastNote()?.createdAt
            if (shouldShowNotification(lastNoteAdded)) {
                showNotification()
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun shouldShowNotification(lastCreatedAt: String?): Boolean {
        if (lastCreatedAt == null) return true

        val lastCreatedAtInstant = try {
            Instant.parse(lastCreatedAt)
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }

        val currentInstant = Instant.now()
        val oneDayDuration = Duration.ofDays(1)

        return Duration.between(lastCreatedAtInstant, currentInstant) >= oneDayDuration
    }

    private fun showNotification() {
        val notificationHelper = DailyReminderNotificationHelper(applicationContext)
        notificationHelper.showDailyReminderNotification()
    }
}