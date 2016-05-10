package com.example.Gardener.util;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;
import com.example.Gardener.ConnectActivity;
import com.example.Gardener.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * IntentService used for connecting to and communicating with devices
 */
public class ArduinoConnectionService extends IntentService {

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private InputStream in;
    private OutputStream out;

    private final String MSG_CNCT = "connect";
    private final String MSG_WRITE = "write";
    private final String MSG_READ = "read";

    private final String PC_NAME = "LENOVOBULAT";

    private final static UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String TAG = "ACS";
    private Boolean connected = false;

    public ArduinoConnectionService(){
        super("ArduinoConnectionService");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "creating arduino connection service");
        super.onCreate();
        //APP_UUID = UUID.fromString(getResources().getString(R.string.uuid));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String msg = intent.getStringExtra(getString(R.string.key_connection_intent));
        switch (msg) {
            case MSG_CNCT:
                Log.d(TAG, "ACS received connection intent");
                setDevice(intent);
                Log.d(TAG, "trying to connect to device with mac" + intent.getStringExtra(getString(R.string.key_mac_address)));
                connectToDevice(device);
                break;
            case MSG_WRITE:
                Log.d(TAG, "ACS received write intent");
                setDevice(intent);
                byte[] data = intent.getByteArrayExtra(getString(R.string.key_data));
                writeToDevice(data);
                break;
            case MSG_READ: break;
        }
    }

    private void setDevice(Intent i){
        String mac = i.getStringExtra(getString(R.string.key_mac_address));
        device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
    }

    /**
     * Establishes a connection with given bluetooth device:
     * sends client request and opens bluetooth socket if accepted
     * @param device
     */
    private void connectToDevice(BluetoothDevice device){

        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        BluetoothSocket temp;

        try{
            closeSocket();
            temp = device.createRfcommSocketToServiceRecord(APP_UUID);
            socket = temp;
            Log.d(TAG, "created socket:" + socket.toString());
        } catch (IOException e){
            Log.e(TAG, "error creating socket to service record for:");
            Log.e(TAG, socket.toString());
        }

        try{
            Log.d(TAG, "trying to connect socket:" + socket.toString());
            socket.connect();
            connected = true;
            Log.d(TAG, "Connection successful");
            Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.toast_connected_to_device) + device.getName(),
                        Toast.LENGTH_SHORT).show();

        } catch (IOException connectingException) {
            Log.e(TAG, "error connecting to socket" + socket.toString());
            Log.e(TAG, connectingException.toString());
            connectingException.printStackTrace();
        }
    }


    /**
     * Opens IO streams for current device if not null, writes given data
     * and finally closes streams
     * @param data: the byte array to send
     */
    private void writeToDevice(byte[] data){
        if(!connected){
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.toast_bt_not_connected),
                    Toast.LENGTH_SHORT).show();
            connectToDevice(device);
        } else {
            try{
                in = socket.getInputStream();
                out = socket.getOutputStream();
                out.write(data);
            }catch(IOException e){
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.toast_bt_write_fail),
                        Toast.LENGTH_SHORT).show();
            }finally {
                closeIOStreams();
            }
        }
    }

    //TODO
    private byte[] readFromDevice(){
        return null; //stub
    }

    private void closeSocket(){
        try {
            if(socket!=null) {
                socket.close();
                connected = false;
                Log.d(TAG, "closed socket:" + socket.toString());
            }
        } catch (IOException closingException) {
            Log.e(TAG, "error closing socket" + socket.toString());
        }
    }

    private void closeIOStreams(){
        try {
            if(in!=null)
                in.close();
            if(out!=null)
                out.close();
        } catch (IOException e) {
            Log.e(TAG, "error closing socket" + socket.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSocket();
        closeIOStreams();
    }
}
