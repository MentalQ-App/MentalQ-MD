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
import androidx.compose.ui.tooling.preview.Preview
import com.c242_ps246.mentalq.ui.navigation.AppNavigation
import com.c242_ps246.mentalq.ui.onboarding.OnboardingScreen
import com.c242_ps246.mentalq.ui.onboarding.OnboardingViewModel
import com.c242_ps246.mentalq.ui.theme.MentalQTheme

class MainActivity : ComponentActivity() {
    private val onboardingViewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppContent(onboardingViewModel)
        }
    }
}

@Composable
fun AppContent(viewModel: OnboardingViewModel) {
    val shouldShowOnboarding by viewModel.shouldShowOnboarding.collectAsState()

    if (shouldShowOnboarding) {
        OnboardingScreen(
            onFinished = {
                viewModel.onOnboardingCompleted()
            }
        )
    } else {
        MentalQTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AppNavigation(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    var showOnboarding by remember { mutableStateOf(true) }
    if (showOnboarding) {
        OnboardingScreen(
            onFinished = {
                showOnboarding = false
            }
        )
    } else {
        MentalQTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AppNavigation(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}