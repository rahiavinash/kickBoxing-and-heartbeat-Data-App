package com.app.kickboxing.Activitys.presentation.Ui.home.ui.ScheduledWorkouts


import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.app.kickboxing.Activitys.domain.model.ScheduledWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.SessionRequest
import com.app.kickboxing.Activitys.domain.response.WorkoutData
import com.app.kickboxing.Activitys.presentation.Ui.Activities.AuthActivity
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertHost
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertState
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertType
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.ScheduledWorkouts.ComposableComponents.ScheduledWorkoutsComposableComponents
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TopbarComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BottomPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagDark
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagLight
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EndPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.StartPadding
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.Activitys.utils.MyUtils.isNetworkAvailable
import com.google.gson.Gson


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduledWorkoutsScreen(navController: NavController,workoutData: WorkoutData, mViewModel: LoginViewModel) {

    val getWorkoutState by mViewModel.scheduleWorkoutResponseData.collectAsStateWithLifecycle()
    val workoutResult = getWorkoutState?.getContentIfNotHandled()
    var workoutList by remember { mutableStateOf<List<WorkoutData>>(emptyList()) }
    var scheduleList = remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val offset = remember { mutableStateOf(0) }
    val isLoadingMore = remember { mutableStateOf(false) }
    var previousIndex by remember { mutableStateOf(0) }
    val currentlyJoinedSessionId by mViewModel.currentlyJoinedSessionId.collectAsState()
    val context = LocalContext.current
    var alertState by remember { mutableStateOf(AlertState()) }

    LaunchedEffect(currentlyJoinedSessionId) {
        if (!isInternetAvailable(context)) {

            alertState = AlertState(
                message = "Please check your internet connection.",
                type = AlertType.ERROR,
                isVisible = true
            )
        } else {
            mViewModel.scheduledWorkout(ScheduledWorkoutRequest(5, 0))
        }
    }



    LaunchedEffect(listState.firstVisibleItemIndex) {

        val isScrollingDown = listState.firstVisibleItemIndex > previousIndex
        previousIndex = listState.firstVisibleItemIndex
        if (isScrollingDown && listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == workoutList.lastIndex && !isLoadingMore.value) {
            isLoadingMore.value = true
            offset.value += 5

            val payload = SessionRequest(
                limit = 5,
                offset = offset.value
                )
            mViewModel.session(payload)
        }
    }


    when (workoutResult) {
        is Results.Success -> {
            workoutResult.data.data?.list?.let {
                workoutList = it
                scheduleList.value = Uri.encode(Gson().toJson(it))

                Log.e("workout","$workoutResult")
            }

        }
        is Results.Failure -> {
            // Handle failure
            Log.e("Profile", "Failed to fetch profile data")
        }
        is Results.Loading -> {
            // Show loading indicator
        }
        null -> {
            // No new event
        }
    }

    Log.d("scheduleScreen","$currentlyJoinedSessionId")

    Column(
        modifier = Modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        ColorBagDark,
                        ColorBagDark,
                        ColorBagLight
                    )
                )
            )
            .padding(bottom = BottomPadding)
    ) {
        AlertHost(
            alertState = alertState,
            onDismiss = {
                alertState = alertState.copy(isVisible = false)
            }
        )
        TopbarComposable(text = "Scheduled Workouts", onClickBack = { navController.navigateUp() }, isBackVisible = true)
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp  , start = StartPadding , end = EndPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


                items(workoutList.size) { index ->
                    var item = workoutList[index]

                    ScheduledWorkoutsComposableComponents(
                        data = item,
                        currentlyJoinedSessionId = currentlyJoinedSessionId,
                        workoutViewModel = mViewModel,
                        onJoinToggle = { joined ->
                            item  = item.copy(joined = joined)
                        }
                    )
                }

              //  ScheduledBox(navController, workoutData = user ,mViewModel.currentlyJoinedSessionId.collectAsState().value.toString())

        }
    }

}


@Preview
@Composable
fun ScheduledPreview(){
    // ScheduledWorkoutsScreen()
}
