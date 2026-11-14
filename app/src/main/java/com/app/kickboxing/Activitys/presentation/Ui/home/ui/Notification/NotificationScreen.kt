package com.app.kickboxing.Activitys.presentation.Ui.home.ui.Notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.kickboxing.Activitys.domain.model.NotificationRequest
import com.app.kickboxing.Activitys.domain.response.NotificationData
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.Notification.ComposableComponents.NotificationItemComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TopbarComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BottomPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagDark
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagLight
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EndPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.StartPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils.getRelativeNotificationDateLabel
import com.app.kickboxing.Activitys.utils.MyUtils.parseTimeAmPm
import com.app.kickboxing.R

@Composable
fun NotificationScreen(navController: NavController) {
    val mViewModel = hiltViewModel<LoginViewModel>()
    val notificationState by mViewModel.notificationResponseData.collectAsStateWithLifecycle()
    val result = notificationState?.getContentIfNotHandled()
    val notificationList = remember { mutableStateListOf<NotificationData>() }
    val listState = rememberLazyListState()
    val offset = remember { mutableStateOf(0) }
    var previousIndex by remember { mutableStateOf(0) }
    val isLoadingMore = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        mViewModel.notification(NotificationRequest(offset = 0, limit = 5))
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        val isScrollingDown = listState.firstVisibleItemIndex > previousIndex
          previousIndex = listState.firstVisibleItemIndex

        if (
            isScrollingDown &&
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == notificationList.lastIndex &&
            !isLoadingMore.value
        ) {
            isLoadingMore.value = true
            offset.value += 5
            mViewModel.notification(NotificationRequest(limit = 5, offset = offset.value))
        }
    }

    when (result) {
        is Results.Success -> {
            val dataList = result.data.data?.list.orEmpty()
            if (offset.value == 0) {
                notificationList.clear()
            }
            notificationList.addAll(dataList)
            isLoadingMore.value = false
        }
        else -> Unit
    }

    val groupedNotifications = notificationList
        .sortedByDescending { it.createdAt }
        .groupBy { notification ->
            getRelativeNotificationDateLabel(notification.createdAt ?: "")
        }

    Column(
        modifier = Modifier.background(
            brush = Brush.horizontalGradient(listOf(ColorBagDark, ColorBagDark, ColorBagLight))
        )
    ) {
        TopbarComposable(
            text = "Notifications",
            onClickBack = { navController.navigateUp() },
            isBackVisible = true
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = StartPadding, end = EndPadding, top = 3.dp, bottom = BottomPadding)
        ) {
            if (notificationList.isEmpty()) {
                item {

                    Text(
                        text = "No Notifications yet.",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                            fontSize = 15.sp,
                            color = WhiteColor,

                            ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 75.dp),
                        maxLines = 2,
                        textAlign = TextAlign.Center

                    )
                }
            } else {
                groupedNotifications.forEach { (dateLabel, notificationsForDate) ->
                    item {
                        Text(
                            text = dateLabel,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(notificationsForDate) { notification ->
                        NotificationItemComposable(
                            isSameDate = true,
                            title = notification.type.orEmpty(),
                            description = notification.message.orEmpty(),
                            date = dateLabel,
                            time = parseTimeAmPm(notification.createdAt.orEmpty())
                        )
                    }


                }
            }
        }
    }
}

@Preview
@Composable
fun NotificationPreview() {
    NotificationScreen(rememberNavController())
}
