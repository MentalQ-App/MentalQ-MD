package com.c242_ps246.mentalq.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.c242_ps246.mentalq.R
import com.c242_ps246.mentalq.ui.navigation.Routes

@Composable
fun CustomNavigationBar(
    modifier: Modifier = Modifier,
    selectedItem: Int,
    onItemSelected: (Int, String) -> Unit
) {
    val items = listOf(
        BottomNavItem("Dashboard", R.drawable.ic_home, Routes.DASHBOARD),
        BottomNavItem("Note", R.drawable.ic_note, Routes.NOTE),
        BottomNavItem("Profile", R.drawable.ic_profile, Routes.PROFILE)
    )

    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .width(300.dp)
            .height(110.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        NavigationBar(
            modifier = modifier
                .matchParentSize()
                .background(colorScheme.background, shape = RoundedCornerShape(50.dp))
                .align(Alignment.Center),
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0, 0, 0, 0),
            contentColor = colorScheme.onPrimary,
            tonalElevation = 8.dp,
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterVertically),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorScheme.secondary,
                        unselectedIconColor = colorScheme.tertiary,
                        selectedTextColor = colorScheme.onSecondary,
                        unselectedTextColor = colorScheme.onTertiary,
                        indicatorColor = Color.Transparent,
                    ),
                    icon = {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (selectedItem == index) colorScheme.secondary else Color.Transparent
                                )
                                .padding(20.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.label,
                                tint = if (selectedItem == index) colorScheme.onSecondary else colorScheme.tertiary
                            )
                        }
                    },
                    selected = selectedItem == index,
                    onClick = {
                        onItemSelected(index, item.route)
                    },
                    alwaysShowLabel = false
                )
            }
        }
    }
}


data class BottomNavItem(val label: String, val icon: Int, val route: String)

