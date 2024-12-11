package com.c242_ps246.mentalq.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Tosca_600,
    onPrimary = Black,
    secondary = Tosca_600,
    onSecondary = Black,
    tertiary = Grey40,
    background = Black,
    onBackground = White,
    surface = LightBlack,
    onSurface = White,
    outline = LighterBlack,
    outlineVariant = LighterBlack,
)

private val LightColorScheme = lightColorScheme(
    primary = Tosca_200,
    onPrimary = White,
    secondary = Tosca_200,
    onSecondary = White,
    tertiary = Grey40,
    background = White,
    onBackground = Black,
    surface = NeutralWhite,
    onSurface = Black,
    outlineVariant = Tosca_100,
    outline = LighterGrey
)

@Composable
fun MentalQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}