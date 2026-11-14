package com.app.kickboxing.Activitys.presentation.Ui.home.ui.ScheduledWorkouts.ComposableComponents

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.kickboxing.Activitys.domain.model.CancelWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.JoinWorkoutRequest
import com.app.kickboxing.Activitys.domain.response.WorkoutData
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.HeartRateViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.HintColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils
import com.app.kickboxing.Activitys.utils.MyUtils.parseDayAndMonth
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.R
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduledWorkoutsComposableComponents(
    data: WorkoutData,
    currentlyJoinedSessionId: String?,
    workoutViewModel: LoginViewModel = hiltViewModel(),
    onJoinToggle: (Boolean) -> Unit)
{
    var isJoined by remember { mutableStateOf(data.joined) }
    var isJoinableTime by remember { mutableStateOf(false) }
    val joinWorkoutState by workoutViewModel.joinWorkoutResponseData.collectAsStateWithLifecycle()
    val cancelWorkoutState by workoutViewModel.cancelWorkoutResponseData.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isInsideGeofence by remember { mutableStateOf(false) }
    Log.d("sessionIDis","$currentlyJoinedSessionId $isJoined ")
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val parsedStartTime = remember(data.startTime) {
        try {
            OffsetDateTime.parse(data.startTime)
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalTime()
        } catch (e: Exception) {
            null
        }
    }



    val parsedEndTime = remember(data.endTime) {
        try {
            OffsetDateTime.parse(data.endTime)
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalTime()
        } catch (e: Exception) {
            null
        }
    }


    //For location check we are near to the GYM

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                for (location in locationResult.locations) {
                    val inside = checkGeofence(
                        location.latitude,
                        location.longitude,
                        targetLat = data.cords?.get(0) ?: 0.0,
                        targetLng = data.cords?.get(1) ?: 0.0,
                    )
                    isInsideGeofence = inside
                    Log.d("GeoCheck", "Update check: $inside (lat=${location.latitude}, lng=${location.longitude})")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L // update every 5 seconds
        ).build()

        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("GeoCheck", "Requesting lastLocation...")

            // Immediate check for last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val inside = checkGeofence(
                        location.latitude,
                        location.longitude,
                        targetLat = data.cords?.get(0) ?: 0.0,
                        targetLng = data.cords?.get(1) ?: 0.0,
                    )
                    isInsideGeofence = inside
                    Log.d("GeoCheck", "Immediate check (lastLocation): $inside")
                } else {
                    Log.d("GeoCheck", "lastLocation is null, requesting fresh location...")

                    // Fallback: request a one-time fresh location
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { freshLocation ->
                        freshLocation?.let {
                            val inside = checkGeofence(
                                it.latitude,
                                it.longitude,
                                targetLat = data.cords?.get(0) ?: 0.0,
                                targetLng = data.cords?.get(1) ?: 0.0,
                            )
                            isInsideGeofence = inside
                            Log.d("GeoCheck", "Immediate check (fresh location): $inside")
                        } ?: run {
                            Log.d("GeoCheck", "Fresh location also null")
                        }
                    }
                }
            }

            // Then keep receiving updates
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            Log.d("GeoCheck", "Location permissions are missing")
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

