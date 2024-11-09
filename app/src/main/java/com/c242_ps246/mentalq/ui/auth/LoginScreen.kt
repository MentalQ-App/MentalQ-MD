@file:Suppress("DEPRECATION")

package com.c242_ps246.mentalq.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.c242_ps246.mentalq.ui.theme.Black
import com.c242_ps246.mentalq.ui.theme.White
import com.c242_ps246.mentalq.R

@Composable
fun LoginScreen(onBackPress: () -> Unit){
    val noDarkTheme = !isSystemInDarkTheme()
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = if(noDarkTheme) CardDefaults.cardColors(
            White
        )else CardDefaults.cardColors(
            Black
        ),
        shape = RoundedCornerShape(
            topStart = 40.dp,
            topEnd = 40.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBackPress, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = if (noDarkTheme) Black else White
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(id = R.string.login),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = if (noDarkTheme) Black else White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = "",
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.username), style = TextStyle(color = Color.Gray)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.LightGray,
                        disabledContainerColor = Color.Gray,
                        errorContainerColor = Color.Red,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = "",
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.password), style = TextStyle(color = Color.Gray)) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.LightGray,
                        disabledContainerColor = Color.Gray,
                        errorContainerColor = Color.Red,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        ripple(
                            color = if (noDarkTheme) White else Color.LightGray,
                            radius = 24.dp
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (noDarkTheme) Black else White,
                        contentColor = if (noDarkTheme) White else Black
                    ),
                ) {
                    Text(
                        text = stringResource(id = R.string.login),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(onBackPress = {})
}