package com.c242_ps246.mentalq.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.fillMaxSize()
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
        CustomNavigationBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
