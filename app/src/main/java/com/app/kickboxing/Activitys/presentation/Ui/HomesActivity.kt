package com.app.kickboxing.Activitys.presentation.Ui

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.app.kickboxing.Activitys.Network.SocketIOImpl
import com.app.kickboxing.Activitys.domain.response.BluetoothDeviceModel
import com.app.kickboxing.Activitys.domain.response.HeartRateValue
import com.app.kickboxing.Activitys.domain.response.HomeData
import com.app.kickboxing.Activitys.domain.response.ProfileData
import com.app.kickboxing.Activitys.domain.response.RecordBoxingSession
import com.app.kickboxing.Activitys.presentation.Main.DeviceClass
import com.app.kickboxing.Activitys.presentation.Main.MainActivity
import com.app.kickboxing.Activitys.presentation.MainApplication
import com.app.kickboxing.Activitys.presentation.Navigation.NavHostContainer
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertState
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertStateCompose
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertType
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.ComposeAlertHost
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.HeartRateViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.setSocketListenON
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.bottomNavigationBar.BottomNavigationBar
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.KbxckickboxingclubTheme
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.AlertManager
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.Activitys.utils.appConstant.AppConstant
import com.app.kickboxing.R
import com.app.kickboxing.databinding.ActivityAuthBinding
import com.app.kickboxing.databinding.ActivityHomeBinding
import com.google.gson.Gson
import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod
import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo
import com.onecoder.fitblekit.API.Boxing.FBKApiBoxing
import com.onecoder.fitblekit.API.Boxing.FBKApiBoxingCallBack
import com.onecoder.fitblekit.API.HeartRate.FBKApiHeartRate
import com.onecoder.fitblekit.API.HeartRate.FBKApiHeartRateCallBack
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus
import com.onecoder.fitblekit.Protocol.Boxing.FBKBoxingAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


@AndroidEntryPoint
class HomesActivity : ComponentActivity() {
    private var bluetoothDevice: String? = null
    private val connectedDevices = mutableMapOf<String, FBKApiHeartRate>()
    private val heartRateMap = mutableMapOf<String, Int>()
    private var pendingDevice: BluetoothDevice? = null
    private lateinit var socket: SocketIOImpl
    private var m_apiBoxing: FBKApiBoxing? = null
    var boxingDeviceName: String = ""
    val viewModel: HeartRateViewModel by viewModels()
    val mViewModel: LoginViewModel by viewModels()
    private var currentConnectedDevice: DeviceClass? = null
    private val deviceArray: MutableList<DeviceClass> = mutableListOf()
    private val boxingDataListener = "boxingDataUpdated"
    private val _alertState = mutableStateOf(AlertStateCompose())
    private val alertState: State<AlertStateCompose> get() = _alertState

    val gson = Gson()
    var finalPunchValue = 0
    private fun connectWithRetry(
        deviceClass: DeviceClass,
        bluetoothDevice: BluetoothDevice,
        maxRetries: Int = 3
    ) {
        var retryCount = 0
        val connectRunnable = object : Runnable {
            override fun run() {
                if (retryCount < maxRetries) {
                    (deviceClass.bleDevice as? FBKApiBoxing)?.connectBluetooth(bluetoothDevice)
                    retryCount++
                    if (deviceClass.connectStatus != FBKBleDeviceStatus.BleConnected) {
                        Handler(Looper.getMainLooper()).postDelayed(this, 5000)
                    }
                } else {
                    this@HomesActivity.runOnUiThread {
                        Toast.makeText(
                            this@HomesActivity,
                            "Failed to connect to ${bluetoothDevice.name ?: "device"} after $maxRetries attempts.",
                            Toast.LENGTH_LONG
                        ).show()


                        deviceClass.setConnectStatus(FBKBleDeviceStatus.BleDisconnected)
                        updateDeviceStatus(deviceClass)
                    }
                }
            }
        }
        connectRunnable.run()
    }

