package com.example.Gardener.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import com.example.Gardener.ConnectActivity;

import java.io.IOException;
import java.util.UUID;

public class ArduinoListenerThread extends Thread {

    private final BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private ConnectActivity parentActivity;

    public ArduinoListenerThread(String appName, ConnectActivity activity, UUID uuid) {
        parentActivity = activity;
        BluetoothServerSocket temp = null;
        try {
            temp = BluetoothAdapter
                    .getDefaultAdapter()
                    .listenUsingRfcommWithServiceRecord(appName, uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.serverSocket = temp;
    }

    public void run() {
        while (true) {
            try {
                socket = serverSocket.accept();
                if (socket != null) {
                    parentActivity.setSocket(socket);
                }
            } catch (IOException e) {
                break;
            } finally {
                    closeServerSocket();
                }
            }
    }

    public void closeServerSocket(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            // stub
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            //stub
        }
    }
}