package com.app.kickboxing.Activitys.domain.response

import android.bluetooth.BluetoothDevice
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceType

data class BluetoothDeviceModel(
    val deviceId: String? = null,
    val deviceMac: String? = null,
    val deviceName: String? = null,
    val connectionStatus: FBKBleDeviceStatus? = null,
    val deviceType: FBKBleDeviceType? = null,
    val deviceInformation: BluetoothDevice? = null
)

data class HeartRateValue(
    val heartRate : Int? = null
)

data class LoggedInFromAnotherDevice(
    val hasTokenExpired : Boolean? = null
)