package com.c242_ps246.mentalq.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.c242_ps246.mentalq.ui.animation.PageAnimation.slideInFromRight
import com.c242_ps246.mentalq.ui.animation.PageAnimation.slideOutToLeft
import com.c242_ps246.mentalq.ui.main.dashboard.DashboardScreen
import com.c242_ps246.mentalq.ui.main.note.NoteScreen
import com.c242_ps246.mentalq.ui.main.profile.ProfileScreen
import com.c242_ps246.mentalq.ui.navigation.Routes

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { CustomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Routes.DASHBOARD,
                enterTransition = { slideInFromRight },
                exitTransition = { slideOutToLeft }
            ) {
                DashboardScreen()
            }
            composable(
                route = Routes.NOTE,
                enterTransition = { slideInFromRight },
                exitTransition = { slideOutToLeft }
            ) {
                NoteScreen()
            }
            composable(
                route = Routes.PROFILE,
                enterTransition = { slideInFromRight },
                exitTransition = { slideOutToLeft }
            ) {
                ProfileScreen()
            }
        }
    }
}

