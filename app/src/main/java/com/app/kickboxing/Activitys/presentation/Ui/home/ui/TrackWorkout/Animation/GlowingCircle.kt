package com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.Animation

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.Brown
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.kickboxing.Activitys.domain.model.JoinWorkoutRequest
import com.app.kickboxing.Activitys.domain.response.WorkoutData
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.HeartRateViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.canRunWorkout
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.R
import kotlinx.coroutines.delay

@Composable

fun GlowingCircle(
    viewModel: HeartRateViewModel ,
    onClickTimer: (Boolean) -> Unit,
    timeInSecond: (Int) -> Unit,
    isRunningExternal: Boolean

) {
    
 //   deviceViewModel?.deviceStatus?.collectAsState()?.value
    val time = viewModel.timeInSeconds.collectAsState()
    val isRunning = viewModel.isRunning.collectAsState()
    val deviceName = viewModel.deviceName.collectAsState().value
    val boxingName = viewModel.boxingDeviceName.collectAsState().value
    var isInsideGeofence by remember { mutableStateOf(false) }

    var context = LocalContext.current
    val mViewModel = hiltViewModel<LoginViewModel>()

    val minutes = time.value / 60
    val seconds = time.value % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)
    var internalRunning by remember { mutableStateOf(false) }

    // Sync geofence-driven state
    LaunchedEffect(isRunningExternal) {
        if (isRunningExternal != internalRunning) {
            internalRunning = isRunningExternal
            onClickTimer(internalRunning)
        }
    }
    val transition = rememberInfiniteTransition(label = "glow")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val workoutData = remember { Prefs.init().joinedSession }
    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .background(shape = CircleShape, color = ButtonRed)
                .size(165.dp)
                .clickable {
                    if (deviceName.isNullOrEmpty()){
                        (context as HomesActivity).showErrorAlert("Please connect your heart rate sensor.")
                        return@clickable
                    }else if (boxingName.isNullOrEmpty()){
                        (context as HomesActivity).showErrorAlert("Please connect your boxing sensor.")
                        return@clickable
                    }
                    if (!deviceName.isNullOrEmpty()){
                        if (isRunning.value) {
//                            mViewModel.joinWorkoutWithApi(
//                                JoinWorkoutRequest(workoutData?.id),
//                                workoutData = WorkoutData()
//                            )
                            viewModel.stopTimer(onClickTimer)
                        } else {
                            viewModel.startTimer(
                                onTick = timeInSecond,
                                onSession = onClickTimer
                            )
                        }
                    }

                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formattedTime,
                color = Color.White,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 40.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (isRunning.value) "Tap To Stop & Reset" else "Press To Start Workout",
                color = Color.White,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 12.sp
                )
            )
        }

        listOf(195.dp to 0.2f, 180.dp to 0.4f, 165.dp to 0.8f).forEach { (size, opacity) ->
            Box(
                modifier = Modifier
                    .size(size)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = if (isRunning.value) alpha else 0.6f
                    }
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(2.dp, Brown.copy(alpha = opacity), CircleShape)
            )
        }
    }
}


//fun GlowingCircle(onClickTimer: (Boolean) -> Unit
//                  ,timeInSecond:(Int) ->Unit) {
//    var isRunning by remember { mutableStateOf(false) }
//    var timeInSeconds by remember { mutableStateOf(0) }
//
//    LaunchedEffect(isRunning) {
//        onClickTimer(isRunning)
//        while (isRunning) {
//            delay(1000L)
//            timeInSeconds++
//            timeInSecond(timeInSeconds)
//        }
//    }
//
//    val minutes = timeInSeconds / 60
//    val seconds = timeInSeconds % 60
//    val formattedTime = String.format("%02d:%02d", minutes, seconds)
//
//    val transition = rememberInfiniteTransition(label = "glow")
//    val scaleFinal by transition.animateFloat(
//        initialValue = 1f,
//        targetValue = 1.3f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 1200, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "scale"
//    )
//
//    val scaleInitial by transition.animateFloat(
//        initialValue = 1.1f,
//        targetValue = 1.1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 1200, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "scale"
//    )
//
//    val alpha by transition.animateFloat(
//        initialValue = 0.2f,
//        targetValue = 0.6f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 1200, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "alpha"
//    )
//
//    Box(contentAlignment = Alignment.Center) {
//        Column(
//            modifier = Modifier
//                .padding(vertical = 5.dp, horizontal = 5.dp)
//                .background(shape = CircleShape, color = ButtonRed)
//                .size(165.dp)
//                .clickable(
//                    enabled = true,
//                    onClickLabel = "",
//                    onClick = {
//                        if (isRunning) {
//                            // Stop & Reset Timer
//                            isRunning = false
//                            timeInSeconds = 0
//                        } else {
//                            // Start Timer
//                            isRunning = true
//                        }
//                    },
//                    role = null,
//                    indication = null,
//                    interactionSource = remember { MutableInteractionSource() }
//                ),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = formattedTime,
//                color = Color.White,
//                style = TextStyle(
//                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
//                    fontSize = 40.sp
//                )
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//            Text(
//                text = if (isRunning) "Tap To Stop & Reset" else "Press To Start Workout",
//                color = Color.White,
//                style = TextStyle(
//                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
//                    fontSize = 12.sp
//                ),
//                maxLines = 1
//            )
//        }
//
//        // Glowing Circles
//        listOf(195.dp to 0.2f, 180.dp to 0.4f, 165.dp to 0.8f).forEach { (size, opacity) ->
//            Box(
//                modifier = Modifier
//                    .size(size)
//                    .graphicsLayer {
//                        val scale = if (isRunning) scaleFinal else scaleInitial
//                        scaleX = scale
//                        scaleY = scale
//                        this.alpha = if (isRunning) alpha else 0.6f
//                    }
//                    .clip(CircleShape)
//                    .background(Color.Transparent)
//                    .border(
//                        width = 2.dp,
//                        color = Brown.copy(alpha = opacity),
//                        shape = CircleShape
//                    )
//            )
//        }
//    }
//}



//@Preview
//@Composable
//fun TimerPreview(){
//    GlowingCircle({
//        Log.e("isValueChnaged", "fjkds")
//    } , {
//        Log.e("isValueChnaged", "fjkds")
//    })
//}
