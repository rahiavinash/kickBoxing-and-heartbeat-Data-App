package com.app.kickboxing.Activitys.presentation.Ui.Activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod
import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo
import com.onecoder.fitblekit.API.Boxing.FBKApiBoxing
import com.onecoder.fitblekit.API.Boxing.FBKApiBoxingCallBack
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus
import com.onecoder.fitblekit.Protocol.Boxing.FBKBoxingAxis
import com.app.kickboxing.Activitys.presentation.Main.DeviceClass
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity.SCAN_ACTIVITY_BACK
import com.app.kickboxing.databinding.FragmentHomesBinding
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomesFragment : Fragment() {
    private val deviceArray: MutableList<DeviceClass> = mutableListOf()
    private var m_apiBoxing: FBKApiBoxing? = null
    private lateinit var mbinding : FragmentHomesBinding
    private var current_ConnectedDevice: DeviceClass? = null
    private var isProcessingBleStatus = false
    private val bluetoothPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val fbkApiBoxingCallBack = object : FBKApiBoxingCallBack {
        override fun bleConnectError(p0: String?, p1: FBKApiBsaeMethod?) {
            Log.e(TAG, "BLE Connect Error: $p0, Method: $p1")
            requireActivity().runOnUiThread {
                current_ConnectedDevice?.let {
                    it.setConnectStatus(FBKBleDeviceStatus.BleDisconnected)
                    updateDeviceStatus(it)
                    Toast.makeText(requireContext(), "Connection Error: $p0", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun updateDeviceStatus(device: DeviceClass) {
            requireActivity().runOnUiThread {
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
                mbinding.txtDevice.text = "Device: $deviceName $statusText "
            }
        }


        override fun bleConnectStatus(p0: FBKBleDeviceStatus?, p1: FBKApiBsaeMethod?) {
            if (isProcessingBleStatus) {
                Log.e(TAG, "bleConnectStatus is already being processed; ignoring this call")
                return
            }
            isProcessingBleStatus = true
            Log.e(TAG, "BLE Connect Status: p0=$p0, p1=$p1")
            if (p0 == null || p1 == null) {
                Log.e(TAG, "Null parameters in bleConnectStatus: p0=$p0, p1=$p1")
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "BLE status update failed: Invalid data", Toast.LENGTH_SHORT).show()
                }
                isProcessingBleStatus = false
                return
            }
            Log.e(TAG, "p1 class: ${p1.javaClass.name}")
            if (p1 !is FBKApiBoxing) {
                Log.e(TAG, "p1 is not of type FBKApiBoxing: ${p1.javaClass.name}")
                isProcessingBleStatus = false
                return
            }
            try {
                bleConnectStatus(p0, p1)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing BLE status: ${e.message}", e)
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error updating BLE status", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isProcessingBleStatus = false
            }
        }

        override fun bleConnectStatusLog(p0: String?, p1: FBKApiBsaeMethod?) {
            Log.e(TAG, "BLE Connect Status Log: $p0, Method: $p1")
        }

        override fun batteryPower(p0: Int, p1: FBKApiBsaeMethod?) {}

        override fun protocolVersion(p0: String?, p1: FBKApiBsaeMethod?) {}

        override fun firmwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {}

        override fun hardwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {}

        override fun softwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {}

        override fun privateVersion(p0: MutableMap<String, String>?, p1: FBKApiBsaeMethod?) {}

        override fun privateMacAddress(p0: MutableMap<String, String>?, p1: FBKApiBsaeMethod?) {}

        override fun bleConnectInfo(p0: String?, p1: FBKApiBsaeMethod?) {
            Log.e(TAG, "BLE Connect Info: $p0, Method: $p1")
            if (p0 != null && p1 != null) {
                if (p0.contains("onConnectionStateChange start discovered services")) {
                    Log.e(TAG, "Connection established, starting service discovery")

                    deviceArray.forEach { device ->
                        if (device.bleDevice == p1) {
                            device.setConnectStatus(FBKBleDeviceStatus.BleConnecting)
                            if (device == current_ConnectedDevice) {
                                requireActivity().runOnUiThread {
                                    updateDeviceStatus(device)
                                }
                            }
                        }
                    }
                } else if (p0.contains("gatt.discoverServices is : true")) {
                    Log.e(TAG, "Service discovery initiated")
                }
            }
        }

        override fun deviceSystemData(p0: ByteArray?, p1: FBKApiBsaeMethod?) {}

        override fun deviceModelString(p0: String?, p1: FBKApiBsaeMethod?) {}

        override fun deviceSerialNumber(p0: String?, p1: FBKApiBsaeMethod?) {}

        override fun deviceManufacturerName(p0: String?, p1: FBKApiBsaeMethod?) {}

        override fun deviceBaseInfo(p0: FBKBleBaseInfo?, p1: FBKApiBsaeMethod?) {}

        override fun realTimeBoxing(p0: Any?, p1: FBKApiBoxing?) {
            Log.e("RealTimeBoxing02", "RealTimeBoxing: $p0, Boxing API: $p1")
            Log.e("RealTimeBoxing02", "Boxing Data Entry")
            val resultMap = p0 as? Map<String, String>
            resultMap?.let { map ->
                Log.e("RealTimeBoxing02", "Boxing Data: $map")
                requireActivity().runOnUiThread {
                    var typeString = map["fistType"]
                    when (typeString) {
                        "1" -> typeString = "Hook"
                        "2" -> typeString = "Uppercut"
                        "3" -> typeString = "Straight punch"
                    }

                    val fistNumberString = map["fistNumber"]
                    val fistNumber = fistNumberString?.toIntOrNull()
                    if (fistNumber != null) {
                        mbinding.counts.text = fistNumber.toString()
                        Log.d("RealTimeBoxing", "Fist Number: $fistNumber")
                    } else {
                        Log.e("RealTimeBoxing", "Fist Number is null or not an integer")
                    }

                    Toast.makeText(requireContext(), "Boxing Data: $typeString", Toast.LENGTH_SHORT).show()
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
        ) {}
    }

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.all { it.value }
            if (granted) {
                openDeviceScanner()
            } else {
                Toast.makeText(requireContext(), "Bluetooth permissions are required.", Toast.LENGTH_SHORT).show()
            }
        }


    private val scanActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == SCAN_ACTIVITY_BACK) {
                val bluetoothDevice: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra("bluetooth", BluetoothDevice::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    result.data?.getParcelableExtra("bluetooth")
                }
                bluetoothDevice?.let { device ->
                    if (ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(requireContext(), "BLUETOOTH_CONNECT permission missing.", Toast.LENGTH_SHORT).show()
                        return@registerForActivityResult
                    }
                    val deviceClass = deviceArray.firstOrNull { it.bluetoothDevice == device }
                        ?: DeviceClass()
                            .apply {
                            setBluetoothDevice(device)
                            bleDevice = FBKApiBoxing(requireActivity(), fbkApiBoxingCallBack)
                            setAvailable(true)
                        }.also { deviceArray.add(it) }

                    deviceClass.setAvailable(false)
                    current_ConnectedDevice = deviceClass
                    m_apiBoxing = deviceClass.bleDevice as? FBKApiBoxing

                    connectWithRetry(deviceClass, device)
                    updateDeviceStatus(deviceClass)
                    Toast.makeText(requireContext(), "Device Selected: ${device.name ?: "Unknown"}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onStart() {
        super.onStart()
        org.greenrobot.eventbus.EventBus.getDefault().register(this)
        val currentDevice = (activity as? HomeActivity)?.currentConnectedDevice
        currentDevice?.let {
            val deviceName = it.bluetoothDevice.name ?: "Unknown"
            mbinding.txtDevice.text = "Device: $deviceName"
        }

        mbinding.txtDevice.setOnClickListener {
            if (hasBluetoothPermissions()) {
                openDeviceScanner()
            } else {
                requestBluetoothPermissions()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        var resultMap = (activity as HomeActivity).resultMap
        Log.e("abc","${resultMap}")
        mbinding.counts.text = resultMap?.get("fistNumber") ?: "00"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mbinding = FragmentHomesBinding.inflate(layoutInflater , container, false)

        //mbinding.LinearProgressBar.setToProgress(70)
        return mbinding.root
    }

    private fun hasBluetoothPermissions(): Boolean {
        return bluetoothPermissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestBluetoothPermissions() {
        permissionRequestLauncher.launch(bluetoothPermissions)
    }

    private fun connectWithRetry(deviceClass: DeviceClass, bluetoothDevice: BluetoothDevice, maxRetries: Int = 3) {
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
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to connect to ${bluetoothDevice.name ?: "device"} after $maxRetries attempts.", Toast.LENGTH_LONG).show()
                        deviceClass.setConnectStatus(FBKBleDeviceStatus.BleDisconnected)
                        updateDeviceStatus(deviceClass)
                    }
                }
            }
        }
        connectRunnable.run()
    }

    private fun updateDeviceStatus(device: DeviceClass) {
        requireActivity().runOnUiThread {
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
            val deviceName = device.bluetoothDevice.name ?: ""
            mbinding.txtDevice.text = "Device: $deviceName"
        }
    }
    private fun openDeviceScanner() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(requireContext(), "Bluetooth is disabled. Please enable it.", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(requireActivity(), DevicesScanActivity::class.java)
        scanActivityLauncher.launch(intent)

    }


    override fun onStop() {
        super.onStop()
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun punchCount(event: PunchCount){

        Log.e("jksa", event.toString())
        mbinding.counts.text = event.count.toString()

    }
}