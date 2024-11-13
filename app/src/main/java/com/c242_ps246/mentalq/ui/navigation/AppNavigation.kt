package com.c242_ps246.mentalq.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.c242_ps246.mentalq.ui.animation.PageAnimation.slideInFromBottom
import com.c242_ps246.mentalq.ui.animation.PageAnimation.slideOutToBottom
import com.c242_ps246.mentalq.ui.auth.AuthScreen
import com.c242_ps246.mentalq.ui.main.MainScreen
import com.c242_ps246.mentalq.ui.onboarding.OnboardingScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.ONBOARDING,
        modifier = modifier
    ) {
        composable (
            Routes.ONBOARDING,
            enterTransition = { slideInFromBottom },
            exitTransition = { slideOutToBottom }
        ) {
            OnboardingScreen {
                navController.navigate(Routes.AUTH) {
                    popUpTo(Routes.ONBOARDING) {
                        inclusive = true
                    }
                }
            }
        }
        composable(
            Routes.AUTH,
            enterTransition = { slideInFromBottom },
            exitTransition = { slideOutToBottom }
        ) {
            AuthScreen()
        }
        composable(Routes.MAIN_SCREEN) {
            MainScreen()
        }
    }
}

