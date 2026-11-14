package com.app.kickboxing.Activitys.presentation.Ui.home.ui.home.commonWidget.homeScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import java.time.LocalDate
import java.util.Locale
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import com.google.gson.Gson
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.kickboxing.Activitys.domain.model.HomeRequest
import com.app.kickboxing.Activitys.domain.model.ScheduledWorkoutRequest
import com.app.kickboxing.Activitys.domain.response.HomeData
import com.app.kickboxing.Activitys.domain.response.WorkoutData
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertHost
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertState
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertType
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.RoundedImage
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.FilterBottomSheet
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.FilterDates
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.PastSessionsDataModel.FilterType
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.ProgressBar
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.ScheduledWorkouts.ComposableComponents.ScheduledWorkoutsComposableComponents
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.HeartRateViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.bottomNavigationBar.data.BottomNavItem
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.home.commonWidget.OverViewComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.AppFonts
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BottomPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagDark
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagLight
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EndPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.StartPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.TextOffWhite
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.Activitys.presentation.permission.LocationPermissionHandler
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, heartViewModel: HeartRateViewModel?) {

    val isFirstTime = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val mViewModel = hiltViewModel<LoginViewModel>()
    val getWorkoutState by mViewModel.scheduleWorkoutResponseData.collectAsStateWithLifecycle()
    val homeState by mViewModel.homeResponseData.collectAsStateWithLifecycle()
    val workoutResult = getWorkoutState?.getContentIfNotHandled()
    val workoutMiniList = remember { mutableStateListOf<WorkoutData>() }
    var workoutList by remember { mutableStateOf<List<WorkoutData>>(emptyList()) }
    val scheduleList = remember { mutableStateOf("") }
    val rank = remember { mutableStateOf(0) }
    val averageTotalPunches = remember { mutableStateOf(0.0) }
    val averageGForce = remember { mutableStateOf(0.0) }
    val averageScore = remember { mutableStateOf(0.0) }
    val previousAverageTotalPunches = remember { mutableStateOf(0.0) }
    val previousAverageGForce = remember { mutableStateOf(0.0) }
    val previousAverageScore = remember { mutableStateOf(0.0) }
    val homeData = remember { mutableStateOf(HomeData()) }
    val result = homeState?.getContentIfNotHandled()
    val joinedSession by mViewModel.joinSession.collectAsState()
    val currentlyJoinedSessionId by mViewModel.currentlyJoinedSessionId.collectAsState()
    val context = LocalContext.current
    val currentDate = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd", Locale.getDefault()))
    }



    /**  HERE WE HANDLE THE LOCATION PERMISSION TO  CHECK USER IS NEAR TO THE  THE GYM*/
    LocationPermissionHandler(
        onGranted = {

            if (isInternetAvailable(context)){
                callScheduleApi(mViewModel)
            }else{
                (context as HomesActivity).showErrorAlert("Please check your internet connection.")
            }
        },
        onDenied = {

        },
        onPermanentlyDenied = {
            Log.d("HomeScreen", "joinedSession changed:1")
        }
    )



    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)){
            mViewModel.home(HomeRequest(
                type = Prefs.init().isSelectedFilter?:"week"
            ))
            callScheduleApi(mViewModel)
        }else{
            (context as HomesActivity).showErrorAlert("Please check your internet connection.")
        }

    }



    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                isFirstTime.value = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when (result) {
        is Results.Success -> {
            homeData.value = result.data.data ?: HomeData()
            rank.value = homeData.value.rank ?: 0
            averageTotalPunches.value = homeData.value.averageTotalPunches ?: 0.0
            averageGForce.value = homeData.value.averageGForce ?: 0.0
            averageScore.value = homeData.value.averageScore ?: 0.0
            previousAverageTotalPunches.value = homeData.value.previousAverageTotalPunches ?: 0.0
            previousAverageGForce.value = homeData.value.previousAverageGForce ?: 0.0
            previousAverageScore.value = homeData.value.previousAverageScore ?: 0.0
            heartViewModel?.updateHomeData(result.data.data ?: HomeData())
        }

        else -> Unit
    }

    when (workoutResult) {
        is Results.Success -> {
            workoutMiniList.clear()
            workoutResult.data.data?.list?.let {
                workoutMiniList.addAll(it.take(2))
                workoutList = it
                scheduleList.value = Uri.encode(Gson().toJson(workoutList))
                Prefs.init().scheduleList = it

                Log.d("HeartRateViewModel", "Saved scheduleList to Prefs: $it")
            }
        }

        else -> Unit
    }

    val (gForcePercentage, gForceIncrease) = comparePercentage(previousAverageGForce.value, averageGForce.value)
    val (scorePercentage, scoreIncrease) = comparePercentage(previousAverageScore.value, averageScore.value)
    val (punchPercentage, punchIncrease) = comparePercentage(previousAverageTotalPunches.value, averageTotalPunches.value)

    val colors = listOf(ColorBagDark, ColorBagDark, ColorBagLight)
    val gradient = Brush.horizontalGradient(colors = colors)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(start = StartPadding, end = EndPadding, top = 10.dp, bottom = BottomPadding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RoundedImage(
                    url = Prefs.init().myProfileData.profile?.let { MyUtils.IMAGE_URL + it } ?: "",
                    size = 50
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Prefs.init().myProfileData.userName ?: "",
                        style = TextStyle(color = TextOffWhite, fontSize = 18.sp, fontFamily = AppFonts.DmSemiBold)
                    )
                    Text(
                        text = currentDate,
                        style = TextStyle(color = TextOffWhite, fontSize = 18.sp, fontFamily = AppFonts.DmMedium)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.notification),
                    contentDescription = "Notification",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(35.dp))
                        .border(1.dp, BorderColor, RoundedCornerShape(35.dp))
                        .clickable { navController.navigate("notification") },
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(16.dp))


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 150.dp)
            ) {
                item {
                    if (Prefs.init().joinedSession != null) {
                        Prefs.init().joinedSession?.let { CurrentSession(navController, workoutData = it, mViewModel) }
                        Spacer(modifier = Modifier.height(35.dp))
                    }

                    Log.d("sesssion","${Prefs.init().joinedSession} ${joinedSession?.id} $currentlyJoinedSessionId")

                    OverviewBox(
                        koPoints = homeData.value.averageScore ?: 0.0,
                        rank = homeData.value.rank ?: 0,
                        navController
                    )

                    Spacer(modifier = Modifier.height(35.dp))

                    RecentPerformanceBox(
                        navController,
                        punchCount = homeData.value.averageTotalPunches ?: 0.0,
                        avgPower = homeData.value.averageGForce ?: 0.0,
                        punchPercentage = punchPercentage,
                        punchIncrease = punchIncrease,
                        powerPercentage = gForcePercentage,
                        powerIncrease = gForceIncrease
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Scheduled Workouts",
                            style = TextStyle(color = TextOffWhite, fontSize = 20.sp, fontFamily = AppFonts.DmMedium)
                        )

                        if (workoutMiniList.size > 1) {
                            Text(
                                text = "View All",
                                style = TextStyle(color = TextOffWhite, fontSize = 14.sp, fontFamily = AppFonts.DmMedium),
                                modifier = Modifier.clickable { navController.navigate("scheduleWorkout") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (workoutMiniList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No Scheduled Workout.",
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                                    fontSize = 15.sp,
                                    color = WhiteColor
                                )
                            )
                        }
                    }
                } else {
                    items(workoutMiniList.size) { index ->
                        val item = workoutMiniList[index]
                            ScheduledWorkoutsComposableComponents(
                                data = item,
                                currentlyJoinedSessionId = currentlyJoinedSessionId,
                                workoutViewModel = mViewModel,
                                onJoinToggle = { joined ->
                                    workoutMiniList[index] = item.copy(joined = joined)
                                }
                            )


                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentSession(navController: NavController, workoutData: WorkoutData ,mViewModel:LoginViewModel) {

    val parsedStartTime = remember(workoutData.startTime) {
        workoutData.startTime?.let {
            OffsetDateTime.parse(it).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        }
    }

    val parsedEndTime = remember(workoutData.endTime) {
        workoutData.endTime?.let {
            OffsetDateTime.parse(it).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        }
    }

    val currentTime = remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = LocalDateTime.now()
            delay(1000)
        }
    }

    val progress = if (parsedStartTime != null && parsedEndTime != null) {
        val total = Duration.between(parsedStartTime, parsedEndTime).seconds.toFloat()
        val elapsed = Duration.between(parsedStartTime, currentTime.value).seconds.toFloat().coerceIn(0f, total)
        if (total > 0) elapsed / total else 0f
    } else 0f

    if (progress== 1f){
        callScheduleApi(mViewModel)
        mViewModel.updateCurrentIdIsNull()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .clickable {
                navController.navigate(BottomNavItem.Workout.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            .background(BlackCardBackground)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.award),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Current Session", style = TextStyle(color = Color.White, fontSize = 18.sp, fontFamily = AppFonts.DmMedium))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Progress", style = TextStyle(color = TextOffWhite, fontSize = 14.sp, fontFamily = AppFonts.DmMedium))
                    Text("${(progress * 100).toInt()}%", style = TextStyle(color = Color.White, fontSize = 14.sp, fontFamily = AppFonts.DmMedium))
                }
                Spacer(modifier = Modifier.height(5.dp))
                ProgressBar(progress = progress)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewBox(koPoints: Double, rank: Int,navController: NavController) {


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showPicker by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val currentlySelectedFilter = rememberSaveable  { mutableStateOf<FilterType?>(FilterType.WEEKLY) }
    val currentFilterDates = remember { mutableStateOf<FilterDates?>(null) }
    val mViewModel = hiltViewModel<LoginViewModel>()
    val context = LocalContext.current


    if (showPicker) {

        ModalBottomSheet(
            onDismissRequest = { showPicker = false },
            sheetState = sheetState
        ) {

            FilterBottomSheet(
                onDone = { filterType, filterDates ->
                    coroutineScope.launch {
                        sheetState.hide()
                        showPicker = false
                        currentlySelectedFilter.value = filterType

                        val payloadType = when (filterType) {
                            FilterType.DAILY -> null
                            FilterType.WEEKLY -> "week"
                            FilterType.MONTHLY -> "month"
                            FilterType.YEARLY -> "year"
                            FilterType.SELECT_DATE -> "custom"
                            null -> null
                        }
                        Prefs.init().isSelectedFilter = payloadType
                        if (!isInternetAvailable(context)) {
                            (context as HomesActivity).showErrorAlert("Please check your internet connection.")
                        } else {
                            // Filter api is calling
                            mViewModel.home(HomeRequest(payloadType))
                        }


                    }
                },
                onClose = {

                    coroutineScope.launch {
                        sheetState.hide()
                        showPicker = false
                    }
                },
                filterType = if (Prefs.init().isSelectedFilter=="year"){
                    FilterType.YEARLY
                }else if(Prefs.init().isSelectedFilter=="month") {
                    FilterType.MONTHLY
                }else  {
                    FilterType.WEEKLY
                },
                filterDates = currentFilterDates.value,
                showSelectDateOption = false
            )
        }

    }
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Overview",
            style = TextStyle(
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = AppFonts.DmMedium
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .height(38.dp)
                .wrapContentWidth()
                .background(
                    color = Color(0xE2FF0000),
                    shape = RoundedCornerShape(75.dp),
                )
                .clickable {
                    showPicker = true
                }
                .padding(horizontal = 11.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            Text(
             text = if (!Prefs.init().isSelectedFilter.isNullOrEmpty()) {
                Prefs.init().isSelectedFilter.toString().lowercase().replaceFirstChar { it.uppercase() } + "ly"
            } else {
                "Weekly"
            },
                modifier = Modifier.padding(5.dp),
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 12.sp,
                    color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = "Icon Filter",
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp),
                tint = colorResource(com.hb.dialog.R.color.dialog_white)
            )
        }

    }


    Spacer(modifier = Modifier.height(15.dp))
    Row(  modifier = Modifier.fillMaxWidth(1f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {


        Box(modifier = Modifier.weight(1f)) {
            OverViewComposable(
                "KO Points",
                R.drawable.ic_new_flame,
                koPoints.toInt(),
                "Start New Workout"
            ){}
        }
        Spacer(modifier = Modifier.width(18.dp))

        Box(
            modifier = Modifier
                .weight(1f).fillMaxSize()

        )
        {

            OverViewComposable("Leaderboard", R.drawable.ic_leaderboard, rank, "View Leaderboard") {
                navController.navigate(BottomNavItem.Medal.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }


        }
    }


}


fun FilterType?.displayName(): String {
    return when (this) {
        FilterType.WEEKLY -> "Weekly"
        FilterType.MONTHLY -> "Monthly"
        FilterType.YEARLY -> "Yearly"
        FilterType.SELECT_DATE -> "Custom"
        else -> "Filter"
    }
}


@Composable
fun RecentPerformanceBox(navController: NavController,punchCount: Double,
                         avgPower: Double,
                         punchPercentage: Double,
                         punchIncrease: Boolean,
                         powerPercentage: Double,
                         powerIncrease: Boolean) {


    Row( modifier = Modifier
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = "Recent Performance",
            style = TextStyle(
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = AppFonts.DmMedium
            ),
            color = Color.White,
        )


        Text(text = "View More" ,
            style = TextStyle(
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = AppFonts.DmMedium
        ),
            modifier = Modifier.clickable(
                enabled = true,
                onClickLabel = "",
                onClick = {
                    navController.navigate("recentPerformanceScreen")

                },
                role = null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
            color = TextOffWhite
        )
    }

    Spacer(modifier = Modifier.height(15.dp))
    Row(horizontalArrangement = Arrangement.SpaceBetween ,
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f)){
            RecentPerformance("Avg. Punch Count" ,  if (punchIncrease) R.drawable.ic_up_arrow else R.drawable.ic_down_arrow,
                punchCount ,  "${if (punchIncrease) "+" else "-"}${punchPercentage}%","From Previous")
        }
        Spacer(modifier = Modifier.width(18.dp))
        Box(modifier = Modifier.weight(1f)){
            RecentPerformance("Avg. Power" , if (powerIncrease) R.drawable.ic_up_arrow else R.drawable.ic_down_arrow, avgPower ,  "${if (powerIncrease) "+" else "-"}${powerPercentage}%" ,"From Previous")
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduledBox(
    navController: NavController,
    workoutData: WorkoutData,
    mViewModel: LoginViewModel,
    currentlyJoinedSessionId: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(8.dp)
    ) {



    }
}
fun callScheduleApi(mViewModel: LoginViewModel){
    mViewModel.scheduledWorkout(ScheduledWorkoutRequest(2, 0))
}

fun comparePercentage( previous: Double, current: Double): Pair<Double, Boolean> {
    if (previous == 0.0) {

        return Pair(0.0, current >= 0)
    }

    val change = current - previous
    val percentageChange = (change / abs(previous)) * 100
    val isIncrease = change >= 0

    val roundedPercentage = round(abs(percentageChange) * 100) / 100

    return Pair(roundedPercentage, isIncrease)
}

enum class FilterType { DAILY, WEEKLY, MONTHLY, YEARLY, SELECT_DATE }

val saved = Prefs.init().isSelectedFilter

val filterType = try {
    FilterType.valueOf(saved!!.uppercase())

} catch (e: Exception) {
    null
}


