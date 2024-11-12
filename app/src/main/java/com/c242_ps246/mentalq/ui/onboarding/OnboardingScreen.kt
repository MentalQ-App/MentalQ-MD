package com.c242_ps246.mentalq.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.c242_ps246.mentalq.ui.theme.MentalQTheme

@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit, onNavigateToRegister: () -> Unit) {

}

@Preview
@Composable
fun OnboardingScreenPreview() {
    MentalQTheme {
        OnboardingScreen({}, {})
    }
}
