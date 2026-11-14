package com.app.kickboxing.Activitys.presentation.Ui.home


import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.kickboxing.Activitys.domain.response.BluetoothDeviceModel
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity.SCAN_ACTIVITY_BACK
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TopbarComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.HeartRateViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BottomPadding
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagDark
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagLight
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.KbxckickboxingclubTheme
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.RedColor
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.R
import org.greenrobot.eventbus.EventBus


@Composable
fun WithoutTrackingScreen(navController: NavController , viewModel:HeartRateViewModel?) {
    val deviceName = remember { mutableStateOf("") }
    deviceName.value = viewModel?.deviceName?.collectAsState()?.value ?: ""

    val boxingDeviceName = remember { mutableStateOf("") }
    boxingDeviceName.value = viewModel?.boxingDeviceName?.collectAsState()?.value ?: ""

    val secondBoxingDeviceName = remember { mutableStateOf("") }
    secondBoxingDeviceName.value = viewModel?.secondBoxingDeviceName?.collectAsState()?.value ?: ""
    val context = LocalContext.current
    val connectedDevices = viewModel?.connectedDeviceList ?: emptyList()
    val recentDevices = Prefs.init().getBluetoothDeviceList(LocalContext.current)
    var lastClickTime by remember { mutableStateOf(0L) }

    LaunchedEffect(connectedDevices) {
        Log.e("dfskfhdsfds", connectedDevices.toString())
    }
    LaunchedEffect(Unit) {
        Log.e("dfskfhdsfddcfs", recentDevices.toString())
    }


    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        ColorBagDark,
                        ColorBagDark,
                        ColorBagLight
                    )
                )
            )

    ) {

        TopbarComposable("Device Integration", onClickBack = { navController.navigateUp() }, true)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, bottom = BottomPadding, top = 10.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {

                //      BluetoothIconWithAnimatedCircles()

               // Spacer(modifier = Modifier.size(15.dp))
                // ProgressBar(progress = 0.7f)

                Row(
                    modifier = androidx.compose.ui.Modifier
                ) {
                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Devices",
                        color = colorResource(id = com.hb.dialog.R.color.dialog_white),
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                            fontSize = 20.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
                val context = LocalContext.current

//            DeviceItem(R.drawable.ic_watch, "Apple Watch / Android Watch", R.drawable.ic_check_one)


                DeviceItem(
                    R.drawable.ic_watch,
                    "Android Watch",
                    R.drawable.ic_check_one, { })
                Spacer(modifier = Modifier.height(25.dp))


                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Sensors",
                    color = colorResource(id = com.hb.dialog.R.color.dialog_white),
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 20.sp,

                        )

                )

//                if () {

                DeviceItem(
                    R.drawable.icpulse_graph,
                    text = if (deviceName.value == "") "ANT+ Heart Rate Monitors" else deviceName.value,
                    if (deviceName.value == "") R.drawable.ic_grey_check else R.drawable.ic_check_one,
                    onClick = {
                        if (deviceName.value.isNullOrEmpty()) {

                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastClickTime > 1000) {
                                lastClickTime = currentTime

                                val activity = context as? HomesActivity
                                if (activity?.hasBluetoothPermissions() == true) {
                                    Log.d("PermissionCheck", "Has bluetooth permissions.")
                                    val intent =
                                        Intent(context, DevicesScanActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    context.startActivity(intent)
                                } else {
                                    Log.d(
                                        "PermissionCheck",
                                        "Requesting bluetooth permissions."
                                    )
                                    activity?.requestBluetoothPermissions()
                                }
                            }
                        }
                    },
                    isConnected = deviceName.value != ""

                )

                Log.e("devices", "${secondBoxingDeviceName.value.toString()} ")
                Log.e("devices", "${boxingDeviceName.value.toString()} ")

                DeviceItem(
                    iconDevice = R.drawable.icpulse_graph,
                    text = if (boxingDeviceName.value.isEmpty()) {
                        "Boxing Sensors"
                    } else {
                        boxingDeviceName.value
                    },
                    ic_check = 0,
                    isConnected = boxingDeviceName.value.isNotEmpty(),
                    onClick = {
                        if (secondBoxingDeviceName.value.isNullOrEmpty()) {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastClickTime > 1000) {
                                lastClickTime = currentTime

                                val activity = context as? HomesActivity
                                if (activity?.hasBluetoothPermissions() == true) {
                                    Log.d("PermissionCheck", "Has bluetooth permissions.")
                                    val intent =
                                        Intent(context, DevicesScanActivity::class.java).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        }
                                    context.startActivity(intent)
                                } else {
                                    Log.d("PermissionCheck", "Requesting bluetooth permissions.")
                                    activity?.requestBluetoothPermissions()
                                }
                            }
                        }
                    })
                Spacer(modifier = Modifier.height(10.dp))
                if (!secondBoxingDeviceName.value.isNullOrEmpty()) {
                    DeviceItem(
                        iconDevice = R.drawable.icpulse_graph,
                        text = if (secondBoxingDeviceName.value.isEmpty()) {
                            "Boxing Sensors"
                        } else {
                            secondBoxingDeviceName.value
                        },
                        ic_check = if (secondBoxingDeviceName.value.isEmpty()) {
                            R.drawable.ic_grey_check
                        } else {
                            R.drawable.ic_check_one
                        },
                        isConnected = secondBoxingDeviceName.value.isNotEmpty(),
                        onClick = {
                            if (secondBoxingDeviceName.value.isNullOrEmpty()) {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastClickTime > 1000) {
                                    lastClickTime = currentTime

                                    val activity = context as? HomesActivity
                                    if (activity?.hasBluetoothPermissions() == true) {
                                        Log.d("PermissionCheck", "Has bluetooth permissions.")
                                        val intent =
                                            Intent(context, DevicesScanActivity::class.java).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            }
                                        context.startActivity(intent)
                                    } else {
                                        Log.d(
                                            "PermissionCheck",
                                            "Requesting bluetooth permissions."
                                        )
                                        activity?.requestBluetoothPermissions()
                                    }
                                }
                            }
                        })

                }


                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Recent Devices",
                    color = colorResource(id = com.hb.dialog.R.color.dialog_white),
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 20.sp,

                        )
                )

                if (recentDevices.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "No recent devices",
                            color = colorResource(id = com.hb.dialog.R.color.dialog_white),
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1


                        )
                    }
                }
            }