    private val bluetoothPermissions: Array<String>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }


    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.all { it.value }
            if (granted) {
                pendingDevice?.let {
                    connectToHeartRateDevice(it)
                    Log.e("fhkshfjsdhf", pendingDevice.toString())
                    pendingDevice = null
                } ?: startActivity(Intent(this, DevicesScanActivity::class.java))
            } else {
Toast.makeText(this , "Bluetooth permissions are required." ,Toast.LENGTH_SHORT).show()
               // alertManager.showError("Bluetooth permissions are required.")
            
            }
        }

    fun hasBluetoothPermissions(): Boolean {
        return bluetoothPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestBluetoothPermissions() {
        permissionRequestLauncher.launch(bluetoothPermissions)
    }


    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
//
//        // Get the FrameLayout as ViewGroup
//        val rootViewGroup = findViewById<ViewGroup>(R.id.composeId)
//
//        // Pass it to AlertManager
//        alertManager = AlertManager(rootViewGroup, this)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.statusBars())
//        window.statusBarColor = android.graphics.Color.TRANSPARENT


        // Existing permission check for POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    AppConstant.REQUEST_CODE
                )
            }
        }


        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false // force white icons

        window.statusBarColor = android.graphics.Color.TRANSPARENT // or set a color

        socket = MainApplication.get().socket
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = ContextCompat.getColor(
            this, R.color.light_gray
        )

        setContent {
            KbxckickboxingclubTheme {
                val navController = rememberNavController()

                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = { BottomNavigationBar(navController = navController) }
                    ) { innerPadding ->
                        NavHostContainer(
                            navController = navController,
                            padding = innerPadding,
                            viewModel,
                            mViewModel
                        )
                    }

                    ComposeAlertHost(
                        alertState = alertState.value,
                        onDismiss = {
                            _alertState.value = _alertState.value.copy(isVisible = false)
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }

        callApi()
        observer()
        bluetoothDevice = intent?.getStringExtra("bluetooth")
    //    showErrorAlert("Test Alert from HomesActivity!")

    }


    override fun attachBaseContext(newBase: Context?) {
        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        applyOverrideConfiguration(newOverride)
        super.attachBaseContext(newBase)
    }

    override fun onStart() {
        super.onStart()
        if (!socket.isConnected()) {
            socket.connect()
        }
        org.greenrobot.eventbus.EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this)
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            Prefs.init().isSelectedFilter="week"
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        connectedDevices.values.forEach { api ->
            api.disconnectBle()
            api.unregisterBleListenerReceiver()
        }
        connectedDevices.clear()
        heartRateMap.clear()
        Prefs.init().isSelectedFilter="week"
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDeviceSelected(deviceModel: BluetoothDeviceModel) {
        Log.d("DEVICE_SELECTED", "Device received: ${deviceModel}")
        deviceModel.deviceInformation?.let { device ->
            if (hasBluetoothPermissions()) {
                setSocketListener()
                connectToHeartRateDevice(device)
            } else {
                pendingDevice = device
                requestBluetoothPermissions()
            }
        } ?: Toast.makeText(this, "Device connection error", Toast.LENGTH_SHORT).show()
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connectToHeartRateDevice(device: BluetoothDevice) {
        val macAddress = device.address

        if (connectedDevices.containsKey(macAddress)) {
            Toast.makeText(this, "${device.name} already connected", Toast.LENGTH_SHORT).show()
            return
        }

        val callback = createHeartRateCallback(macAddress, device.name ?: "Unknown Device")

        val api = FBKApiHeartRate(applicationContext, callback).apply {
            registerBleListenerReceiver()
            connectBluetooth(device)
        }


        connectedDevices[macAddress] = api
        heartRateMap[macAddress] = 0

        Toast.makeText(this, "Connecting to ${device.name}...", Toast.LENGTH_SHORT).show()


        var boxCallBack = createBoxingRateCallback(macAddress, device.name)


        val deviceClass = deviceArray.firstOrNull { it.bluetoothDevice == device }
            ?: DeviceClass()
                .apply {
                    setBluetoothDevice(device)
                    bleDevice = FBKApiBoxing(this@HomesActivity, boxCallBack)
                    setAvailable(true)
                }.also { deviceArray.add(it) }

        deviceClass.setAvailable(false)
        currentConnectedDevice = deviceClass
        m_apiBoxing = deviceClass.bleDevice as? FBKApiBoxing

        connectWithRetry(deviceClass, device)
        updateDeviceStatus(deviceClass)

        Log.e("fkfkdfjfjsf", deviceArray.toString())
        Toast.makeText(
            this@HomesActivity,
            "Device Selected: ${device.name ?: "Unknown"}",
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun createHeartRateCallback(
        macAddress: String,
        deviceName: String
    ): FBKApiHeartRateCallBack {
        return object : FBKApiHeartRateCallBack {
            override fun realTimeHeartRate(data: Any, apiHeartRate: FBKApiHeartRate) {
                try {
                    val resultMap = data as Map<*, *>
                    val heartRate = (resultMap["heartRate"] as String).toInt()

                    heartRateMap[macAddress] = heartRate

                    val currentMax = heartRateMap.values.maxOrNull() ?: 0
                    viewModel.updateHeartRate(currentMax)

                    Log.d("HEART_RATE", "$deviceName: $heartRate BPM (Max: $currentMax)")
                } catch (e: Exception) {
                    Log.e("HEART_RATE_ERROR", "$deviceName: ${e.message}")
                }
            }

            override fun bleConnectStatus(
                status: FBKBleDeviceStatus,
                apiBsaeMethod: FBKApiBsaeMethod
            ) {
                Log.d("BLE_STATUS", "$deviceName: ${status.name}")


                viewModel.connectedDeviceStatus( "${status.name.removePrefix("Ble")}")
                if (status.name=="BleConnected"){
                    viewModel.updateConnectedDevice(deviceName , true)
                    if(deviceName.contains("HRM")){
                        viewModel.connectedDeviceName( "${deviceName}")
                    }else{
                        if (viewModel.boxingDeviceName.value.isNullOrEmpty()){
                            Log.e("kdlfsf", boxingDeviceName.toString())
                            viewModel.connectedBoxingDeviceName( "${deviceName}")
                        }else{

                            viewModel.connectedSecondBoxingDeviceName( "${deviceName}")
                        }
                    }
                }else{
                    viewModel.updateConnectedDevice(deviceName , false)
                    if(deviceName.contains("HRM")){
                        viewModel.connectedDeviceName( "")
                        viewModel.stopTimer { isRunning ->
                            // Do something with the Boolean
                            false
                            Log.d("Timer", "Is running: $isRunning")
                        }
                        viewModel.updateHeartRate(0)
                    }else{
                        if (viewModel.boxingDeviceName.value==deviceName){
                            viewModel.connectedBoxingDeviceName( "")
                        }else{
                            viewModel.connectedSecondBoxingDeviceName( "")
                        }

                    }
                }






                when (status) {
                    FBKBleDeviceStatus.BleDisconnected -> handleDisconnection(
                        macAddress,
                        deviceName
                    )


                    FBKBleDeviceStatus.BleConnected -> {
                        runOnUiThread {
                            Toast.makeText(
                                this@HomesActivity,
                                "Connected: $deviceName",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    else -> {}
                }
            }

            override fun bleConnectError(error: String, apiBsaeMethod: FBKApiBsaeMethod) {
                Log.e("BLE_ERROR", "$deviceName: $error")
                handleDisconnection(macAddress, deviceName)
                runOnUiThread {
                    Toast.makeText(
                        this@HomesActivity,
                        "$deviceName error: $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun deviceBaseInfo(baseInfo: FBKBleBaseInfo, apiBsaeMethod: FBKApiBsaeMethod) {
                Log.d(
                    "DEVICE_INFO",
                    "$deviceName: Battery ${baseInfo.battery}%, Firmware ${baseInfo.firmVersion}"
                )
            }


            override fun bleConnectStatusLog(p0: String?, p1: FBKApiBsaeMethod?) {}
            override fun batteryPower(p0: Int, p1: FBKApiBsaeMethod?) {}
            override fun protocolVersion(p0: String?, p1: FBKApiBsaeMethod?) {}
            override fun firmwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {}
            override fun hardwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {}
            override fun softwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {}
            override fun privateVersion(p0: MutableMap<String, String>?, p1: FBKApiBsaeMethod?) {}
            override fun privateMacAddress(
                p0: MutableMap<String, String>?,
                p1: FBKApiBsaeMethod?
            ) {
            }

            override fun bleConnectInfo(p0: String?, p1: FBKApiBsaeMethod?) {}
            override fun deviceSystemData(p0: ByteArray?, p1: FBKApiBsaeMethod?) {}
            override fun deviceModelString(p0: String?, p1: FBKApiBsaeMethod?) {}
            override fun deviceSerialNumber(p0: String?, p1: FBKApiBsaeMethod?) {}
            override fun deviceManufacturerName(p0: String?, p1: FBKApiBsaeMethod?) {}
            override fun heartRateRecord(p0: Any?, p1: FBKApiHeartRate?) {}
            override fun HRHrvResultData(p0: Boolean, p1: Any?, p2: FBKApiHeartRate?) {}
            override fun setHRShockStatus(p0: Boolean, p1: FBKApiHeartRate?) {}
            override fun getHRShockStatus(p0: Any?, p1: FBKApiHeartRate?) {}
            override fun closeHRShockStatus(p0: Boolean, p1: FBKApiHeartRate?) {}
            override fun setHRMaxIntervalStatus(p0: Boolean, p1: FBKApiHeartRate?) {}
            override fun setHRLightSwitchStatus(p0: Boolean, p1: FBKApiHeartRate?) {}
            override fun setHRColorShockStatus(p0: Boolean, p1: FBKApiHeartRate?) {}
            override fun setHRColorIntervalStatus(p0: Boolean, p1: FBKApiHeartRate?) {}
            override fun clearHRRecordStatus(p0: Boolean, p1: FBKApiHeartRate?) {}
        }
    }

    private fun createBoxingRateCallback(
        macAddress: String,
        deviceName: String
    ): FBKApiBoxingCallBack {
        return object : FBKApiBoxingCallBack {
            override fun bleConnectError(p0: String?, p1: FBKApiBsaeMethod?) {
                Log.e(ContentValues.TAG, "BLE Connect Error: $p0, Method: $p1")
                this@HomesActivity.runOnUiThread {
                    currentConnectedDevice?.let {
                        it.setConnectStatus(FBKBleDeviceStatus.BleDisconnected)

                        updateDeviceStatus(it)
                        Toast.makeText(
                            this@HomesActivity,
                            "Connection Error: $p0",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }

            override fun bleConnectStatus(
                connectStatus: FBKBleDeviceStatus,
                apiBsaeMethod: FBKApiBsaeMethod
            ) {
                val status = connectStatus
                for (i in deviceArray.indices) {
                    val myDevice = deviceArray[i]
                    val apiBoxing = myDevice.bleDevice as FBKApiBoxing
                    if (apiBoxing === apiBsaeMethod) {
                        myDevice.connectStatus = status
                        if (status == FBKBleDeviceStatus.BleReconnect) {
                            apiBoxing.connectBluetooth(myDevice.bluetoothDevice)
                        }
                        break
                    }
                }

                if (apiBsaeMethod === m_apiBoxing) {
                    UiThreadStatement.runOnUiThread(Runnable { getDeviceStatusString(status) })
                }
                addDeviceName(deviceName)
            }

            override fun bleConnectStatusLog(p0: String?, p1: FBKApiBsaeMethod?) {
                Log.e(ContentValues.TAG, "BLE Connect Status Log 01: $p0, Method: $p1")
            }

            override fun batteryPower(p0: Int, p1: FBKApiBsaeMethod?) {}

            override fun protocolVersion(p0: String?, p1: FBKApiBsaeMethod?) {}

            override fun firmwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {}

            override fun hardwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {}

            override fun softwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {}

            override fun privateVersion(p0: MutableMap<String, String>?, p1: FBKApiBsaeMethod?) {}

            override fun privateMacAddress(
                p0: MutableMap<String, String>?,
                p1: FBKApiBsaeMethod?
            ) {
            }

            override fun bleConnectInfo(p0: String?, p1: FBKApiBsaeMethod?) {

            }

            override fun deviceSystemData(p0: ByteArray?, p1: FBKApiBsaeMethod?) {}

            override fun deviceModelString(p0: String?, p1: FBKApiBsaeMethod?) {}

            override fun deviceSerialNumber(p0: String?, p1: FBKApiBsaeMethod?) {}

            override fun deviceManufacturerName(p0: String?, p1: FBKApiBsaeMethod?) {}

            override fun deviceBaseInfo(p0: FBKBleBaseInfo?, p1: FBKApiBsaeMethod?) {}

            override fun realTimeBoxing(p0: Any?, p1: FBKApiBoxing?) {
                Log.e("RealTimeBoxing", "RealTimeBoxing: $p0, Boxing API: $p1")
                val resultMap = p0 as? Map<String, String>
                if (resultMap == null) {
                    Log.e("RealTimeBoxing", "Invalid or null p0 data: $p0")
                    return
                }

                Log.d("BoxingMap", "Parsed boxing map: $resultMap")

                this@HomesActivity.runOnUiThread {
                    var typeString = when (resultMap["fistType"]) {
                        "1" -> "Hook"
                        "2" -> "Uppercut"
                        "3" -> "Straight punch"
                        else -> "Unknown"
                    }

                    val fistNumber = resultMap["fistNumber"]?.toIntOrNull() ?: 0
                    val punchingTime = resultMap["fistOutTime"]?.toLongOrNull() ?: 0L
                    val withdrawingTime = resultMap["fistInTime"]?.toLongOrNull() ?: 0L
                    val gForce = resultMap["fistPower"]?.toFloatOrNull() ?: 0f
                    val punchingSpeed = resultMap["fistSpeed"]?.toFloatOrNull() ?: 0f
                    val punchingDistance = resultMap["fistDistance"]?.toFloatOrNull() ?: 0f
                    if (viewModel.isRunning.value){
                        viewModel.updatePunchSpeed(gForce)
                        finalPunchValue++
                        Log.e("BoxingParsedData", "finalPunchVlaue#: ${viewModel.isRunning.value}, Type: $typeString, Speed: $punchingSpeed")
                        viewModel.updatePunchIncrementValue(finalPunchValue)
                        viewModel.updateBoxingDate(
                            totalPunches = fistNumber,
                            punchingType = typeString,
                            punchingTime = punchingTime,
                            withdrawingTime = withdrawingTime,
                            gForce = gForce,
                            punchingSpeed = punchingSpeed,
                            punchingDistance = punchingDistance
                        )

                    }




            }
                }

            override fun boxingRecord(p0: Any?, p1: FBKApiBoxing?) {}

            override fun boxingAxisSwitchResult(p0: Boolean, p1: FBKApiBoxing?) {}

            override fun realtimeAxis(
                p0: MutableList<FBKBoxingAxis>?,
                p1: Int,
                p2: Int,
                p3: FBKApiBoxing?
            ) {
            }

        }
    }

    fun addDeviceName(newDeviceName: String) {
        val currentDeviceList = Prefs.init().getRecentDeviceList().toMutableList()
        if (!currentDeviceList.contains(newDeviceName)) {
            currentDeviceList.add(newDeviceName)
            Prefs.init().saveDeviceList(currentDeviceList)

            Log.e("currentDeviceList","$currentDeviceList")
        }
    }


    private fun handleDisconnection(macAddress: String, deviceName: String) {
        connectedDevices.remove(macAddress)?.apply {
            disconnectBle()
            unregisterBleListenerReceiver()
        }
        heartRateMap.remove(macAddress)

        val newMax = heartRateMap.values.maxOrNull() ?: 0
        viewModel.updateHeartRate(newMax)

        runOnUiThread {
            Toast.makeText(this@HomesActivity, "Disconnected: $deviceName", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @Subscribe
    fun getHeartRate(event: HeartRateValue?) {
        event?.let {
            Log.d("heartRateValue", "HeartRateValue: $it")
        }
    }
    fun autoDismiss() {
        Handler(Looper.getMainLooper()).postDelayed({
            _alertState.value = _alertState.value.copy(isVisible = false)
        }, 3000)
    }

    fun showSuccessAlert(message: String) {
        _alertState.value = AlertStateCompose(message, type = AlertType.SUCCESS, isVisible = true)
        autoDismiss()
    }

    fun showErrorAlert(message: String) {
        _alertState.value = AlertStateCompose(message,  type = AlertType.ERROR, isVisible = true)
        autoDismiss()
    }

    private fun updateDeviceStatus(device: DeviceClass) {
        this.runOnUiThread @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT) {
            val statusText = when (device.connectStatus) {
                FBKBleDeviceStatus.BleConnecting -> "Connecting"
                FBKBleDeviceStatus.BleConnected -> "Connected"
                FBKBleDeviceStatus.Blesynchronizing -> "Synchronizing"
                FBKBleDeviceStatus.BleSyncOver -> "Sync Over"
                FBKBleDeviceStatus.BleReconnect -> "Reconnecting"
                FBKBleDeviceStatus.BleDisconnected -> "Disconnected"
                FBKBleDeviceStatus.BleTurnOn -> "Bluetooth Turned On"
                FBKBleDeviceStatus.BleTurnOff -> "Bluetooth Turned Off"
                FBKBleDeviceStatus.BleTurningOff -> "Turning Off Bluetooth"
                FBKBleDeviceStatus.BleTurningOn -> "Turning On Bluetooth"
                FBKBleDeviceStatus.BleSearching -> "Searching for Devices"
                FBKBleDeviceStatus.BleSyncFailed -> "Sync Failed"
                FBKBleDeviceStatus.BleDisconnecting -> "Disconnecting"
                FBKBleDeviceStatus.BleBroadcast -> "Broadcasting"
            }
            val deviceName = device.bluetoothDevice.name ?: "Unknown"
            val deviceAddress = device.bluetoothDevice.address
            boxingDeviceName = "Device: $deviceName ($deviceAddress) - $statusText"
        }
    }

    private fun getDeviceStatusString(status: FBKBleDeviceStatus) {
        if (status == FBKBleDeviceStatus.BleConnecting) {
            boxingDeviceName = "Connecting"
        } else if (status == FBKBleDeviceStatus.BleConnected) {
            boxingDeviceName = "Connected"
        } else if (status == FBKBleDeviceStatus.Blesynchronizing) {
            boxingDeviceName = "Synchronizing"
        } else if (status == FBKBleDeviceStatus.BleSyncOver) {
            boxingDeviceName = "Syn Over"
        } else if (status == FBKBleDeviceStatus.BleReconnect) {
            boxingDeviceName = "Reconnecting"
        } else if (status == FBKBleDeviceStatus.BleDisconnected) {
            boxingDeviceName = "Disconnected"
        } else if (status == FBKBleDeviceStatus.BleTurnOn) {
            boxingDeviceName = "BleTurnOn"
        } else if (status == FBKBleDeviceStatus.BleTurnOff) {
            boxingDeviceName = "BleTurnOff"
        }
    }

    private fun setSocketListener() {
        if (socket.isConnected()) {
            socket.listenOff(boxingDataListener)
            socket.listenOn(boxingDataListener, { args ->
                CoroutineScope(Dispatchers.Main).launch {
                    if (args.isNotEmpty() && args[0] is JSONObject) {
                        val jsonObject = args[0] as JSONObject
                        val sessionData = gson.fromJson(jsonObject.toString(), RecordBoxingSession::class.java)
                        viewModel.updateSessionID(sessionData.session)
                        Log.d("BoxingSession", "Session: ${sessionData.session}, Punches: ${sessionData.totalPunches}")
                    }
                }
            })
        } else {
            socket.connect()
        }
    }

    private fun callApi() {
        mViewModel.userGetProfile()
    }




    private fun observer() {
        lifecycleScope.launchWhenStarted {
            mViewModel.userGetProfileResponseData.collect { event ->
                event?.getContentIfNotHandled()?.let { result ->
                    when (result) {
                        is Results.Loading -> {
                        }

                        is Results.Success -> {
                            if (result.data.success) {
                                Prefs.init().myProfileData = result.data.data?:ProfileData()
                            }
                        }

                        is Results.Failure -> {
                        }
                    }
                }
            }


        }

    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
}
