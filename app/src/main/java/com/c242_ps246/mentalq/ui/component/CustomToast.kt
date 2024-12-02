package com.c242_ps246.mentalq.ui.component

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.window.Popup
import androidx.compose.material3.Surface

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO
}

@Composable
fun CustomToast(
    message: String,
    type: ToastType = ToastType.INFO,
    duration: Long = 2000L,
    onDismiss: () -> Unit,
    placement: Alignment = Alignment.TopCenter
) {
    var isVisible by remember { mutableStateOf(true) }

    val backgroundColor = when (type) {
        ToastType.SUCCESS -> Color(0xFF4CAF50)
        ToastType.ERROR -> Color(0xFFE53935)
        ToastType.INFO -> Color(0xFF2196F3)
    }

    val icon = when (type) {
        ToastType.SUCCESS -> Icons.Filled.Check
        ToastType.ERROR -> Icons.Filled.Warning
        ToastType.INFO -> Icons.Filled.Info
    }

    LaunchedEffect(key1 = true) {
        delay(duration)
        isVisible = false
        onDismiss()
    }

    if (isVisible) {
        Popup(
            alignment = placement
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it }
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { -it }
                ) + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize(),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp,
                    color = backgroundColor
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text(
                            text = message,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}