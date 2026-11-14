package com.app.kickboxing.Activitys.presentation.Ui.home.ui.bottomNavigationBar

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.bottomNavigationBar.data.BottomNavItem
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.GrayBottomColor


@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
    val mainRoutes = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Medal.route,
        BottomNavItem.Workout.route,
        BottomNavItem.SessionDetail.route,
        BottomNavItem.Profile.route
    )

    Log.d("CurrentDestination", "$currentDestination")
    Log.d("CurrentDestination", "List ${BottomNavItem.items}")
    Log.d("CurrentDestination", "List Size: ${BottomNavItem.items.size}")

    if (currentDestination in mainRoutes) {
        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 56.dp)

                .fillMaxWidth()
                .clip(RoundedCornerShape(100.dp))
                .background(GrayBottomColor)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 26.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem.items.filterNotNull().forEach { item ->
                    val isSelected = currentDestination == item.route
                    var lastClickTime by remember { mutableStateOf(0L) }
                    val interactionSource = remember { MutableInteractionSource() }

                    IconButton(
                        onClick = {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastClickTime > 600) {
                                lastClickTime = currentTime
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier
                            .height(36.dp)
                            .width(60.dp)
                            .background(Color.Transparent),
                        interactionSource = interactionSource
                    ) {
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .width(60.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) ButtonRed else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.route,
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                }
            }
        }
    }
}



@Preview
@Composable
fun BottomNavPreview() {
    BottomNavigationBar(rememberNavController())
}
