package com.c242_ps246.mentalq.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.manager.MentalQAppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private val onboardingManager = MentalQAppPreferences(application)

    val shouldShowOnboarding: StateFlow<Boolean> = onboardingManager.shouldShowOnboarding
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun onOnboardingCompleted() {
        viewModelScope.launch {
            onboardingManager.completeOnboarding()
        }
    }
}