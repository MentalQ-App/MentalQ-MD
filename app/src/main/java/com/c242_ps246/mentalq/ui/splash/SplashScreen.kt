package com.c242_ps246.mentalq.ui.splash

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.c242_ps246.mentalq.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navigateToAuthScreen: (String?, String?) -> Unit
) {
    val viewModel: SplashViewModel = hiltViewModel()
    val token by viewModel.token.collectAsState()
    val role by viewModel.role.collectAsState()

    var isInitialCheckComplete by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.mentalq),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MentalQ",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(2000)
        isInitialCheckComplete = true
    }

    LaunchedEffect(isInitialCheckComplete, token) {
        if (isInitialCheckComplete) {
            if (!token.isNullOrEmpty()) {
                viewModel.getUserRole()
            } else {
                isLoading = false
                navigateToAuthScreen(null, null)
            }
        }
    }

    LaunchedEffect(role) {
        if (role != null) {
            isLoading = false
            Log.d("SplashScreen", "Navigating to AuthScreen with role: $role")
            navigateToAuthScreen(token, role)
        }
    }
}
