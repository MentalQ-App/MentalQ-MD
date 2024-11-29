package com.c242_ps246.mentalq

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import com.c242_ps246.mentalq.data.manager.MentalQAppPreferences
import com.c242_ps246.mentalq.ui.navigation.AppNavigation
import com.c242_ps246.mentalq.ui.onboarding.OnboardingScreen
import com.c242_ps246.mentalq.ui.onboarding.OnboardingViewModel
import com.c242_ps246.mentalq.ui.splash.SplashScreen
import com.c242_ps246.mentalq.ui.theme.MentalQTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val onboardingViewModel: OnboardingViewModel by viewModels()

    @Inject
    lateinit var preferencesManager: MentalQAppPreferences

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AppContent(viewModel: OnboardingViewModel, preferencesManager: MentalQAppPreferences) {
    val shouldShowOnboarding by viewModel.shouldShowOnboarding.collectAsState()
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