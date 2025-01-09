package com.c242_ps246.mentalq.ui.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO
}

@Composable
fun CustomToast(
    message: String,
    type: ToastType = ToastType.INFO,
    onDismiss: () -> Unit,
    placement: Alignment = Alignment.TopCenter
) {
    var isVisible by remember { mutableStateOf(true) }

    val outlineColor = when (type) {
        ToastType.SUCCESS -> Color(0xFF4CAF50)
        ToastType.ERROR -> Color(0xFFE53935)
        ToastType.INFO -> Color(0xFF2196F3)
    }

    val icon = when (type) {
        ToastType.SUCCESS -> Icons.Filled.Check
        ToastType.ERROR -> Icons.Filled.Warning
        ToastType.INFO -> Icons.Filled.Info
    }

    LaunchedEffect(Unit) {
        delay(3000L)
        isVisible = false
        delay(300)
        onDismiss()
    }

    if (isVisible) {
        Popup(alignment = placement) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -200 },
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                ) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -200 },
                    animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
                )
            ) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize()
                        .border(1.dp, outlineColor, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 6.dp,
                    color = MaterialTheme.colorScheme.background
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = outlineColor
                        )
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

