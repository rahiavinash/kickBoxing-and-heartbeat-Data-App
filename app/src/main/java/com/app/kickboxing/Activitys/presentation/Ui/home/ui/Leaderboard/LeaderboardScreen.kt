package com.app.kickboxing.Activitys.presentation.Ui.home.ui.Leaderboard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.app.kickboxing.Activitys.domain.model.HomeRequest
import com.app.kickboxing.Activitys.domain.model.LeaderboardDetailRequest
import com.app.kickboxing.Activitys.domain.model.SessionRequest
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertHost
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertState
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.DropdownMenuComposable
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.Leaderboard.ComposableComponents.LeaderboardComposableComponents
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.Leaderboard.DataModel.LeaderboardDataModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.PastSessionsDataModel.FilterType
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TopbarComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.home.commonWidget.homeScreen.callScheduleApi
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.*
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.R

@Composable
fun LeaderboardScreen(navController: NavController) {

    val mViewModel = hiltViewModel<LoginViewModel>()
    val leaderboardDetailState by mViewModel.leaderboardDetailResponseData.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val shouldScrollToTop = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val leaderboardItems = remember { mutableStateListOf<LeaderboardDataModel>() }
    val result = leaderboardDetailState?.getContentIfNotHandled()
    val dropdownOptions = listOf("Daily", "Weekly", "Monthly","Yearly")
    val currentUser = (result as? Results.Success)?.data?.data?.user
    val currentFilter = remember { mutableStateOf<FilterType?>(FilterType.DAILY)  }
    val currentFilterString = remember { mutableStateOf<String?>(dropdownOptions[0])  }
    val isLoadingMore = remember { mutableStateOf(false) }
    val offset = remember { mutableStateOf(0) }
    var previousIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val activity = context as? HomesActivity


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

    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)){
            mViewModel.leaderboardDetail(
                LeaderboardDetailRequest(limit = 5, offset = offset.value, type = currentFilterString.value?.lowercase())
            )
            listState.scrollToItem(0)
        }else{
            (context as HomesActivity).showErrorAlert("Please check your internet connection.")
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        val isScrollingDown = listState.firstVisibleItemIndex > previousIndex
        previousIndex = listState.firstVisibleItemIndex
        if (isScrollingDown && listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == leaderboardItems.lastIndex && !isLoadingMore.value) {
            if (!isInternetAvailable(context)) {
                activity?.showErrorAlert("Please check your internet connection.")

            } else {
                isLoadingMore.value = true
                offset.value += 5
                mViewModel.session(SessionRequest(limit = 5, offset = offset.value, type = currentFilterString.value?.lowercase()))
            }
        }
    }

    when (result) {

        is Results.Success -> {

            Log.d("leaderboardDeghbdhtail","leaderboardDetail: ${result.toString()}")

            if(offset.value == 0){
                leaderboardItems.clear()
            }
                leaderboardItems?.addAll(
                    result.data.data?.list?.mapNotNull { dto ->

                LeaderboardDataModel(
                        rank = dto.rank.toInt(),
                        imageUrl = dto?.userDetails?.profile,
                        userName = dto?.userDetails?.userName,
                        score = dto?.score?.toInt()

                    )
            } ?: emptyList()
                )

        }
        is Results.Failure -> {
            Log.e("leaderboardDetail", "Failed to load leaderboardDetail}")
        }
        is Results.Loading -> {
            Log.d("leaderboardDetail", "Loading leaderboardDetail...")
        }
        null -> {
            Log.d("leaderboardDetail", "No new leaderboardDetail")
        }

    }


    Column(
        modifier = Modifier
            .background(
                Brush.horizontalGradient(
                    listOf(ColorBagDark, ColorBagDark, ColorBagLight)
                )
            )
            .fillMaxSize()
    ) {

        TopbarComposable(
            text = "Leaderboard",
            onClickBack = {},
            isBackVisible = false
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = StartPadding, end = EndPadding, top = TopPadding, bottom = BottomPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Rank",
                        style = TextStyle (
                            fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                            fontSize = 20.sp,
                            color = WhiteColor
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    DropdownMenuComposable(
                        label = "Monthly",
                        options = dropdownOptions,
                        selectedOption = currentFilterString.value?:""
                    ) { selectedOption->
                        if (selectedOption != currentFilterString.value) {
                            currentFilterString.value = selectedOption
                            currentFilter.value = when (selectedOption) {
                                dropdownOptions[0] -> {
                                    FilterType.DAILY
                                }

                                dropdownOptions[1] -> {
                                    FilterType.WEEKLY
                                }

                                dropdownOptions[2] -> {
                                    FilterType.MONTHLY
                                }

                                dropdownOptions[3] -> {
                                    FilterType.YEARLY
                                }

                                else -> {
                                    FilterType.MONTHLY
                                }
                            }
                            offset.value = 0
                            if (!isInternetAvailable(context)) {
                                activity?.showErrorAlert("Please check your internet connection.")
                            } else {
                                mViewModel?.leaderboardDetail(
                                    LeaderboardDetailRequest(
                                        limit = 5,
                                        offset = offset.value,
                                        type = selectedOption.lowercase()
                                    )
                                )
                            }
                        }

                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                if (currentUser != null){
                    currentUser?.let { user ->
                        LeaderboardComposableComponents(
                            rank = user.rank ?: 0,
                            imageUrl = MyUtils.IMAGE_URL + (user.userDetails.profile ?: ""),
                            userName = user.userDetails.userName ?: "",
                            score = user.score.toInt() ?: 0
                        )
                    }
                }else{
                    Text(
                        text = "No Data Found",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                            fontSize = 15.sp,
                            color = WhiteColor,
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 35.dp),
                        maxLines = 2
                    )
                }


                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Top Performers",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 20.sp,
                        color = WhiteColor,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            if(leaderboardItems.isNullOrEmpty()){
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(

                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(25.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No Data Found",
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                                    fontSize = 15.sp,
                                    color = WhiteColor,
                                    textAlign = TextAlign.Start
                                ),
                                modifier = Modifier
                                    .fillMaxHeight(),
                                maxLines = 2,
                                textAlign = TextAlign.Center

                            )
                        }
                    }
                }

            }else{
                items(leaderboardItems) { user ->
                    Log.e("bfdhbfgfgj", user.toString())


                    LeaderboardComposableComponents (
                        rank = user.rank ?: 0,
                        imageUrl = MyUtils.IMAGE_URL + user.imageUrl ?: "",
                        userName = user.userName ?: "",
                        score = user.score ?: 0
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(154.dp))
            }
        }

        LaunchedEffect(shouldScrollToTop.value) {
            if (shouldScrollToTop.value) {
                listState.scrollToItem(0)
                shouldScrollToTop.value = false
            }
        }
    }
}

@Preview
@Composable
fun LeaderboardScreenPreview() {

}
