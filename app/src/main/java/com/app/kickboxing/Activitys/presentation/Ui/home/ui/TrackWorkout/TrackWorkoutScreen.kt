package com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.app.kickboxing.Activitys.Network.SocketIO
import com.app.kickboxing.Activitys.domain.model.CancelWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.JoinWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.LeaderboardDetailRequest
import com.app.kickboxing.Activitys.domain.model.ScheduledWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.SessionRequest
import com.app.kickboxing.Activitys.domain.response.WorkoutData
import com.app.kickboxing.Activitys.presentation.MainApplication
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.AveragePerformanceComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.StatsComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.ScheduledWorkouts.ComposableComponents.checkGeofence
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TopbarComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.Animation.GlowingCircle
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.ComputeKOPoints.KCESInput
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.Model.PunchData
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.UseCase.getHeartRateProgress
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.UseComponent.LivePerformance
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BottomPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.Brown
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagDark
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagLight
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EndPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.StartPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.TextOffWhite
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.TopPadding
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.Activitys.utils.Utils
import com.app.kickboxing.R
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ContextCastToActivity")
@Composable
fun TrackWorkout(navController: NavController, deviceViewModel: HeartRateViewModel?) {
    val activity = LocalContext.current as? HomesActivity
    val isFirstTime = remember { mutableStateOf(false) }

    val socket = MainApplication.get().socket
    val mViewModel = hiltViewModel<LoginViewModel>()

    val updateProfileState by mViewModel.sessionResponseData.collectAsStateWithLifecycle()
    var isSessionActive by remember { mutableStateOf(false) }

    //  var punchesValue by rememberSaveable { mutableStateOf(0) }
    val isRunning = deviceViewModel?.isRunningStart?.collectAsState()?.value ?: false

    val punches = remember { mutableStateOf(0) }
    punches.value = deviceViewModel?.punchIncrementCounter?.collectAsState()?.value ?: 0

    var punchingSpeed by remember { mutableStateOf(0f) }
    punchingSpeed = deviceViewModel?.punchingSpeed?.collectAsState()?.value ?: 0f

    var punchingSpeedForUI by remember { mutableStateOf(0f) }
    punchingSpeedForUI = deviceViewModel?.punchingSpeedForUI?.collectAsState()?.value ?: 0f

    val punchingType = remember { mutableStateOf("") }
    punchingType.value = deviceViewModel?.punchingType?.collectAsState()?.value ?: ""

    val punchingTime = remember { mutableStateOf(0L) }
    punchingTime.value = deviceViewModel?.punchingTime?.collectAsState()?.value ?: 0L

    val withdrawingTime = remember { mutableStateOf(0L) }
    withdrawingTime.value = deviceViewModel?.withdrawingTime?.collectAsState()?.value ?: 0L

    val gForce = remember { mutableStateOf(0f) }
    gForce.value = deviceViewModel?.gForce?.collectAsState()?.value ?: 0f

    val punchingDistance = remember { mutableStateOf(0f) }
    punchingDistance.value = deviceViewModel?.punchingDistance?.collectAsState()?.value ?: 0f

    val heartRate = remember { mutableStateOf(0) }
    heartRate.value = deviceViewModel?.heartRate?.collectAsState()?.value ?: 0

    val sessionID = remember { mutableStateOf("") }
    sessionID.value = deviceViewModel?.sessionId?.collectAsState()?.value ?: ""

    val coroutineScope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }
    var lastEmittedPunchCount by remember { mutableStateOf(-1) }

    val punchHistory = remember { mutableStateListOf<PunchData>() }

    val formattedTime = remember { mutableStateOf("") }

    val punchingPerMinutes = remember { mutableStateOf(0) }
    punchingPerMinutes.value = deviceViewModel?.punchPerMinutes?.collectAsState()?.value ?: 0

    val heartBeatProgress = remember { mutableStateOf(0f) }
    heartBeatProgress.value = getHeartRateProgress(heartRate.value.toDouble().toInt())

    val deviceStatus = remember { mutableStateOf("") }
    deviceStatus.value = deviceViewModel?.deviceStatus?.collectAsState()?.value ?: "Not Connected "


    val deviceName = remember { mutableStateOf("") }
    deviceName.value = deviceViewModel?.deviceName?.collectAsState()?.value ?: ""

    val boxingDeviceName = remember { mutableStateOf("") }
    boxingDeviceName.value = deviceViewModel?.boxingDeviceName?.collectAsState()?.value ?: ""


    val punchPowerProgress = remember { mutableStateOf(0f) }
    punchPowerProgress.value = getPunchPower(punchingSpeedForUI.toDouble())

    val koPoints = remember { mutableStateOf(0) }
    val recentPerformanceKoPoints = remember { mutableStateOf(0) }

    var isRunningValue by rememberSaveable { mutableStateOf(false) }
    var timeInSeconds by rememberSaveable { mutableStateOf(0) }

    val time = deviceViewModel?.timeInSeconds?.collectAsState()
    val context = LocalContext.current

    var lastUpdatedMinute by remember { mutableStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var isInsideGeofence by remember { mutableStateOf(false) }
    val insideGeofenceIndexes = remember { mutableStateListOf<WorkoutData>() }
    val offset = remember { mutableStateOf(0) }
    val getWorkoutState by mViewModel.scheduleWorkoutResponseData.collectAsStateWithLifecycle()
    val workoutResult = getWorkoutState?.getContentIfNotHandled()
    var workoutList by remember { mutableStateOf<List<WorkoutData>>(emptyList()) }
    var scheduleList = remember { mutableStateOf("") }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val workoutData = remember { Prefs.init().joinedSession }
    val geofenceList = remember { mutableStateOf(listOf<Double>()) }
    var hasJoinedSession by remember { mutableStateOf(false) }
    val joinWorkoutState by mViewModel.joinWorkoutResponseData.collectAsStateWithLifecycle()
    var isJoined by remember { mutableStateOf(workoutData?.joined) }


    LaunchedEffect(punches.value) {
        /** Here we are handle the Ko punch Per Minutes*/
        if (time?.value != 0) {
            val timeSecond = (time?.value?.toFloat() ?: 0f) / 60f
            formattedTime.value = String.format("%.1f", timeSecond)
            if (formattedTime.value < "1") {
                formattedTime.value = "1"
            }
            deviceViewModel?.updatePunchesPerMinutes(
                (punches.value.toFloat() / (formattedTime.value?.toFloat() ?: 0f)).toInt()
            )

            /** Here we are handle the Ko point Calculation*/
            koPoints.value = deviceViewModel?.let { vm ->
                val result = computeKoPoints(vm, punches.value)
                result.toInt()
            } ?: 0

            recentPerformanceKoPoints.value = deviceViewModel?.let { vm ->
                val result = computeKoPoints(vm, punches.value)
                result.toInt()
            } ?: 0
        }

    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                for (location in locationResult.locations) {
                    // Clear previous values if you want fresh check each time
                    insideGeofenceIndexes.clear()

                    workoutList.forEachIndexed { index, workout ->
                        val inside = checkGeofence(
                            location.latitude,
                            location.longitude,
                            targetLat = workout.cords?.getOrNull(0) ?: 0.0,
                            targetLng = workout.cords?.getOrNull(1) ?: 0.0
                        )



                        if (inside) {
                            insideGeofenceIndexes.add(workout)
                            Log.d(
                                "GeoCheck001",
                                "Inside geofence for session: ${workout.id}, classType=${workout.classType}, time=${workout.startTime} → ${workout.endTime}"
                            )
                            Log.d(
                                "GeoCheck001",
                                "Inside geofence for Final List: ${insideGeofenceIndexes}"
                            )
                        }

                    }

                    Log.d(
                        "GeoCheck01",
                        "Inside geofence indexes: $insideGeofenceIndexes (lat=${location.latitude}, lng=${location.longitude})"
                    )
                }
            }
        }
    }


    DisposableEffect(Unit) {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            0L // update every 5 seconds
        ).build()

        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("GeoCheck01", "Requesting lastLocation...")

            // Immediate check for last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val inside = checkGeofence(
                        location.latitude,
                        location.longitude,
                        targetLat = workoutData?.cords?.get(0) ?: 0.0,
                        targetLng = workoutData?.cords?.get(1) ?: 0.0,
                    )
                    isInsideGeofence = inside
                    Log.d("GeoCheck01", "Immediate check (lastLocation): $inside")
                } else {
                    Log.d("GeoCheck01", "lastLocation is null, requesting fresh location...")

                    // Fallback: request a one-time fresh location
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { freshLocation ->
                        freshLocation?.let {
                            val inside = checkGeofence(
                                it.latitude,
                                it.longitude,
                                targetLat = workoutData?.cords?.get(0) ?: 0.0,
                                targetLng = workoutData?.cords?.get(1) ?: 0.0,
                            )
                            isInsideGeofence = inside
                            Log.d("GeoCheck01", "Immediate check (fresh location): $inside")
                        } ?: run {
                            Log.d("GeoCheck01", "Fresh location also null")
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
            Log.d("GeoCheck01", "Location permissions are missing")
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }


//    LaunchedEffect(isInsideGeofence, workoutList) {
//        val joinedWorkout = Prefs.init().joinedSession ?: return@LaunchedEffect
//        val canRun = canRunWorkout(joinedWorkout, isInsideGeofence)
//
//        if (canRun && !hasJoinedSession) {
//            Log.d("AutoJoin", "Conditions met → joining workout ${joinedWorkout.id}")
//
//            mViewModel.joinWorkoutWithApi(
//                JoinWorkoutRequest(joinedWorkout.id),
//                workoutData = joinedWorkout
//            )
//            hasJoinedSession = true
//        }
//
//        if (!canRun && hasJoinedSession) {
//            Log.d("AutoJoin", "Conditions lost → stopping auto-join session")
//            hasJoinedSession = false
//        }
//    }


    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)) {
            if (!socket.isConnected()) {
                socket.connect()
            } else {
                setSocketListenON(socket, punchHistory)
            }
        } else {
            (context as HomesActivity).showErrorAlert("Please check your internet connection.")
        }
    }

    LaunchedEffect(Unit) {


        mViewModel.scheduledWorkout(ScheduledWorkoutRequest(10, 0))

        snapshotFlow { time?.value ?: 0 }
            .collect { seconds ->
                if (seconds > 0 && seconds % 60 == 0) {
                    Log.e("EveryMinute", "Minute passed: ${seconds / 60}")

                    val timeSecond = (time?.value?.toFloat() ?: 0f) / 60f
                    formattedTime.value = String.format("%.1f", timeSecond)
                    if (formattedTime.value < "1") {
                        formattedTime.value = "1"
                    }
                    deviceViewModel?.updatePunchesPerMinutes(
                        (punches.value.toFloat() / (formattedTime.value?.toFloat() ?: 0f)).toInt()
                    )
                }
            }
    }


    LaunchedEffect(Unit) {
        mViewModel.joinWorkoutResponseData.collect { response ->
            response?.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is Results.Success -> {
                        val data = result.data
                        val message = data.message ?: ""

                        when {
//                                message.contains("already joined", ignoreCase = true) -> {
//                                    (context as HomesActivity).showSuccessAlert("You’ve already joined this workout.")
//                                }
                            message.contains("No classes available", ignoreCase = true) -> {
                                (context as HomesActivity).showErrorAlert("No classes available, joined with default session.")
                            }

                            else -> {
                                (context as HomesActivity).showSuccessAlert("Successfully joined workout!")
                            }
                        }
                    }

                    is Results.Failure -> {
                        (context as HomesActivity).showErrorAlert(
                            result.message ?: "Something went wrong"
                        )
                    }

                    Results.Loading -> {
                        Log.d("JoinWorkout", "Loading...")
                    }
                }
            }
        }
    }



    when (workoutResult) {
        is Results.Success -> {
            workoutResult.data.data?.list?.let { list ->
                workoutList = list
                scheduleList.value = Uri.encode(Gson().toJson(list))

                val joinedId = Prefs.init().joinedSession?.id
                hasJoinedSession = list.any { it.id == joinedId }
                Log.d("JoinedCheck", "Has joined session? $hasJoinedSession")

                list.forEach { workout ->
                    val canStart = workout.startTime?.let {
                        workout.endTime?.let { it1 ->
                            canStartTimer(
                                it,
                                it1
                            )
                        }
                    }
                    Log.e("TimerCheck", "Workout ${workout.id} → Can start? $canStart")
                }
                Log.e("workout", "$workoutResult")
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
    val joinWorkoutResponseData = mViewModel.joinWorkoutResponseData.collectAsState(initial = null)

//    LaunchedEffect(joinWorkoutResponseData.value) {
//        joinWorkoutResponseData.value?.getContentIfNotHandled()?.let { result ->
//            when (result) {
//                is Results.Success -> {
//                    (context as HomesActivity).showSuccessAlert("Successfully joined workout!")
////                    Prefs.init().joinedSession = true
//                }
//                is Results.Failure -> {
//                    (context as HomesActivity).showErrorAlert(result.message ?: "Something went wrong")
//                }
//                else -> {}
//            }
//        }
//    }


    /**  Here is the emit for Complete session*/
    fun emitBoxingSession() {
        if (!sessionID.value.isNullOrEmpty()) {
            val reservation = Utils.toJsonObject(
                "classType" to Prefs.init().joinedSession?.classType,
                "focus" to Prefs.init().joinedSession?.focus,
                "gym" to Prefs.init().joinedSession?.gym,
                "id" to Prefs.init().joinedSession?.id,
            )

            val obj = Utils.toJsonObject(
                "session" to sessionID.value,
                "totalPunches" to punches.value,
                "punchingType" to punchingType.value,
                "punchingTime" to punchingTime.value,
                "withdrawingTime" to withdrawingTime.value,
                "gForce" to gForce.value,
                "punchingSpeed" to punchingSpeed,
                "punchingDistance" to punchingDistance.value,
                "heartRate" to heartRate.value,
                "score" to koPoints.value,
                "reservation" to reservation

            )
            Log.e("hgdsgdgasdhs", obj.toString())
            socket.emitMessage("recordBoxingSession", obj) { _, _ -> }
        } else {
            val obj = Utils.toJsonObject(
                "session" to "",
                "totalPunches" to 0,
                "punchingType" to "Unknown",
                "punchingTime" to 0,
                "withdrawingTime" to 0,
                "gForce" to 0,
                "punchingSpeed" to 0,
                "punchingDistance" to 0,
                "heartRate" to 0,
                "score" to 0,
                "reservation" to null
            )
            Log.e("hgdsgdgasdhs", obj.toString())
            socket.emitMessage("recordBoxingSession", obj) { _, _ -> }
        }

    }

    fun emitBoxingStopSession() {
        val obj = Utils.toJsonObject(
            "session" to sessionID.value,
            "score" to koPoints.value,
            "totalPunches" to punches.value,
            "averagePunchingSpeed" to punchingPerMinutes.value
        )
        Log.e("hgdsgdgasdhs", obj.toString())

        socket.emitMessage("stopBoxingSession", obj) { _, _ -> }
        deviceViewModel?.updateSessionID("")
    }

    /**Here we handle the if device is disconnected than we will complete the session */
    LaunchedEffect(deviceStatus.value) {
        if (deviceStatus.value == "Reconnect") {
            deviceViewModel?.stopTimer { isRunning ->
                // Do something with the Boolean
                false
                Log.d("Timer", "Is running: $isRunning")
            }
            emitBoxingStopSession()
            activity?.finalPunchValue = 0
            punches.value = 0
            punchingSpeed = 0f
            deviceViewModel?.addHeartRateToHistory(0, 0f)
            job?.cancel()
            job = null
            deviceViewModel?.resetPunchingSpeed()
            deviceViewModel?.updatePunchesPerMinutes(0)
            deviceViewModel?.updatePunchSpeed(0f)
            deviceViewModel?.updateRunningStart(false)
            koPoints.value = 0
            punchPowerProgress.value = 0f
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        ColorBagDark,
                        ColorBagDark,
                        ColorBagLight
                    )
                )
            )
            .padding(bottom = BottomPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        TopbarComposable("Track Your Workout", {}, false)
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = if (isFirstTime.value) {
                Modifier
                    .verticalScroll(ScrollState(0))
            } else {
                Modifier.verticalScroll(rememberScrollState())
            }
                .weight(1f)
                .padding(start = 22.dp, end = 22.dp, top = TopPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isFirstTime.value) {
                isFirstTime.value = false
            }

            //    Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .clickable(
                        enabled = true,
                        onClickLabel = "",
                        onClick = {
                            navController.navigate("withoutTracking")
                        },
                        role = null,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                Text(
                    text = "Devices Status : ",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 19.sp
                    )
                )

                if (deviceStatus.value != "" && deviceStatus.value == "Connected") {
                    Spacer(Modifier.width(1.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.icon_check),
                        contentDescription = "",
                        modifier = Modifier
                            .size(25.dp)
                            .padding(4.dp),
                        tint = Brown
                    )
                }

                Spacer(Modifier.width(3.dp))
                Text(
                    text = if (deviceStatus.value == "") {
                        " Not Connected"
                    } else {
                        if (deviceStatus.value == "Connected") {
                            deviceStatus.value
                        } else {
                            " Not Connected"
                        }

                    },
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 19.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(15.dp))


            Column(
                modifier = Modifier
                    .size(250.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (deviceViewModel != null) {

                    GlowingCircle(
                        deviceViewModel,
                        onClickTimer = { isRunning ->

                            isRunningValue = isRunning
                            isTimerRunning = isRunning
                            deviceViewModel?.updatePunchIncrementValue(0)

                            if (!isInternetAvailable(context)) {
                                (context as HomesActivity).showErrorAlert("Please check your internet connection.")
                            }

                            if (isRunning) {
                                Log.e("api01", Prefs.init().joinedSession.toString())
                                Log.e("api001", insideGeofenceIndexes.toString())

                                // Check internet first
                                if (!isInternetAvailable(context)) {
                                    (context as HomesActivity).showErrorAlert("Please check your internet connection.")
                                    return@GlowingCircle
                                }

                                // Check if any class is available
                                if (!insideGeofenceIndexes.isNullOrEmpty()) {
                                    val workoutId = insideGeofenceIndexes[0].id
                                    Log.d("JoinCheck", "Joining workout with id: $workoutId")
                                    if (isInternetAvailable(context)) {
                                        mViewModel.joinWorkoutWithApi(
                                            JoinWorkoutRequest(workoutId),
                                            insideGeofenceIndexes[0]
                                        )
                                        (context as HomesActivity).showSuccessAlert("Workout Joined")
                                    }

                                } else {
                                    (context as HomesActivity).showErrorAlert("No classes are available at this time. Please join manually.")
                                }

                                //  Job for punch tracking (inside onClickTimer)
                                job = coroutineScope.launch {
                                    while (isRunning) {
                                        if (socket.isConnected()) {
                                            Log.e(
                                                "socketStatus ",
                                                "punchingSpeedForUI is not $punchingSpeedForUI"
                                            )
                                            if (punches.value != lastEmittedPunchCount) {
                                                lastEmittedPunchCount = punches.value
                                                deviceViewModel?.addHeartRateToHistory(
                                                    heartRate.value,
                                                    gForce.value
                                                )
                                                emitBoxingSession()
                                            }
                                        } else {
                                            Log.e("socketStatus ", "Socket is not connected")
                                        }

                                        delay(1000L)
                                    }
                                }
                            } else {

                                Log.e("recentPerformanceKoPoints ", punches.value.toString())
                                if (punches.value == 0) {
                                    Prefs.init().koPoint = "0"
                                } else {
                                    Prefs.init().koPoint =
                                        recentPerformanceKoPoints.value.toString()
                                }

                                emitBoxingStopSession()
                                activity?.finalPunchValue = 0
                                punches.value = 0
                                punchingSpeed = 0f
                                deviceViewModel?.addHeartRateToHistory(0, 0f)
                                job?.cancel()
                                job = null
                                deviceViewModel?.resetPunchingSpeed()
                                deviceViewModel?.updatePunchesPerMinutes(0)
                                deviceViewModel.updatePunchSpeed(0f)
                                deviceViewModel?.updateRunningStart(false)
                                koPoints.value = 0
                                punchPowerProgress.value = 0f
                            }
                        },
                        timeInSecond = { totalSeconds ->

                            isSessionActive = true
                            timeInSeconds = totalSeconds
                            Log.d("WorkoutTime", "1111 : ${punches.value} ")
                            deviceViewModel.updateLocalPunchValue(punches.value)
                            val time = totalSeconds.toFloat() / 60f
                            formattedTime.value = String.format("%.1f", time)
                            if (formattedTime.value < "1") {
                                formattedTime.value = "1"
                            }
                            deviceViewModel.updateRunningStart(true)
                            punchPowerProgress.value =
                                getPunchPower(punchingSpeedForUI.toDouble())
                        },
                        isRunningExternal = isTimerRunning
                    )


                }
            }

            Spacer(modifier = Modifier.height(5.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = BorderColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        color = BlackCardBackground
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ) {

                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = "KO Points",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 20.sp
                    )

                )
                Text(
                    modifier = Modifier.padding(bottom = 20.dp),
                    text = if (isRunning) {
                        koPoints.value.toString() + " Pts"
                    } else {
                        "0 Pts"
                    },
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 25.sp
                    )
                )

            }

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Your Statistics",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 20.sp,
                    color = TextOffWhite
                )
            )

            Spacer(modifier = Modifier.height(20.dp))


            Column(
                // modifier = Modifier.padding(horizontal = 30.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatsComposable(
                            text = "Punches",
                            icon = R.drawable.ic_new_flame,
                            statCount = if (isRunning) {
                                punches.value.toString()
                            } else {
                                "0"
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        StatsComposable(
                            text = "Power",
                            icon = R.drawable.ic_power,
                            statCount = if (isRunning) {
                                punchingSpeedForUI.toString()
                            } else {
                                "0.0"
                            }

                        )
                    }
                }

                Spacer(modifier = Modifier.height(19.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatsComposable(
                            text = "Punches/Min",
                            icon = R.drawable.ic_new_flame,
                            statCount = punchingPerMinutes.value.toString() ?: ""
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        StatsComposable(
                            text = "Heart Rate",
                            icon = R.drawable.ic_power,
                            statCount = deviceViewModel?.heartRate?.collectAsState()?.value?.toString()
                                ?: "0",
                            true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Live Performance",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 20.sp,
                    color = TextOffWhite
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Column(modifier = Modifier.padding(vertical = 20.dp)) {

                AveragePerformanceComposable(
                    icon = R.drawable.ic_power_new,
                    title = "Punch Power",
                    stats = "${punchingSpeedForUI}",
                    progress = punchPowerProgress.value
                )

                Spacer(modifier = Modifier.height(20.dp))
                Log.e("hhhhhhhhh", heartRate.value.toString())
                AveragePerformanceComposable(
                    icon = R.drawable.ic_hearrate,
                    title = "Heart Rate Zone",
                    stats = "${heartRate.value} bpm",
                    progress = heartBeatProgress.value,
                    true
                )


            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Recent Stats",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 20.sp,
                    color = TextOffWhite
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (punchHistory.isNotEmpty()) {
                val latest = punchHistory.last()
                LivePerformance(
                    latest.heartRate.toFloat().toInt().toString(),
                    latest.power,
                    latest.totalPunch,
                    latest.punchPerMinutes,
                    if (latest.totalPunch == "0") {
                        "0"
                    } else {
                        recentPerformanceKoPoints.value.toString()
                    }

                )
            } else {
                // Show default if empty
                Log.e("hjskahdaj", punches.value.toString())
                LivePerformance(
                    heartbeat = Prefs.init().recentStat?.heartRate ?: "0",
                    power = Prefs.init().recentStat?.power ?: "0",
                    punches = Prefs.init().recentStat?.totalPunch ?: "0",
                    punchPerMinutes = Prefs.init().recentStat?.punchPerMinutes ?: "0",
                    koPoints = Prefs.init().koPoint ?: "0"

                )
                // LivePerformance("0", "0", "0", "0", "0")
            }
            Spacer(modifier = Modifier.height(154.dp))

        }

//           Spacer(modifier = Modifier.height(154.dp))
    }


}


@RequiresApi(Build.VERSION_CODES.O)
fun setSocketListenON(socket: SocketIO, punchHistory: SnapshotStateList<PunchData>) {
    socket.listenOff("sessionUpdated")
    socket.listenOff("error")

    socket.listenOn("sessionUpdated") { args ->
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val obj = JSONObject(args[0].toString())
                Log.e("stopBoxingSession", obj.toString())

                val totalPunches = obj.optDouble("totalPunches", 0.0)
                val punchingSpeed = obj.optDouble("maxGForce", 0.0)
                val heartRate = obj.optDouble("maxHeartRate", 0.0)
                val averagePunchingSpeed = obj.optInt("averagePunchingSpeed", 0)

                // Calculate punch/min using punches & duration
                val startTime = Instant.parse(obj.getString("startTime"))
                val endTime = Instant.parse(obj.getString("endTime"))
                val durationSeconds = Duration.between(startTime, endTime).seconds.coerceAtLeast(1)
                val punchPerMinutes = (totalPunches / (durationSeconds / 60.0)).toInt()

                punchHistory.add(
                    PunchData(
                        totalPunch = String.format("%.0f", totalPunches),
                        punchPerMinutes = averagePunchingSpeed.toString(),
                        power = String.format("%.1f", punchingSpeed),
                        heartRate = String.format("%.1f", heartRate)
                    )
                )
                Prefs.init().recentStat = punchHistory.lastOrNull()


            } catch (e: Exception) {
                Log.e("SocketParseError", "Failed to parse sessionUpdated: ${e.message}")
            }
        }
    }
}


