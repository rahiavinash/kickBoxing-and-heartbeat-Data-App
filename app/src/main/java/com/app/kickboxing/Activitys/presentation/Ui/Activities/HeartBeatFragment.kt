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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod
import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo
import com.onecoder.fitblekit.API.HeartRate.FBKApiHeartRate
import com.onecoder.fitblekit.API.HeartRate.FBKApiHeartRateCallBack
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus
import com.app.kickboxing.Activitys.presentation.Main.DeviceClass
import com.app.kickboxing.Activitys.presentation.NewTracker.ModelStorage
import com.app.kickboxing.Activitys.presentation.NewTracker.RecordActivity
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity.SCAN_ACTIVITY_BACK
import com.app.kickboxing.databinding.FragmentHeartBeatBinding


class HeartBeatFragment : Fragment() {

    private val deviceArray: MutableList<DeviceClass> = mutableListOf()
    private lateinit var binding: FragmentHeartBeatBinding
    private var m_bluetoothDevice: BluetoothDevice? = null
    private var m_apiHeartRate: FBKApiHeartRate? = null
    private var currentConnectedDevice: DeviceClass? = null
    private val m_heartArray = mutableListOf<String>()
    private lateinit var m_heartAdapter: BaseAdapter
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
    private val m_apiHeartRateCallBack = object : FBKApiHeartRateCallBack {
        override fun realTimeHeartRate(data: Any, apiHeartRate: FBKApiHeartRate) {
            Log.e("data Heart rate","{$data}")
            val resultMap = data as Map<*, *>
            val heartRateStr = resultMap["heartRate"] as String
            val heartRate = heartRateStr.toInt()
            Log.e("heart","{$heartRate}")
            requireActivity().runOnUiThread {
                binding.heartTextHeart.text = "HeartRate: $heartRate"
                m_heartArray.add("HR: $heartRate @ ${System.currentTimeMillis()}")
                m_heartAdapter.notifyDataSetChanged()
            }
        }

        override fun heartRateRecord(data: Any, apiHeartRate: FBKApiHeartRate) {
            val resultMap = data as Map<String, Any>
            activity?.runOnUiThread {
                if (resultMap.isNotEmpty()) {
                    ModelStorage.getInstance().putModel(resultMap)
                    startActivity(Intent(requireActivity(), RecordActivity::class.java))
                }
            }
        }

        override fun bleConnectStatus(status: FBKBleDeviceStatus, apiBsaeMethod: FBKApiBsaeMethod) {
            activity?.runOnUiThread {
                binding.heartTextStatus.text = "Status: ${status.name}"
                if (status == FBKBleDeviceStatus.BleTurnOn) {
                    m_bluetoothDevice?.let { m_apiHeartRate?.connectBluetooth(it) }
                }
            }
        }

        override fun bleConnectError(error: String, apiBsaeMethod: FBKApiBsaeMethod) {
            showToast("Error: $error")
        }

        override fun deviceBaseInfo(baseInfo: FBKBleBaseInfo, apiBsaeMethod: FBKApiBsaeMethod) {
            showToast("Battery: ${baseInfo.battery}, Firmware: ${baseInfo.firmVersion}")
        }

        override fun setHRShockStatus(status: Boolean, apiHeartRate: FBKApiHeartRate) {
            showToast("Set Shock succeeded")
        }

        override fun getHRShockStatus(data: Any, apiHeartRate: FBKApiHeartRate) {
            val resultMap = data as Map<String, String>
            showToast("Shock Number: ${resultMap["shockNumber"]}")
        }

        override fun closeHRShockStatus(status: Boolean, apiHeartRate: FBKApiHeartRate) {
            showToast("Shock Closed")
        }

        override fun setHRMaxIntervalStatus(status: Boolean, apiHeartRate: FBKApiHeartRate) {}
        override fun setHRLightSwitchStatus(status: Boolean, apiHeartRate: FBKApiHeartRate) {}
        override fun setHRColorShockStatus(status: Boolean, apiHeartRate: FBKApiHeartRate) {}
        override fun setHRColorIntervalStatus(status: Boolean, apiHeartRate: FBKApiHeartRate) {}
        override fun clearHRRecordStatus(status: Boolean, apiHeartRate: FBKApiHeartRate) {}
        override fun HRHrvResultData(p0: Boolean, p1: Any?, p2: FBKApiHeartRate?) {}
        override fun bleConnectStatusLog(infoString: String, apiBsaeMethod: FBKApiBsaeMethod) {}
        override fun batteryPower(power: Int, apiBsaeMethod: FBKApiBsaeMethod) {}
        override fun protocolVersion(version: String, apiBsaeMethod: FBKApiBsaeMethod) {}
        override fun firmwareVersion(version: String, apiBsaeMethod: FBKApiBsaeMethod) {}
        override fun hardwareVersion(version: String, apiBsaeMethod: FBKApiBsaeMethod) {}
        override fun softwareVersion(version: String, apiBsaeMethod: FBKApiBsaeMethod) {}
        override fun bleConnectInfo(p0: String?, p1: FBKApiBsaeMethod?) {
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
        override fun deviceSystemData(systemData: ByteArray, apiBsaeMethod: FBKApiBsaeMethod) {}
        override fun privateVersion(versionMap: Map<String, String>, apiBsaeMethod: FBKApiBsaeMethod) {}
        override fun privateMacAddress(macMap: Map<String, String>, apiBsaeMethod: FBKApiBsaeMethod) {}
        override fun deviceModelString(modelString: String, apiBsaeMethod: FBKApiBsaeMethod) {}
        override fun deviceSerialNumber(serialNumber: String, apiBsaeMethod: FBKApiBsaeMethod) {}
        override fun deviceManufacturerName(manufacturerName: String, apiBsaeMethod: FBKApiBsaeMethod) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHeartBeatBinding.inflate(inflater, container, false)

        initAdapter()


        binding.heartTextStatus.setOnClickListener {
            if (hasBluetoothPermissions()) {
                openDeviceScanner()
            } else {
                requestBluetoothPermissions()
            }
        }

        m_bluetoothDevice = requireActivity().intent.getParcelableExtra("bluetooth_device")

        m_apiHeartRate = FBKApiHeartRate(requireContext(), m_apiHeartRateCallBack)
        m_apiHeartRate?.registerBleListenerReceiver()

        m_bluetoothDevice?.let { m_apiHeartRate?.connectBluetooth(it) }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        m_apiHeartRate?.disconnectBle()
        m_apiHeartRate?.unregisterBleListenerReceiver()
    }

    private fun initAdapter() {
        m_heartAdapter = object : BaseAdapter() {
            override fun getCount(): Int = m_heartArray.size
            override fun getItem(position: Int): Any = m_heartArray[position]
            override fun getItemId(position: Int): Long = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(requireContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
                (view.findViewById(android.R.id.text1) as TextView).text = m_heartArray[position]
                return view
            }
        }
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

    private fun hasBluetoothPermissions(): Boolean {
        return bluetoothPermissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestBluetoothPermissions() {
        permissionRequestLauncher.launch(bluetoothPermissions)
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
                            bleDevice = FBKApiHeartRate(requireActivity(), m_apiHeartRateCallBack)
                            setAvailable(true)
                        }.also { deviceArray.add(it) }

                    deviceClass.setAvailable(false)
                    currentConnectedDevice = deviceClass
                    m_apiHeartRate = deviceClass.bleDevice as? FBKApiHeartRate

                    connectWithRetry(deviceClass, device)
                    updateDeviceStatus(deviceClass)
                    Toast.makeText(requireContext(), "Device Selected: ${device.name ?: "Unknown"}", Toast.LENGTH_SHORT).show()
                }
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
    private fun connectWithRetry(deviceClass: DeviceClass, bluetoothDevice: BluetoothDevice, maxRetries: Int = 3) {
        var retryCount = 0
        val connectRunnable = object : Runnable {
            override fun run() {
                if (retryCount < maxRetries) {
                    (deviceClass.bleDevice as? FBKApiHeartRate)?.connectBluetooth(bluetoothDevice)
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
            val deviceName = device.bluetoothDevice.name ?: "Unknown"
            binding.heartTextStatus.text = "Device: $deviceName"
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
