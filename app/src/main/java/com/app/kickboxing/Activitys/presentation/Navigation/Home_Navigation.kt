package com.app.kickboxing.Activitys.presentation.Navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.kickboxing.Activitys.domain.model.SessionDetailRequest
import com.app.kickboxing.Activitys.domain.response.WorkoutData
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.WithoutTrackingScreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.EditProfileScreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.Leaderboard.LeaderboardScreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.Notification.NotificationScreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.PastSessionsScreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.SessionDetailScreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.ScheduledWorkouts.ScheduledWorkoutsScreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.Settings.SettingsScreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.HeartRateViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.TrackWorkout
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.bottomNavigationBar.data.BottomNavItem
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.home.commonWidget.homeScreen.HomeScreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.home.commonWidget.homeScreen.RecentPerformanceScreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagDark
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagLight
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.StatusBarColor
import com.app.kickboxing.Activitys.utils.Prefs
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Routes {
    const val Home = "home"
    const val ScheduleWorkout = "scheduleWorkout"
    const val Notification = "notification"
    const val RecentPerformanceScreen = "recentPerformanceScreen"
    const val EditProfile = "editProfile"
    const val TrackWorkout = "trackWorkout"
    const val PastSessions = "pastSessions"
    const val SessionDetail = "sessionDetail"
    const val WithoutTracking = "withoutTracking"
    const val SESSION_ID_ARG = "sessionId"
    const val SCHEDULE_LIST = "sessionList"
    const val WORKOUT_LIST = "workoutList"
    const val LEADERBOARD = "leaderboard"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues,
    mViewModel: HeartRateViewModel?,
    viewModel: LoginViewModel
) {
    val systemUiController = rememberSystemUiController()
    val gradient = Brush.horizontalGradient(
        colors = listOf(ColorBagDark, ColorBagDark, ColorBagLight)
    )

    systemUiController.setStatusBarColor(color = StatusBarColor)

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(navController  , mViewModel)
        }

        composable(BottomNavItem.Medal.route) {
            Prefs.init().isSelectedFilter="week"
            LeaderboardScreen(navController)
        }

        composable(BottomNavItem.Workout.route) {
            Prefs.init().isSelectedFilter="week"
            TrackWorkout(navController, mViewModel)
        }

        composable(BottomNavItem.SessionDetail.route) {
            Prefs.init().isSelectedFilter="week"
            SessionDetailScreen(navController)
        }

        composable(BottomNavItem.Profile.route) {
            Prefs.init().isSelectedFilter="week"
            SettingsScreen(navController,mViewModel)
        }

        composable(Routes.ScheduleWorkout){
            ScheduledWorkoutsScreen(navController, workoutData = WorkoutData(),viewModel)
        }


        composable(Routes.Notification) {
            NotificationScreen(navController)
        }

        composable(Routes.RecentPerformanceScreen) {
                RecentPerformanceScreen(navController, mViewModel)
        }

        composable(Routes.EditProfile) {
            EditProfileScreen(navController)
        }

        composable(Routes.WithoutTracking) {
            WithoutTrackingScreen(navController , mViewModel)
        }

        composable(
            route = "${Routes.PastSessions}/{${Routes.SESSION_ID_ARG}}",
            arguments = listOf(
                navArgument(Routes.SESSION_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString(Routes.SESSION_ID_ARG) ?: ""
            val viewModel: LoginViewModel = hiltViewModel()
            viewModel.sessionDetail(SessionDetailRequest(sessionId))

            PastSessionsScreen(navController, viewModel)
        }
    }
}