//fun getHeartRateProgress(heartRate: Double): Float {
//    return when {
//        heartRate < 0 -> 0.0f
//        heartRate in 0.1..10.0 -> 0.1f
//        heartRate in 10.0..20.0 -> 0.2f
//        heartRate in 20.0..30.0 -> 0.3f
//        heartRate in 40.0..50.0 -> 0.5f
//        heartRate in 50.0..60.0 -> 0.6f
//        heartRate in 60.0..70.0 -> 0.7f
//        heartRate in 70.0..80.0 -> 0.8f
//        heartRate in 80.0..100.0 -> 0.85f
//        heartRate in 100.0..120.0 -> 0.9f
//        else -> 1.0f
//    }
//}


fun getPunchPower(heartRate: Double): Float {
    return when {
        heartRate < 0 -> 0f
        heartRate > 150 -> 1.0f
        else -> ((heartRate / 150.0).coerceIn(0.0, 1.0)).toFloat()
    }
}


fun computeKoPoints(viewModel: HeartRateViewModel, punches: Int): Double {
    val user = Prefs.init().myProfileData
    //  val dob = user.DOB ?: Date()
    val dob = user.DOB?.takeIf { it.isNotBlank() }?.let { parseDate(it) } ?: run {
        Log.e("KO", "Invalid DOB - using fallback")
        Calendar.getInstance().apply { add(Calendar.YEAR, -25) }.time // default age = 25
    }

    val age = calculateAge(dob)
    val weight = user.weight?.toDouble() ?: 0.0
    val gender =
        if (user.gender == "female") com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.ComputeKOPoints.Gender.FEMALE else com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.ComputeKOPoints.Gender.MALE

    val avgHeartRate = calculateAvg(viewModel.heartRateHistory.value.map { it.toDouble() })
    val avgGForce = calculateAvg(viewModel.avgGForceHistory.value.map { it.toDouble() })
    val zones = computeTimeInZones(age.toDouble(), viewModel)

    val input = KCESInput(
        age = age,
        weight = weight,
        gender = gender,
        averageHeartRate = avgHeartRate,
        totalPunches = punches,
        averageGForce = avgGForce,
        zones = zones,
        useAlternateHR = false
    )
    return calculateKCES(input)
}