// Log every 5 seconds (you wrote minutes, but delay was 5 seconds)
    LaunchedEffect(isInsideGeofence) {
        while (true) {
            if (isInsideGeofence) {
                Log.d("GeoCheck", "Inside the radius")
            } else {
                Log.d("GeoCheck", "Outside the radius")
            }
            delay(5 * 1000L) // every 5 seconds
        }
    }


    LaunchedEffect(parsedStartTime, parsedEndTime) {
        while (true) {
            val currentTime = LocalTime.now()
            isJoinableTime = parsedStartTime != null && parsedEndTime != null &&
                    currentTime >= parsedStartTime.minusMinutes(15) && currentTime <= parsedEndTime
            delay(1000L)
        }

    }

    // React to join workout response
    LaunchedEffect(joinWorkoutState) {
        val result = joinWorkoutState?.getContentIfNotHandled()
        when (result) {
            is Results.Success -> {
                val response = result.data
                val isSuccess = response?.success ?: false
                val workoutData = response?.data
                isJoined = true
                (context as HomesActivity).showSuccessAlert("Workout Joined. Get ready to train hard!")
                Log.e("WorkoutResult", "Join successful, isJoined: $result  $isSuccess")
            }
            is Results.Failure -> {
                Log.e("WorkoutResult", "Join failed")
            }
            else -> Unit
        }
    }

    // React to cancel workout response
    LaunchedEffect(cancelWorkoutState) {
        val result = cancelWorkoutState?.getContentIfNotHandled()
        when (result) {
            is Results.Success -> {
                isJoined = false
                Log.e("WorkoutResult", "Cancel successful, isJoined: $result")
            }
            is Results.Failure -> {
                Log.e("WorkoutResult", "Cancel failed")
            }
            else -> Unit
        }
    }
    Box(modifier = Modifier.padding(top = 15.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(BlackCardBackground)
                .border(
                    width = 1.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(28.dp)
                )
                .height(IntrinsicSize.Min)
                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val (day, month) = parseDayAndMonth(data.createdAt.toString())

                DateComposable(day, month)

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = data.classType.toString(),
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                                fontSize = 14.sp,
                                color = WhiteColor,
                                textAlign = TextAlign.Start
                            ), modifier = Modifier
                                .weight(1f),
                            maxLines = 2
                        )
                        Log.e("WorkoutLog", "PREFSE ID : ${Prefs.init().joinedSession}")

                        Button(
                            text =  when {
                                !isInsideGeofence -> "Join"
                                Prefs.init().joinedSession?.id == data.id -> "Cancel"
                                else -> "Join"
                            },
//                            text = when {
//                                data.joined == true -> "Cancel"
//                                Prefs.init().joinedSession?.id == null -> "Join"
//                                else -> "Join"
//                            },
                            onClick = {
                                Log.e("WorkoutLog", "Clicked on item: ${data.id} | currentlyJoinedSessionId: $currentlyJoinedSessionId")

                                if (isInsideGeofence){
                                    if (Prefs.init().joinedSession?.id==data.id ) {
                                        workoutViewModel.cancelWorkoutWithApi(CancelWorkoutRequest(data.id))
                                    } else {
                                        if (Prefs.init().joinedSession?.id == null) {
                                            workoutViewModel.joinWorkoutWithApi(
                                                JoinWorkoutRequest(data.id),
                                                workoutData = data
                                            )
                                            onJoinToggle(true)
                                        } else {
                                            (context as HomesActivity).showErrorAlert("You are already in a session. Cannot join another one!")
                                            return@Button
                                        }
                                    }
                                }else{
                                    (context as HomesActivity).showErrorAlert("You are not close to the gym.")
                                    return@Button
                                }


                            },
                            enabled = currentlyJoinedSessionId == null || data.joined == true || isJoinableTime,
                            buttonColor = if (data.joined == true && isInsideGeofence) Color.Red
                            else if (isJoinableTime && isInsideGeofence) Color.Red else Color.Gray
                        )



                    }


                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = "",
                            modifier = Modifier.size(18.dp),
                            tint = HintColor
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        data.location?.uppercase()?.let {
                            Text(
                                text = it,
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                                    fontSize = 10.sp,
                                    color = WhiteColor
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_clock),
                            contentDescription = "",
                            modifier = Modifier.size(18.dp).padding(2.dp),
                            tint = HintColor
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${MyUtils.parseTimeAmPm(data.startTime.toString())} - ${MyUtils.parseTimeAmPm(data.endTime.toString())}",
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                                fontSize = 12.sp,
                                color = WhiteColor,
                                textAlign = TextAlign.Start
                            )
                        )
                        Spacer(modifier = Modifier.width(25.dp))

                        Icon (
                            painter = painterResource(id = R.drawable.ic_users),
                            contentDescription = "",
                            modifier = Modifier.size(18.dp).padding(2.dp),
                            tint = HintColor
                        )

                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = data.candidates.toString(),
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                                fontSize = 12.sp,
                                color = WhiteColor,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}





@Composable
fun DateComposable(date: Int,month:String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = BlackColor)
            .padding(horizontal = 15.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$date",
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontSize = 18.sp,
                color = WhiteColor,
                platformStyle = PlatformTextStyle(includeFontPadding = false)

            )
        )

        Text(
            modifier = Modifier,
            text = month,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontSize = 18.sp,
                color = WhiteColor,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
        )
    }
}

@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    buttonColor: Color = ButtonRed
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            disabledContainerColor = Color.Gray
        ),
        modifier = Modifier
            .width(90.dp)
            .height(37.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontSize = 12.sp,
                color = WhiteColor
            )
        )
    }
}


fun checkGeofence(
    currentLat: Double,
    currentLng: Double,
    targetLat: Double,
    targetLng: Double,
    radius: Float = 150f // default = 50 meters
): Boolean {
    val userLocation = Location("").apply {
        latitude = currentLat
        longitude = currentLng
    }

    val targetLocation = Location("").apply {
        latitude = targetLat
        longitude = targetLng
    }

    val distanceInMeters = userLocation.distanceTo(targetLocation)

    return distanceInMeters <= radius
}



@Preview
@Composable
fun DatePreview(){
    DateComposable(12, "Jan")
}
