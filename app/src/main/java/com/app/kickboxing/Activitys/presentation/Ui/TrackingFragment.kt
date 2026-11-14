package com.app.kickboxing.Activitys.presentation.Ui

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod
import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo
import com.onecoder.fitblekit.API.Boxing.FBKApiBoxing
import com.onecoder.fitblekit.API.Boxing.FBKApiBoxingCallBack
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus
import com.onecoder.fitblekit.Protocol.Boxing.FBKBoxingAxis
import com.app.kickboxing.Activitys.presentation.Main.DeviceClass
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity
import com.app.kickboxing.R
import com.app.kickboxing.databinding.FragmentTrackingBinding

private const val REQUEST_BLUETOOTH_PERMISSIONS: Int = 101
private const val SCAN_ACTIVITY_BACK = 102
private const val TAG = "TrackingFragment"

class TrackingFragment : Fragment() {

    private val deviceArray: MutableList<DeviceClass> = mutableListOf()
    private var m_apiBoxing: FBKApiBoxing? = null
    private val activityArray = mutableListOf("Boxing", "HeartBeat")
    private lateinit var _binding: FragmentTrackingBinding
    private lateinit var mMainListAdapter: ActivityAdapter
    private var currentConnectedDevice: DeviceClass? = null
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
    private val fbkApiBoxingCallBack = object : FBKApiBoxingCallBack {
        override fun bleConnectError(p0: String?, p1: FBKApiBsaeMethod?) {
            Log.e(TAG, "BLE Connect Error: $p0, Method: $p1")
            requireActivity().runOnUiThread {
                currentConnectedDevice?.let {
                    it.setConnectStatus(FBKBleDeviceStatus.BleDisconnected)
                    updateDeviceStatus(it)
                    Toast.makeText(requireContext(), "Connection Error: $p0", Toast.LENGTH_SHORT).show()
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
                runOnUiThread(Runnable { getDeviceStatusString(status) })
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

        override fun bleConnectInfo(p0: String?, p1: FBKApiBsaeMethod?)
        {


            Log.e(TAG, "BLE Connect Info: $p0, Method: $p1")
            if (p0 != null && p1 != null) {
                if (p0.contains("onConnectionStateChange start discovered services")) {
                    Log.e(TAG, "Connection established, starting service discovery")

                    deviceArray.forEach { device ->
                        if (device.bleDevice == p1) {
                            device.setConnectStatus(FBKBleDeviceStatus.BleConnected)
                            if (device == currentConnectedDevice) {
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
            Log.e(TAG, "RealTimeBoxing: $p0, Boxing API: $p1")
            if (p1 === m_apiBoxing) {
                val resultMap = p0 as? Map<String, String>
                resultMap?.let { map ->
                    Log.e(TAG, "Boxing Data: $map")
                    requireActivity().runOnUiThread {
                        var typeString = map["fistType"]
                        when (typeString) {
                            "1" -> typeString = "Hook"
                            "2" -> typeString = "Uppercut"
                            "3" -> typeString = "Straight punch"
                        }

                        Toast.makeText(requireContext(), "Boxing Data: $typeString", Toast.LENGTH_SHORT).show()
                    }
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
                    val sdkVersion = Build.VERSION.SDK_INT
                    if (sdkVersion >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            Toast.makeText(requireContext(), "BLUETOOTH_CONNECT permission missing.", Toast.LENGTH_SHORT).show()
                            return@registerForActivityResult
                        }
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.BLUETOOTH
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            Toast.makeText(requireContext(), "BLUETOOTH permission missing.", Toast.LENGTH_SHORT).show()
                            return@registerForActivityResult
                        }
                    }


                    val deviceClass = deviceArray.firstOrNull { it.bluetoothDevice == device }
                        ?: DeviceClass()
                            .apply {
                            setBluetoothDevice(device)
                            bleDevice = FBKApiBoxing(requireActivity(), fbkApiBoxingCallBack)
                            setAvailable(true)
                        }.also { deviceArray.add(it) }

                    deviceClass.setAvailable(false)
                    currentConnectedDevice = deviceClass
                    m_apiBoxing = deviceClass.bleDevice as? FBKApiBoxing

                    connectWithRetry(deviceClass, device)
                    updateDeviceStatus(deviceClass)
                    Toast.makeText(requireContext(), "Device Selected: ${device.name ?: "Unknown"}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tracking, container,false)

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(requireContext(), "Please enable Bluetooth to use this feature.", Toast.LENGTH_LONG).show()
        }

        mMainListAdapter = ActivityAdapter(requireContext(), activityArray)
        _binding.mainList.adapter = mMainListAdapter

        _binding.txtDeviceStatus.setOnClickListener {
            if (hasBluetoothPermissions()) {
                openDeviceScanner()
            } else {
                requestBluetoothPermissions()
            }
        }

        deviceArray.clear()
        for (i in 0..7) {
            val apiBoxing = FBKApiBoxing(requireActivity(), fbkApiBoxingCallBack)
            apiBoxing.registerBleListenerReceiver()

            val myDevice =
                DeviceClass()
            myDevice.bleDevice = apiBoxing
            myDevice.deviceId = ""
            myDevice.deviceName = ""
            myDevice.isAvailable = true
            myDevice.deviceInfo = null
            myDevice.connectStatus = FBKBleDeviceStatus.BleTurnOff
            deviceArray.add(myDevice)

            if (i == 0) {
                m_apiBoxing = apiBoxing
            }
        }


        _binding.mainList.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> findNavController().navigate(R.id.homesFragment)
                1 ->
                    findNavController().navigate(R.id.heart_beat_fragment)

            }
        }

        return _binding.root
    }

    private fun hasBluetoothPermissions(): Boolean {
        return bluetoothPermissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestBluetoothPermissions() {
        permissionRequestLauncher.launch(bluetoothPermissions)
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

    private fun getDeviceStatusString(status: FBKBleDeviceStatus) {
        if (status == FBKBleDeviceStatus.BleConnecting) {
            _binding.txtDeviceStatus.setText("Connecting")
        } else if (status == FBKBleDeviceStatus.BleConnected) {
            _binding.txtDeviceStatus.setText("Connected")
        } else if (status == FBKBleDeviceStatus.Blesynchronizing) {
            _binding.txtDeviceStatus.setText("Synchronizing")
        } else if (status == FBKBleDeviceStatus.BleSyncOver) {
            _binding.txtDeviceStatus.setText("Syn Over")
        } else if (status == FBKBleDeviceStatus.BleReconnect) {
            _binding.txtDeviceStatus.setText("Reconnecting")
        } else if (status == FBKBleDeviceStatus.BleDisconnected) {
            _binding.txtDeviceStatus.setText("Disconnected")
        } else if (status == FBKBleDeviceStatus.BleTurnOn) {
            _binding.txtDeviceStatus.setText("BleTurnOn")
        } else if (status == FBKBleDeviceStatus.BleTurnOff) {
            _binding.txtDeviceStatus.setText("BleTurnOff")
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
            val deviceAddress = device.bluetoothDevice.address
            _binding.txtDeviceStatus.text = "Device: $deviceName ($deviceAddress) - $statusText"
        }
    }


    fun bleConnectStatus(connectStatus: FBKBleDeviceStatus, apiBaseMethod: FBKApiBsaeMethod) {
        deviceArray.forEach { device ->
            if (device.bleDevice == apiBaseMethod) {
                device.setConnectStatus(connectStatus)
                when (connectStatus) {
                    FBKBleDeviceStatus.BleReconnect -> {
                        Log.e(TAG, "Attempting to reconnect device: ${device.bluetoothDevice.name}")
                        connectWithRetry(device, device.bluetoothDevice)
                    }
                    FBKBleDeviceStatus.BleConnected -> {
                        Log.e(TAG, "Device connected: ${device.bluetoothDevice.name}")
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "Device Connected: ${device.bluetoothDevice.name}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    FBKBleDeviceStatus.BleDisconnected -> {
                        Log.e(TAG, "Device disconnected: ${device.bluetoothDevice.name}")
                        connectWithRetry(device, device.bluetoothDevice)
                    }
                    else -> {
                        Log.e(TAG, "Device status: ${device.bluetoothDevice.name} - $connectStatus")
                    }
                }
                if (device == currentConnectedDevice) {
                    requireActivity().runOnUiThread {
                        updateDeviceStatus(device)
                    }
                }
            }
        }
    }

    fun bleConnectStatusLog(infoString: String, apiBaseMethod: FBKApiBsaeMethod) {
        Log.e(TAG, "Connection Log: $infoString")
    }

    fun getConnectedDeviceDetails(): String {
        return currentConnectedDevice?.let {
            val name = it.bluetoothDevice.name ?: "Unknown"
            val address = it.bluetoothDevice.address
            val status = when (it.connectStatus) {
                FBKBleDeviceStatus.BleConnected -> "Connected"
                else -> "Not Connected"
            }
            "Connected Device: $name, Address: $address, Status: $status"
        } ?: "No device connected"
    }
}