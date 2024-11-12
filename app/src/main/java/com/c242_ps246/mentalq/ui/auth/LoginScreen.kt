@file:Suppress("DEPRECATION")

package com.c242_ps246.mentalq.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.c242_ps246.mentalq.ui.theme.MentalQTheme

@Composable
fun LoginScreen(onBackPress: () -> Unit, onAuthSuccess: () -> Unit) {

}

@Preview
@Composable
fun LoginScreenPreview() {
    MentalQTheme {
        LoginScreen(onBackPress = {}, onAuthSuccess = {})
    }
}
