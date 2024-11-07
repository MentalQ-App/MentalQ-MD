package com.c242_ps246.mentalq.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.c242_ps246.mentalq.R

@Composable
fun OnboardingScreen() {
    val darkTheme = isSystemInDarkTheme()
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.onboarding),
            contentDescription = "Onboarding",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = if(isSystemInDarkTheme()) CardDefaults.cardColors(
                Color.White
            )else CardDefaults.cardColors(
                Color.Black
            ),
            shape = RoundedCornerShape(
                topStart = 40.dp,
                topEnd = 40.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            )
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(72.dp, 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "Welcome",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSystemInDarkTheme()) Color.Black else Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Let's get started!",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        ripple(
                            color = if (darkTheme) Color.White else Color.LightGray,
                            radius = 24.dp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                        contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                    ),
                ) {
                    Text(
                        text = "Login",
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        ripple(
                            color = if (darkTheme) Color.White else Color.LightGray,
                            radius = 24.dp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                        contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                    ),
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    Text(
                        text = "Register",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                }
            }
        }
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen()
}