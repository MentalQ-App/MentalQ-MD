package com.c242_ps246.mentalq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.c242_ps246.mentalq.data.manager.MentalQAppPreferences
import com.c242_ps246.mentalq.ui.navigation.AppNavigation
import com.c242_ps246.mentalq.ui.notification.dailyreminder.DailyReminderWorker
import com.c242_ps246.mentalq.ui.notification.streak.StreakWorker
import com.c242_ps246.mentalq.ui.onboarding.OnboardingScreen
import com.c242_ps246.mentalq.ui.onboarding.OnboardingViewModel
import com.c242_ps246.mentalq.ui.splash.SplashScreen
import com.c242_ps246.mentalq.ui.theme.MentalQTheme
import com.c242_ps246.mentalq.ui.utils.NetworkAwareContent
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val onboardingViewModel: OnboardingViewModel by viewModels()

    @Inject
    lateinit var preferencesManager: MentalQAppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleDailyReminder()
        scheduleStreakNotification()
        enableEdgeToEdge()
        setContent {
            var showSplashScreen by remember { mutableStateOf(true) }

            if (showSplashScreen) {
                MentalQTheme {
                    SplashScreen { token, role ->
                        showSplashScreen = false
                    }
                }
            } else {
                AppContent(onboardingViewModel, preferencesManager)
            }
        }
    }

    private fun scheduleDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun scheduleStreakNotification() {
        val workRequest = PeriodicWorkRequestBuilder<StreakWorker>(23, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }
}

@Composable
fun AppContent(viewModel: OnboardingViewModel, preferencesManager: MentalQAppPreferences) {
    val shouldShowOnboarding by viewModel.shouldShowOnboarding.collectAsState()

    NetworkAwareContent {
        if (shouldShowOnboarding) {
            MentalQTheme {
                OnboardingScreen(
                    onFinished = {
                        viewModel.onOnboardingCompleted()
                    }
                )
            }
        } else {
            MentalQTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        preferencesManager = preferencesManager
                    )
                }
            }
        }
    }
}