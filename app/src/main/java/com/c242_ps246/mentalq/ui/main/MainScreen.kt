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
import com.c242_ps246.mentalq.ui.main.psychologist.midtrans.MidtransScreen
import com.c242_ps246.mentalq.ui.main.psychologist.midtrans.MidtransWebView
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
                    onBackClick = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.PSYCHOLOGIST_LIST) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToMidtransWebView = { userId, price, itemId ->
                        navController.navigate("${Routes.MIDTRANS_WEBVIEW}/$userId/$price/$itemId")
                    }
                )
            }

            composable(
                route = "${Routes.MIDTRANS_MAIN_SCREEN}/{orderId}/{userId}/{itemId}",
                arguments = listOf(
                    navArgument("orderId") { type = NavType.StringType },
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("itemId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
                MidtransScreen(
                    orderId = orderId,
                    userId = userId,
                    itemId = itemId,
                    onSuccess = { chatId ->
                        navController.navigate("${Routes.CHAT_ROOM}/$chatId")
                    },
                    onFailed = {
                        navController.navigate(Routes.PSYCHOLOGIST_LIST) {
                            popUpTo(Routes.MIDTRANS_MAIN_SCREEN) {
                                inclusive = true
                            }
                        }
                    },
                    onBackClick = {
                        navController.navigate(Routes.PSYCHOLOGIST_LIST) {
                            popUpTo(Routes.MIDTRANS_MAIN_SCREEN) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(
                route = "${Routes.MIDTRANS_WEBVIEW}/{userId}/{price}/{itemId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("price") { type = NavType.IntType },
                    navArgument("itemId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                val price = backStackEntry.arguments?.getInt("price") ?: return@composable
                val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
                MidtransWebView(
                    userId = userId,
                    price = price,
                    itemId = itemId,
                    onBackClick = { orderId ->
                        navController.navigate("${Routes.MIDTRANS_MAIN_SCREEN}/$orderId/$userId/$itemId") {
                            popUpTo(Routes.MIDTRANS_WEBVIEW) {
                                inclusive = true
                            }
                        }
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
                    onBackClick = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.CHAT) {
                                inclusive = true
                            }
                        }
                    }
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
