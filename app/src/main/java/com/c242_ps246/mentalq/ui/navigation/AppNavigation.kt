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

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.AUTH,
        modifier = modifier
    ) {
        composable(
            Routes.AUTH,
            enterTransition = { slideInFromBottom },
            exitTransition = { slideOutToBottom }
        ) {
            AuthScreen{
                navController.navigate(Routes.MAIN_SCREEN) {
                    popUpTo(Routes.AUTH) {
                        inclusive = true
                    }
                }
            }
        }
        composable(Routes.MAIN_SCREEN) {
            MainScreen()
        }
    }
}

