@file:Suppress("DEPRECATION")

package com.c242_ps246.mentalq.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.c242_ps246.mentalq.ui.theme.MentalQTheme

@Composable
fun RegisterScreen(onBackPress: () -> Unit, onRegisterSuccess: () -> Unit) {

}

@Preview
@Composable
fun RegisterScreenPreview() {
    MentalQTheme {
        RegisterScreen(onBackPress = {}, onRegisterSuccess = {})
    }
}