fun computeTimeInZones(age: Double, viewModel: HeartRateViewModel): List<Double> {
    val maxHeartRate = 208 - 0.7 * age
    val timeInZones = MutableList(5) { 0 }

    for (rate in viewModel.heartRateHistory.value.map { it.toDouble() }) {
        val percent = (rate / maxHeartRate) * 100
        when {
            percent in 50.0..59.99 -> timeInZones[0]++
            percent in 60.0..69.99 -> timeInZones[1]++
            percent in 70.0..79.99 -> timeInZones[2]++
            percent in 80.0..89.99 -> timeInZones[3]++
            percent >= 90 -> timeInZones[4]++
        }
    }
    return timeInZones.map { String.format("%.2f", it / 60.0).toDouble() }
}

fun parseDate(dateString: String?): Date {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // adjust format if needed
        format.parse(dateString ?: "") ?: Date() // fallback to current date
    } catch (e: Exception) {
        Date() // fallback in case of parsing error
    }
}

fun calculateKCES(input: KCESInput): Double {
    val (age, weight, gender, avgHR, totalPunches, avgGForce, zones, useAltHR) = input

    if (zones.size != 5) return 0.0

    val (T1, T2, T3, T4, T5) = zones
    val maxHR = if (useAltHR) 208.0 - 0.7 * age else 220.0 - age

    val HRPoints = T1 * 1 + T2 * 2 + T3 * 3 + T4 * 4 + T5 * 5
    val strikePoints = totalPunches * 0.7
    val forcePoints = if (weight > 0) (avgGForce / weight) * 11.023 else 0.0
    val genderBonus =
        if (gender == com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.ComputeKOPoints.Gender.FEMALE) 0.05 else 0.0
    val adjustmentFactor = if (weight > 0 && avgHR > 0)
        (maxHR / avgHR) + (154.0 / weight) + genderBonus
    else 0.0
    val kces = HRPoints + strikePoints + forcePoints + adjustmentFactor
    return if (kces.isFinite()) String.format("%.2f", kces).toDouble() else 0.0
}

fun calculateAvg(data: List<Double>): Double =
    if (data.isNotEmpty()) data.sum() / data.size else 0.0


fun calculateAge(dob: Date): Int {
    val birthCalendar = Calendar.getInstance().apply { time = dob }
    val now = Calendar.getInstance()
    var age = now.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
    if (now.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age
}


@RequiresApi(Build.VERSION_CODES.O)
fun canStartTimer(startTimeIso: String, endTimeIso: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"))

        val start = Instant.from(formatter.parse(startTimeIso))
        val end = Instant.from(formatter.parse(endTimeIso))


        val now = Instant.now()

        now.isAfter(start) && now.isBefore(end)
    } catch (e: Exception) {
        false
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun canRunWorkout(workout: WorkoutData, isInsideGeofence: Boolean): Boolean {
    if (!isInsideGeofence) return false
    val startTime = workout.startTime ?: return false
    val endTime = workout.endTime ?: return false
    return canStartTimer(startTime, endTime)
}


