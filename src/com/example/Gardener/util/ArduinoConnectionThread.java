package com.example.Gardener.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Bulat on 2016-05-05.
 */
public class ArduinoConnectionThread extends Thread {

    BluetoothSocket socket;
    BluetoothDevice device;
    private final UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ArduinoConnectionThread(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        this.device = device;
        try {
            tmp = device.createRfcommSocketToServiceRecord(APP_UUID);
        } catch (IOException e) { }
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
                //stub
            }
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }
    }
}
