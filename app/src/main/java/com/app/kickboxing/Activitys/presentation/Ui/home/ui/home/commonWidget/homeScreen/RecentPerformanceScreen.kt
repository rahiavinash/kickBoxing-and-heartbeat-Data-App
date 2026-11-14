package com.app.kickboxing.Activitys.presentation.Ui.home.ui.home.commonWidget.homeScreen


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.kickboxing.Activitys.domain.model.HomeRequest
import com.app.kickboxing.Activitys.domain.model.ScheduledWorkoutRequest
import com.app.kickboxing.Activitys.domain.response.HomeData
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.PastSessionsDataModel.FilterType
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TopbarComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.HeartRateViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.home.commonWidget.AverageItem
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.home.commonWidget.PerformanceItem
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BottomPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagDark
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagLight
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EndPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.StartPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.TopPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.R

@Composable
fun RecentPerformanceScreen(navController: NavController, viewModel: HeartRateViewModel?) {

    val data = viewModel?.homeData
    val powerPercentage = viewModel?.gForcePercentage?.collectAsState()?.value
    val powerIncrease = viewModel?.gForceIncrease?.collectAsState()?.value
    val scorePercentage = viewModel?.scorePercentage?.collectAsState()?.value
    val scoreIncrease = viewModel?.scoreIncrease?.collectAsState()?.value
    val punchPercentage = viewModel?.punchPercentage?.collectAsState()?.value
    val punchIncrease = viewModel?.punchIncrease?.collectAsState()?.value
    val context = LocalContext.current
    val activity = context as? HomesActivity


    LaunchedEffect(Unit) {

        if(!isInternetAvailable(context)){
            activity?.showErrorAlert("Please check your internet connection.")
        }

    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(ColorBagDark, ColorBagDark, ColorBagLight)
                )
            ),

        verticalArrangement = Arrangement.Top
    ) {

        TopbarComposable(text = "Recent Performance", onClickBack = { navController.navigateUp() }, isBackVisible = true)


        Column(modifier = Modifier
            .padding(start = StartPadding, end = EndPadding, top = TopPadding, bottom = BottomPadding)
            .fillMaxSize(),
        ) {


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {

                Text(
                    text = "LeaderBoard Rank",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 20.sp,
                        color = WhiteColor,
                        textAlign = TextAlign.Start
                    )

                )

                Text(
                    text = data?.rank.toString(),
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 20.sp,
                        color = WhiteColor,
                        textAlign = TextAlign.Start
                    )

                )

            }

            Spacer(modifier = Modifier.height(25.dp))
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {


                Text(
                  //  text =  data?.type.toString(),
                    text = "This ${data?.type?.replaceFirstChar { it.uppercase() }}",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 15.sp,
                        color = WhiteColor,
                        textAlign = TextAlign.Center
                    )
                    )

                PerformanceItem(data?.averageGForce ?: 0.0, data?.averageTotalPunches?:0.0, data?.averageScore?:0.0)

                Text(
                    text = "Previous ${data?.type?.replaceFirstChar { it.uppercase() }}",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 15.sp,
                        color = WhiteColor,
                        textAlign = TextAlign.Center
                    )
                )

                PerformanceItem(data?.previousAverageGForce?:0.0, data?.previousAverageTotalPunches?:0.0, data?.previousAverageScore?:0.0)

                Spacer(modifier = Modifier.height(10.dp))

                AverageItem(powerIncrease?:true,powerPercentage?:0.0,punchPercentage?:0.0,punchIncrease?:true,scorePercentage?:0.0,scoreIncrease?:true )
            }

        }

    }
}


fun FilterType?.displayPeriodText(): String {
    return when (this) {
        FilterType.WEEKLY -> "This Week"
        FilterType.MONTHLY -> "This Month"
        FilterType.YEARLY -> "This Year"
        FilterType.SELECT_DATE -> "Custom Dates"
        else -> "Filter"
    }
}

@Preview
@Composable
fun PreviewRecent(){
}
