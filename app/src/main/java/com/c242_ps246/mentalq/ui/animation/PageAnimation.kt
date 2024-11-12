package com.c242_ps246.mentalq.ui.animation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

object PageAnimation {
    val slideInFromBottom = slideInVertically(
        initialOffsetY = { 1000 },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    ) + fadeIn(animationSpec = tween(500))

    val slideOutToBottom = slideOutVertically(
        targetOffsetY = { 1000 },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    ) + fadeOut(animationSpec = tween(500))

    val slideInFromRight = slideInHorizontally(
        initialOffsetX = { 1000 },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    ) + fadeIn(animationSpec = tween(500))

    val slideOutToRight = slideOutHorizontally(
        targetOffsetX = { 1000 },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    ) + fadeOut(animationSpec = tween(500))

    val slideInFromLeft = slideInHorizontally(
        initialOffsetX = { -1000 },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    ) + fadeIn(animationSpec = tween(500))

    val slideOutToLeft = slideOutHorizontally(
        targetOffsetX = { -1000 },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    ) + fadeOut(animationSpec = tween(500))
}