//            if (!recentDevices.isNullOrEmpty() && deviceName.value != null && !(connectedDevices?.contains(deviceName.value)) {
            if (!recentDevices.isNullOrEmpty() && deviceName.value != null ) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BlackCardBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp)
                            .border(
                                width = 1.dp,
                                color = BorderColor,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)) {
                            (recentDevices ?: emptyList())
                                .filter { device ->
                                    device.deviceName != null && !(connectedDevices?.contains(
                                        device.deviceName
                                    ) ?: false)
                                }
                                .forEachIndexed { index, device ->
                                    val item = device.deviceName ?: return@forEachIndexed
                                    val isDeviceConnected = connectedDevices?.contains(item) == true

                                    DeviceItem(
                                        iconDevice = R.drawable.icpulse_graph,
                                        text = item,
                                        ic_check = 0,
                                        isConnected = isDeviceConnected,
                                        onClick = {
                                            val bluetoothAdapter =
                                                BluetoothAdapter.getDefaultAdapter()
                                            val bluetoothDevice =
                                                bluetoothAdapter.getRemoteDevice(device.deviceMac)

                                            val deviceModel = BluetoothDeviceModel(
                                                null,
                                                device.deviceMac,
                                                device.deviceName,
                                                null,
                                                null,
                                                bluetoothDevice
                                            )

                                            EventBus.getDefault().post(deviceModel)

                                            val activity = context as? Activity
                                            val backIntent = Intent()
                                            backIntent.putExtra("bluetooth", bluetoothDevice)
                                            activity?.setResult(SCAN_ACTIVITY_BACK, backIntent)
                                        }
                                    )
                                }
                        }

                    }
                }
            }
        }
    }
}
//}





@Composable
fun DeviceItem(iconDevice: Int, text: String, ic_check: Int, onClick: () -> Unit , isConnected:Boolean?=null) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                color =
                BlackCardBackground

            )
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
            .clickable {
                onClick()
            },

        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically


    ) {

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            painter = painterResource(id = iconDevice),
            contentDescription = "",
            modifier = Modifier
                .size(34.dp)
                .padding(1.dp),
            tint = ButtonRed
        )

        Spacer(modifier = Modifier.width(13.dp))


        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .padding(end = 35.dp),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontSize = 19.sp,
                color =
                colorResource(id = com.hb.dialog.R.color.dialog_white)
            ),
            maxLines = 2
        )

        Icon(
            modifier = Modifier
                .size(29.dp),
            painter =
            painterResource(id = R.drawable.ic_check_one),
            tint = if (isConnected == true) {
                RedColor
            } else {
                BlackCardBackground
            },

            contentDescription = null
        )


    }
}



fun setupComposeView(composeView: ComposeView) {
    composeView.setContent {
        BluetoothIconWithAnimatedCircles()
    }
}

//@Composable
//fun BluetoothIconWrapper() {
//    BluetoothIconWithAnimatedCircles()
//}

@Composable
fun BluetoothIconWithAnimatedCircles() {
    val infiniteTransition = rememberInfiniteTransition()

    // Outer circle animation scale
    val outerCircleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(110.dp) // Increased size to accommodate thicker inner circle
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius1 = size.minDimension / 2.1f  // Inner circle - slightly larger spacing
            val radius2 = size.minDimension / 1.6f  // Middle circle
            val radius3 = radius2 * outerCircleScale // Animated outer circle

            // Inner static circle - thicker stroke
            drawCircle(
                color = ButtonRed,
                radius = radius1,
                style = Stroke(width = 2.dp.toPx()),
                alpha = 1f
            )

            // Middle static circle - lower opacity
            drawCircle(
                color = ButtonRed,
                radius = radius2,
                style = Stroke(width = 2.dp.toPx()),
                alpha = 0.4f
            )

            // Animated outer circle - even lower opacity
            drawCircle(
                color = ButtonRed,
                radius = radius3,
                style = Stroke(width = 2.dp.toPx()),
                alpha = 0.2f
            )
        }

        // Bluetooth icon with red circular background
        Image(
            modifier = Modifier
                .size(75.dp)
                .background(color = ButtonRed, shape = CircleShape)
                .padding(20.dp)
                .clip(CircleShape),
            painter = painterResource(id = R.drawable.ic_bluetooth),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
    }
}


@Preview(showBackground = true)
@Composable
fun WithoutTrackingScreenPreview() {
    KbxckickboxingclubTheme {
        /// GlowingCircle({}, {})
    }
}
