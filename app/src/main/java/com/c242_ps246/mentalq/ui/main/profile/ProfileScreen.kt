package com.c242_ps246.mentalq.ui.main.profile

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.c242_ps246.mentalq.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.c242_ps246.mentalq.data.remote.response.UserData
import com.c242_ps246.mentalq.ui.component.CustomDialog
import com.c242_ps246.mentalq.ui.theme.MentalQTheme

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showConfirmDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Add spacing between items
            ) {
                item {
                    Text(
                        text = stringResource(id = R.string.your_profile),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    )
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.default_profile),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            UserDetailInfo(
                                UserData(
                                    id = 1,
                                    name = "John Doe",
                                    email = "johndoe@gmail.com",
                                    birthday = "01/01/1990"
                                )
                            )
                        }
                    }
                }
                item {
                    Text(
                        text = stringResource(id = R.string.preferences),
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            PreferenceItem(
                                title = stringResource(id = R.string.notifications),
                                isChecked = true,
                                onCheckedChange = {}
                            )
                            PreferenceItem(
                                title = stringResource(id = R.string.language),
                            )
                            PreferenceItem(
                                title = stringResource(id = R.string.privacy_and_policy)
                            )
                            PreferenceItem(
                                title = stringResource(id = R.string.terms_of_service)
                            )
                            LogoutItem(
                                onClick = {
                                    showConfirmDialog = true
                                }
                            )
                        }
                    }
                }
                if (showConfirmDialog) {
                    item {
                        CustomDialog(
                            dialogTitle = stringResource(id = R.string.logout),
                            dialogMessage = stringResource(id = R.string.logout_message),
                            onConfirm = {
                                viewModel.logout()
                                onLogout()
                                showConfirmDialog = false
                            },
                            onDismiss = { showConfirmDialog = false }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun UserDetailInfo(userData: UserData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = userData.name,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userData.email,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = userData.birthday,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

    }
}

@Composable
fun PreferenceItem(
    title: String,
    isChecked: Boolean = false,
    onClick: () -> Unit = {},
    onCheckedChange: ((Boolean) -> Unit)? = null
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title)
            if (onCheckedChange != null) {
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Navigate"
                )
            }
        }
    }
}

@Composable
fun LogoutItem(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Logout", color = Color.Red)
        Icon(
            imageVector = Icons.Default.ExitToApp,
            contentDescription = "Logout",
            tint = Color.Red
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    MentalQTheme {
        ProfileScreen({})
    }
}