package com.iot.falldetection;


import java.io.Serializable;

public class BluetoothDevice implements Serializable {

    public String deviceName;
    public String deviceAddress;
    public transient android.bluetooth.BluetoothDevice device;
    public String textToShow;

    public BluetoothDevice(String deviceName, String deviceAddress) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.textToShow = deviceAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public android.bluetooth.BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(android.bluetooth.BluetoothDevice device) {
        this.device = device;
    }

    public String getTextToShow() {
        return textToShow;
    }

    public void setTextToShowToAddress() {
        this.textToShow = this.deviceAddress;
    }

    public void setTextToShowToName() {
        this.textToShow = this.deviceName;
    }
}
