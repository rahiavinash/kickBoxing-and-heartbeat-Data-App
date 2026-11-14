package com.app.kickboxing.Activitys.presentation.Main;

import android.bluetooth.BluetoothDevice;

import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;

public class DeviceClass {

    // 设备类
    private Object bleDevice = null;

    private BluetoothDevice bluetoothDevice;

    // 设备ID
    private String deviceId = "";

    // 设备名称
    private String deviceName = "";

    // 设备连接状态
    private FBKBleDeviceStatus connectStatus = FBKBleDeviceStatus.BleDisconnected;

    // 设备信息
    private Object deviceInfo = null;

    // 设备可用
    private boolean isAvailable = true;

    public Object getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(Object bleDevice) {
        this.bleDevice = bleDevice;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public FBKBleDeviceStatus getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(FBKBleDeviceStatus connectStatus) {
        this.connectStatus = connectStatus;
    }

    public Object getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(Object deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }
}
