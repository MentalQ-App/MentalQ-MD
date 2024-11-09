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

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onboarding",
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
            LoginScreen(onBackPress = { navController.popBackStack() })
        }
        composable(
            "register",
            enterTransition = { slideInFromBottom },
            exitTransition = { slideOutToBottom }
        ) {
            RegisterScreen(onBackPress = { navController.popBackStack() })
        }
    }
}
