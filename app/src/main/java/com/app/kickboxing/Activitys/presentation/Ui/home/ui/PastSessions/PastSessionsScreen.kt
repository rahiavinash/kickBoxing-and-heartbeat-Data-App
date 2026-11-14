package com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.app.kickboxing.Activitys.domain.model.LeaderboardDetailRequest
import com.app.kickboxing.Activitys.domain.model.SessionDetailRequest
import com.app.kickboxing.Activitys.domain.response.SessionData
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.AveragePerformanceComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.MyPointsComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.StatsComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TopbarComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.UseCase.getHeartRateProgress

import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.getPunchPower
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagDark
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagLight
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.R


@Composable
fun PastSessionsScreen(navController: NavController , viewModel:LoginViewModel){

    val mViewModel = hiltViewModel<LoginViewModel>()
    val sessionDetailState by mViewModel.sessionDetailResponseData.collectAsStateWithLifecycle()
    val pastData = remember { mutableStateOf(SessionData()) }
    val heartRate = remember { mutableStateOf(0) }
    heartRate.value = (pastData.value.averageHeartRate?: 0).toInt()
    val punchPower = remember { mutableStateOf(0) }
    punchPower.value =(pastData.value.maxPunchingSpeed?: 0).toInt()
    val heartBeatProgress = remember { mutableStateOf(0f) }
    heartBeatProgress.value = getHeartRateProgress(heartRate.value.toDouble().toInt())
    val punchPowerProgress = remember { mutableStateOf(0f) }
    punchPowerProgress.value = getPunchPower(punchPower.value.toDouble())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)){
            mViewModel.sessionDetail(SessionDetailRequest())
        }else{
            (context as HomesActivity).showErrorAlert("Please check your internet connection.")
        }

    }


    when (val result = sessionDetailState?.getContentIfNotHandled()) {
        is Results.Success -> {

                Log.e("c","{$result}")

            pastData.value = result.data.data?:SessionData()
            result.data.data?.apply {
                Log.e("dvsdvs","{$result}")
            }

        }
        is Results.Failure -> Log.e("SessionDetail", "Failed to load sessions")
        is Results.Loading -> Log.d("SessionDetail", "Loading...")
        null -> Unit
    }

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
            .padding( bottom = 5.dp)
    ) {

        TopbarComposable(text = "Past Sessions", onClickBack = { navController.navigateUp()}, isBackVisible = true)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .verticalScroll(ScrollState(0)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            MyPointsComposable(points = pastData.value.score?.toInt()?: 0)

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Your Statistics",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 20.sp,
                    color = WhiteColor
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.padding(horizontal = 25.dp)
            ) {

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatsComposable(
                            text = "Punches",
                            icon = R.drawable.ic_new_flame,
                            statCount = pastData.value.totalPunches.toString()?: "0"
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        StatsComposable(
                            text = "Power",
                            icon = R.drawable.ic_power,
                            statCount = pastData.value.maxPunchingSpeed?.toString()?:"0"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(19.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatsComposable(
                            text = "Punches/min",
                            icon = R.drawable.ic_new_flame,
                            statCount = pastData.value.maxPunchingSpeed?.toString()?: "0"
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        StatsComposable(
                            text = "Heart Rate",
                            icon = R.drawable.ic_power,
                            statCount = pastData.value.averageHeartRate?.toString()?: "0"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Average Performance",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 20.sp,
                    color = WhiteColor
                )
            )

           // Spacer(modifier = Modifier.height(5.dp))

            Column(modifier = Modifier.padding( 30.dp)) {

                AveragePerformanceComposable(
                    icon = R.drawable.ic_power_new,
                    title = "Punch Power",
                    stats ="${pastData.value.maxPunchingSpeed}",
                    progress = punchPowerProgress.value
                )

                Spacer(modifier = Modifier.height(20.dp))

                AveragePerformanceComposable(
                    icon = R.drawable.ic_hearrate,
                    title = "Heart Rate Zone",
                    stats ="${pastData.value.averageHeartRate} bpm",
                    progress = heartBeatProgress.value
                )

            //    Spacer(modifier = Modifier.height(20.dp))
//
//                AveragePerformanceComposable(
//                    icon = R.drawable.ic_workout_density,
//                    title = "Workout Density",
//                    stats = "90/100",
//                    progress = 0.8f
//                )
            }
            Spacer(modifier = Modifier.height(50.dp))

        }
    }
}

@Preview
@Composable
fun PastSessionPreview(){
    //PastSessionsScreen()
}