package com.c242_ps246.mentalq.ui.main

import android.app.Application
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.c242_ps246.mentalq.ui.main.chat.ChatRoomScreen
import com.c242_ps246.mentalq.ui.main.chat.ChatScreen
import com.c242_ps246.mentalq.ui.main.dashboard.DashboardScreen
import com.c242_ps246.mentalq.ui.main.note.NoteScreen
import com.c242_ps246.mentalq.ui.main.note.detail.DetailNoteScreen
import com.c242_ps246.mentalq.ui.main.profile.ProfileScreen
import com.c242_ps246.mentalq.ui.main.psychologist.PsychologistScreen
import com.c242_ps246.mentalq.ui.navigation.Routes

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    userRole: String
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomBar = when (currentRoute) {
        Routes.DASHBOARD, Routes.NOTE, Routes.CHAT, Routes.PROFILE -> true
        else -> false
    }

    var selectedItem by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentRoute) {
        selectedItem = when (currentRoute) {
            Routes.DASHBOARD -> 0
            Routes.NOTE -> 1
            Routes.CHAT -> 2
            Routes.PROFILE -> 3
            else -> selectedItem
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            composable(
                route = Routes.DASHBOARD
            ) {
                DashboardScreen(
                    onNavigateToNoteDetail = { noteId ->
                        navController.navigate("${Routes.NOTE_DETAIL}/$noteId")
                    },
                    onNavigateToPsychologistList = {
                        navController.navigate(Routes.PSYCHOLOGIST_LIST)
                    }
                )
            }

            composable(
                route = Routes.PSYCHOLOGIST_LIST
            ) {
                PsychologistScreen(
                    onNavigateToChatRoom = { chatId ->
                        navController.navigate("${Routes.CHAT_ROOM}/$chatId")
                    }
                )
            }


            composable(
                route = Routes.NOTE
            ) {
                NoteScreen(
                    onNavigateToNoteDetail = { noteId ->
                        navController.navigate("${Routes.NOTE_DETAIL}/$noteId")
                    },
                )
            }
            composable(
                route = "${Routes.NOTE_DETAIL}/{noteId}",
                arguments = listOf(
                    navArgument("noteId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
                DetailNoteScreen(
                    noteId = noteId,
                    onBackClick = {
                        navController.navigate(Routes.NOTE) {
                            popUpTo(Routes.NOTE_DETAIL) {
                                inclusive = true
                            }
                        }
                    },
                    application = LocalContext.current.applicationContext as Application
                )
            }
            composable(
                route = Routes.CHAT
            ) {
                ChatScreen(
                    onChatSelected = {},
                    onNavigateToChatRoom = { chatId ->
                        navController.navigate("${Routes.CHAT_ROOM}/$chatId")
                    },
                )
            }
            composable(
                route = "${Routes.CHAT_ROOM}/{chatId}",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
                ChatRoomScreen(
                    chatRoomId = chatId,
                    onBackClick = {
                        navController.navigate(Routes.CHAT) {
                            popUpTo(Routes.CHAT_ROOM) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(
                route = Routes.PROFILE
            ) {
                ProfileScreen(
                    onLogout = onLogout
                )
            }
        }

        if (shouldShowBottomBar) {
            CustomNavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedItem = selectedItem,
                onItemSelected = { index, route ->
                    selectedItem = index
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                },
                userRole = userRole
            )
        }
    }
}
