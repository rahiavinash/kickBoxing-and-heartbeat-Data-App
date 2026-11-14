package com.app.kickboxing.Activitys.presentation.Ui.home.ui.bottomNavigationBar.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.app.kickboxing.R

sealed class BottomNavItem(
    val route: String,
    val icon: Int
) {
    object Home : BottomNavItem( "Home", R.drawable.ic_home)
    object Medal : BottomNavItem( "Medal", R.drawable.icon_leaderboard)
    object Workout : BottomNavItem( "Workout",R.drawable.ic_warm_up)
    object SessionDetail : BottomNavItem("SessionDetail", R.drawable.ic_sessions)
    object Profile : BottomNavItem( "Profile", R.drawable.ic_profile)

    companion object {
        val items: List<BottomNavItem> by lazy {
            listOf(Home, Medal, Workout, SessionDetail, Profile)
        }
    }
}
