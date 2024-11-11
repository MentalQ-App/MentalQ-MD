package com.c242_ps246.mentalq.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.c242_ps246.mentalq.ui.auth.LoginScreen
import com.c242_ps246.mentalq.ui.auth.RegisterScreen
import com.c242_ps246.mentalq.ui.onboarding.OnboardingScreen
import com.c242_ps246.mentalq.ui.animation.PageAnimation.slideInFromBottom
import com.c242_ps246.mentalq.ui.animation.PageAnimation.slideOutToBottom
import com.c242_ps246.mentalq.ui.main.MainScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.ONBOARDING,
        modifier = modifier
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable(
            "login",
            enterTransition = { slideInFromBottom },
            exitTransition = { slideOutToBottom }
        ) {
            LoginScreen(
                onBackPress = { navController.popBackStack() },
                onAuthSuccess = {
                    navController.navigate("main_screen") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable(
            "register",
            enterTransition = { slideInFromBottom },
            exitTransition = { slideOutToBottom }
        ) {
            RegisterScreen(
                onBackPress = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("main_screen") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("main_screen") {
            MainScreen()
        }
    }
}

