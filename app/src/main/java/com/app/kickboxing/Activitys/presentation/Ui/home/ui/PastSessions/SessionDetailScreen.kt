package com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.TimeUtils.formatDuration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.app.kickboxing.Activitys.domain.model.SessionDetailRequest
import com.app.kickboxing.Activitys.domain.model.SessionRequest
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.FilterBottomSheet
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.FilterDates
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.SessionItem
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.PastSessionsDataModel.FilterType
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.PastSessionsDataModel.PastSessionsDataModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TopbarComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.HeartRateViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.home.commonWidget.homeScreen.displayName
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BottomPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagDark
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagLight
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EndPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.StartPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.R
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(navController: NavController) {

    val mViewModel = hiltViewModel<LoginViewModel>()
    val sessionState by mViewModel.sessionResponseData.collectAsStateWithLifecycle()
    val sessionItems = remember { mutableStateListOf<PastSessionsDataModel>() }
    val listState = rememberLazyListState()
    val shouldScrollToTop = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val offset = remember { mutableStateOf(0) }
    var sessionID = remember { mutableStateOf("") }
    var currentlySelectedFilter = remember { mutableStateOf<FilterType?>(FilterType.MONTHLY) }
    var currentFilterDates = remember { mutableStateOf<FilterDates?>(null) }
    val isLoadingMore = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showPicker by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var payloadStartDate: String? = null
    var payloadEndDate: String? = null
    var previousIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val activity = context as? HomesActivity

    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)){
            mViewModel.session(SessionRequest(limit = 5, offset = offset.value))
        }else{
            (context as HomesActivity).showErrorAlert("Please check your internet connection.")
        }


    }

    // Pagination observer
    LaunchedEffect(listState.firstVisibleItemIndex) {

        val isScrollingDown = listState.firstVisibleItemIndex > previousIndex
        previousIndex = listState.firstVisibleItemIndex

            if (
                isScrollingDown &&
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == sessionItems.lastIndex &&
                !isLoadingMore.value
            ) {
                isLoadingMore.value = true
                offset.value += 5

                currentFilterDates.value = MyUtils.getDefaultFilterDates()
                val payloadType = currentlySelectedFilter.value.toString()
                if (payloadType == "custom") {
                    payloadStartDate = currentFilterDates.value?.startDateUTC
                    payloadEndDate = currentFilterDates.value?.endDateUTC
                }

                val payload = SessionRequest(
                    limit = 5,
                    offset = offset.value,
                    type = payloadType,
                    startDate = payloadStartDate,
                    endDate = payloadEndDate
                )
                mViewModel.session(payload)
            }
        }


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
                        currentFilterDates.value = filterDates
                        val payloadType = when (filterType) {
                            FilterType.DAILY -> null
                            FilterType.WEEKLY -> "weekly"
                            FilterType.MONTHLY -> "monthly"
                            FilterType.YEARLY -> "yearly"
                            FilterType.SELECT_DATE -> "custom"
                            null -> null
                        }

                        val payloadStartDate = if (filterType == FilterType.SELECT_DATE) {

                            filterDates?.startDateUTC

                        } else {
                            null
                        }
                        val payloadEndDate = if (filterType == FilterType.SELECT_DATE) {
                            filterDates?.endDateUTC
                        } else {
                            null
                        }

                        offset.value = 0
                        val payload = SessionRequest(
                            limit = 5,
                            offset = offset.value,
                            type = payloadType,
                            startDate = payloadStartDate,
                            endDate = payloadEndDate
                        )
                        if (!isInternetAvailable(context)) {
                            activity?.showErrorAlert("Please check your internet connection.")

                        } else{
                            mViewModel.session(payload)
                        }
                    }
                },
                onClose = {

                    coroutineScope.launch {
                        sheetState.hide()
                        showPicker = false
                    }
                },
                filterType = currentlySelectedFilter.value,
                filterDates = currentFilterDates.value,
                showSelectDateOption = true
            )
        }

    }


    when (val result = sessionState?.getContentIfNotHandled()) {
        is Results.Success -> {
            val dataList = result.data.data?.list.orEmpty()

            val processedItems = dataList.mapNotNull { dto ->
                dto.startTime?.let {

                    PastSessionsDataModel(
                        sessionId = dto.id,
                        date = dto.startTime.substring(0, 10),
                        workoutTime = dto.startTime.substring(11, 16),
                        workoutDuration = formatDuration(dto.startTime, dto.endTime),
                        points = dto.score?.toInt() ?: 0,
                        punches = dto.totalPunches?.toInt() ?: 0,
                        power = dto.maxPunchingSpeed?.toInt() ?: 0,
                        punches_min = dto.averagePunchingSpeed?.toInt() ?: 0,
                        heartRate = dto.averageHeartRate?.toInt() ?: 0
                    )
                }
            }

            if (offset.value == 0) {
                sessionItems.clear()
            }

            sessionItems.addAll(processedItems)

            isLoadingMore.value = false

            if (processedItems.isEmpty() && offset.value == 0) {
                Log.d("SessionList", "No data for this filter.")
            }
        }

        is Results.Failure -> {
            Log.e("SessionDetail", "Failed to load sessions: ${result.message}")
            isLoadingMore.value = false
        }

        is Results.Loading -> {
            Log.d("SessionDetail", "Loading...")
            isLoadingMore.value = true
        }

        null -> Unit
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                shouldScrollToTop.value = true
            }
        }
        val lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TopbarComposable(text = "Past Sessions", onClickBack = {}, isBackVisible = false)
        Spacer(modifier = Modifier.height(10.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 25.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Sessions",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 20.sp,
                    color = colorResource(id = com.hb.dialog.R.color.dialog_white)
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
                    text = currentlySelectedFilter.value.displayName(),
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
                        .width(IntrinsicSize.Min)
                        .padding(end = 8.dp),
                    tint = colorResource(com.hb.dialog.R.color.dialog_white)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 30.dp),
            contentAlignment = Alignment.Center

        ) {
            if (sessionItems.isNullOrEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Past Session Available",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                            fontSize = 15.sp,
                            color = WhiteColor,

                            ),
                        modifier = Modifier
                            .fillMaxHeight(),
                        maxLines = 2,
                        textAlign = TextAlign.Center

                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = StartPadding,
                            end = EndPadding,
                            top = 0.dp,
                            bottom = BottomPadding
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(sessionItems.size) { index ->
                        val item = sessionItems[index]


                        SessionItem(
                            date = item.date.toString(),
                            workoutTime = item.workoutTime.toString(),
                            workoutDuration = item.workoutDuration.toString(),
                            points = item.points ?: 0,
                            punches = item.punches ?: 0,
                            power = item.power ?: 0,
                            punches_min = item.punches_min ?: 0,
                            heartRate = item.heartRate ?: 0,
                            onClick = {
                                navController.navigate("pastSessions/${item.sessionId ?: ""}")
                            }
                        )
                    }


                    if (isLoadingMore.value) {
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            androidx.compose.material3.CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(150.dp))
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun formatDuration(start: String?, end: String?): String {
    return try {
        val s = java.time.Instant.parse(start)
        val e = java.time.Instant.parse(end)
        val dur = java.time.Duration.between(s, e)

        val totalSeconds = dur.seconds
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        String.format("%02d:%02d", minutes, seconds)
    } catch (e: Exception) {
        ""
    }
}

