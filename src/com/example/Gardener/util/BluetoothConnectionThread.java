package com.example.Gardener.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * A thread used to maintain an open connection with a Bluetooth Device
 */
public class BluetoothConnectionThread extends Thread {

    private final static String TAG = "ACT";

    BluetoothSocket socket;
    BluetoothDevice device;
    private final UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothConnectionThread(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        this.device = device;
        try {
            tmp = device.createRfcommSocketToServiceRecord(APP_UUID);
        } catch (IOException e) {
            Log.e(TAG, "error creating socket to service record for");
            Log.e(TAG, device.toString());
        }
        this.socket = tmp;
    }

    public void run() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        try {
            socket.connect();
        } catch (IOException connectException) {
            try {
                socket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "error closing socket");
            }
        }
    }
}
