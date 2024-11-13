package com.c242_ps246.mentalq.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.c242_ps246.mentalq.ui.auth.LoginScreen
import com.c242_ps246.mentalq.ui.auth.RegisterScreen
import com.c242_ps246.mentalq.ui.animation.PageAnimation.slideInFromBottom
import com.c242_ps246.mentalq.ui.animation.PageAnimation.slideOutToBottom
import com.c242_ps246.mentalq.ui.main.MainScreen
import com.c242_ps246.mentalq.ui.onboarding.OnboardingScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {
        composable (
            Routes.ONBOARDING,
            enterTransition = { slideInFromBottom },
            exitTransition = { slideOutToBottom }
        ) {
            OnboardingScreen { navController.navigate(Routes.LOGIN) }
        }
        composable(
            Routes.LOGIN,
            enterTransition = { slideInFromBottom },
            exitTransition = { slideOutToBottom }
        ) {
            LoginScreen(
                onBackPress = { navController.popBackStack() },
                onAuthSuccess = {
                    navController.navigate(Routes.MAIN_SCREEN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(
            Routes.REGISTER,
            enterTransition = { slideInFromBottom },
            exitTransition = { slideOutToBottom }
        ) {
            RegisterScreen(
                onBackPress = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MAIN_SCREEN) {
            MainScreen()
        }
    }
}

