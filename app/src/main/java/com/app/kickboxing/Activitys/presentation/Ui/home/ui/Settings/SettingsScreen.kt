package com.app.kickboxing.Activitys.presentation.Ui.home.ui.Settings

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.app.kickboxing.Activitys.domain.model.SessionRequest
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity
import com.app.kickboxing.Activitys.presentation.Ui.Activities.AuthActivity
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertHost
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertState
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertType
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.ButtonComposable
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.RoundedImage
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.DeviceItem
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.convertCmToFeetInches
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.convertToMMddyyyy
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TopbarComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.HeartRateViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BottomPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagDark
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagLight
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EndPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.StartPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun SettingsScreen(navController: NavController,viewModel: HeartRateViewModel?) {

    val showLoadingDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isFirstTime = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val mViewModel = hiltViewModel<LoginViewModel>()
    val DOB = convertToMMddyyyy(
        Prefs.init().myProfileData.DOB ?: ""
    )
    val height = convertCmToFeetInches(
        Prefs.init().myProfileData.height ?: 0.0
    )
    var imageUri by remember { mutableStateOf("") }
    val logoutState by mViewModel.logoutResponseData.collectAsStateWithLifecycle()
    val logoutResult = logoutState?.getContentIfNotHandled()

    val deviceName = remember { mutableStateOf("") }
    deviceName.value = viewModel?.deviceName?.collectAsState()?.value ?: ""

    val boxingDeviceName = remember { mutableStateOf("") }
    boxingDeviceName.value = viewModel?.boxingDeviceName?.collectAsState()?.value ?: ""
    var lastClickTime by remember { mutableStateOf(0L) }
    val alertState = remember { mutableStateOf<AlertState?>(null) }
    val activity = context as? HomesActivity
    val getProfileState by mViewModel.userGetProfileResponseData.collectAsStateWithLifecycle()
    val currentState by rememberUpdatedState(getProfileState)


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    isFirstTime.value = true
                    Log.d("abc", "This is On Create Log")
                }

                else -> {}
            }
        }

        val lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }

    }


    //Here we handle the response of the userProfile
    when (val state = getProfileState?.getContentIfNotHandled()) {
        is Results.Success -> {
            Prefs.init().myProfileData = state.data.data
        }
        is Results.Failure -> {
            // Handle failure
            Log.e("Profile", "Failed to fetch profile data")
        }

        is Results.Loading -> {
        }

        null -> {}
    }


    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)){
            activity?.showErrorAlert("Please check your internet connection.")
        }
        mViewModel.userGetProfile()
    }



    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

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
                    .padding(
                        bottom = BottomPadding
                    )
            ) {
                TopbarComposable(text = "Settings", onClickBack = { }, false)

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = if (isFirstTime.value) {
                        Modifier
                            .verticalScroll(ScrollState(0))
                    } else {
                        Modifier.verticalScroll(rememberScrollState())
                    }
                        .fillMaxWidth()
                        .padding(start = StartPadding, end = EndPadding, top = 0.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    if (isFirstTime.value) {
                        isFirstTime.value = false
                    }

                    Spacer(modifier = Modifier.size(35.dp))

                    RoundedImage(
                        url = if (!Prefs.init().myProfileData.profile.isNullOrEmpty()) {
                            MyUtils.IMAGE_URL + Prefs.init().myProfileData.profile
                        } else {
                            ""
                        },
                        size = 122
                    )

                    Spacer(modifier = Modifier.height(29.dp))

                    Text(
                        text = Prefs.init().myProfileData.userName ?: "",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                            fontSize = 20.sp,
                            color = WhiteColor
                        )

                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    Text(
                        text = Prefs.init().myProfileData.fullName ?: "",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                            fontSize = 25.sp,
                            color = WhiteColor
                        )
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    UserDetails(
                        Prefs.init().myProfileData.gender ?: "",
                        DOB ?: "",
                        height.first,
                        heightInches = height.second,
                        Prefs.init().myProfileData.weight?.toInt() ?: 0
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                    ) {

                        Spacer(modifier = Modifier.height(15.dp))

                        Text(
                            text = "Devices",
                            color = colorResource(id = com.hb.dialog.R.color.dialog_white),
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                                fontSize = 20.sp
                            )
                        )


                        DeviceItem(
                            R.drawable.ic_watch,
                            "Android Watch",
                            R.drawable.ic_check_one, {}
                        )
                        Spacer(modifier = Modifier.height(25.dp))


                        Text(
                            text = "Sensors",
                            color = colorResource(id = com.hb.dialog.R.color.dialog_white),
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                                fontSize = 20.sp
                            )
                        )

                        DeviceItem(
                            R.drawable.icpulse_graph,
                            text = if (deviceName.value == "") "ANT+ Heart Rate Monitors" else deviceName.value,
                            ic_check = if (deviceName.value.isEmpty()) {
                                R.drawable.ic_grey_check
                            } else {
                                R.drawable.ic_check_one
                            },
                            isConnected = deviceName.value.isNotEmpty(),

                            onClick =       {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastClickTime > 1000) {
                                    lastClickTime = currentTime

                                    val activity = context as? HomesActivity
                                    if (activity?.hasBluetoothPermissions() == true) {
                                        Log.d("PermissionCheck", "Has bluetooth permissions.")
                                        val intent = Intent(context, DevicesScanActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        context.startActivity(intent)
                                    } else {
                                        Log.d("PermissionCheck", "Requesting bluetooth permissions.")
                                        activity?.requestBluetoothPermissions()
                                    }
                                }
                            }
                        )

                        DeviceItem(
                            R.drawable.icpulse_graph,
                            text = if (boxingDeviceName.value==""){
                                "Boxing Sensors"
                            }else{
                                boxingDeviceName.value
                            },
                            ic_check = if (boxingDeviceName.value.isEmpty()) {
                                R.drawable.ic_grey_check
                            } else {
                                R.drawable.ic_check_one
                            },
                            isConnected = boxingDeviceName.value.isNotEmpty(),

                            onClick =     {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastClickTime > 1000) {  // 1 sec debounce
                                    lastClickTime = currentTime

                                    val activity = context as? HomesActivity
                                    if (activity?.hasBluetoothPermissions() == true) {
                                        Log.d("PermissionCheck", "Has bluetooth permissions.")
                                        val intent = Intent(context, DevicesScanActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        context.startActivity(intent)
                                    } else {
                                        Log.d("PermissionCheck", "Requesting bluetooth permissions.")
                                        activity?.requestBluetoothPermissions()
                                    }
                                }
                            }
                        )


                        Spacer(modifier = Modifier.height(25.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Goals",
                                color = colorResource(id = com.hb.dialog.R.color.dialog_white),
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                                    fontSize = 20.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        UserGoals(
                            Prefs.init().myProfileData.weightGoals ?: 0,
                            Prefs.init().myProfileData.punchAverageGoal ?: 0,
                            Prefs.init().myProfileData.pointAverageGoal ?: 0,
                            Prefs.init().myProfileData.classAttendenceGoal ?: 0
                        )


                        Spacer(modifier = Modifier.height(60.dp))

                        ButtonComposable(
                            text = "Edit Profile",
                            onClick = {
                                navController.navigate("editProfile")

                            }
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        ButtonComposable(
                            text = "Logout",
                            onClick = {
                                showLoadingDialog.value = true
                            }
                        )
                        Spacer(modifier = Modifier.height(150.dp))
                        if (showLoadingDialog.value) {
                            CustomPopup(openDialogCustom = showLoadingDialog) {
                                (context as HomesActivity).showSuccessAlert("Your account is Logout.")

                                mViewModel.userLogOut()
                            }
                        }
                    }
                }
            }

            when (logoutResult) {
                is Results.Failure -> {
                    Log.e("logout", "Profile logout  ")

                }

                Results.Loading -> {}
                is Results.Success -> {
                    val intent = Intent(context, AuthActivity::class.java)
                    context.startActivity(intent)
                    Prefs.init().clear()
                    if (context is Activity) {
                        context.finish()
                    }
                }

                null -> {}
            }

        }

    }




fun convertCmToFeetInches(cm: Double): Pair<Int, Int> {
    val totalInches = cm / 2.54
    val roundedInches = Math.round(totalInches).toInt()
    val feet = roundedInches / 12
    val inches = roundedInches % 12
    return Pair(feet, inches)
}

