package com.example.Gardener;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.Gardener.ui.DeviceListFragment;
import com.example.Gardener.ui.DeviceSelectionListener;
import com.example.Gardener.util.ArduinoConnectionService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * This activity is used to discover and connect to the companion
 * Arduino device via Bluetooth.
 */
public class ConnectActivity extends Activity implements DeviceSelectionListener {

    private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static int ENBT_REQ;
    private static int DISC_BT_REQ;
    private static int DISC_DUR;
    private static String ARD_NAME;
    private static String APP_NAME;
    private final String TAG = "CA";
    DeviceListFragment listFragment;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "broadcast received");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    listFragment.addDeviceToList(device);
                    Log.d(TAG, "added device" + device.getName());
                }
                listFragment.notifyAdapterOfChanges();
            }
        }

    };
    private Set<BluetoothDevice> devices;
    private BluetoothDevice selectedDevice;
    private BluetoothAdapter adapter;
    private BluetoothSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect);
        initializeValues();
        setUpListFragment();
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);
        listFragment.notifyAdapterOfChanges();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(receiver);
    }

    private void initializeValues() {
        ENBT_REQ = getResources().getInteger(R.integer.code_enable_bluetooth_request);
        DISC_BT_REQ = getResources().getInteger(R.integer.code_make_discoverable_request);
        DISC_DUR = getResources().getInteger(R.integer.duration_discoverable_in_sec);
        ARD_NAME = getResources().getString(R.string.arduino_name);
        APP_NAME = getResources().getString(R.string.app_name);

        devices = new HashSet<>();
        selectedDevice = null;
    }

    private void setUpListFragment() {
        listFragment = new DeviceListFragment();
        listFragment.setRetainInstance(true);
        getFragmentManager().beginTransaction()
                .add(R.id.devices, listFragment)
                .commit();
    }

    private Boolean bluetoothIsSupported() {
        if (adapter == null) {
            makeToast(getResources().getString(R.string.toast_bt_not_supported));
            return false;
        }
        return true;
    }

    private Boolean bluetoothIsEnabled() {
        if (!adapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, ENBT_REQ);
            return false;
        }
        return true;
    }

    public void onScanDevicesPressed(View view) {
        if (bluetoothIsSupported() && bluetoothIsEnabled()) {
            discoverDevices();
            listFragment.addBondedDevices();
            //
            //makeLocalDeviceDiscoverable();
        }
    }

    private void discoverDevices() {
        if (adapter.startDiscovery()) {
            makeToast(getResources().getString(R.string.toast_bt_discovery_initiated));
            Log.d(TAG, "began discovery");
        } else {
            makeToast(getResources().getString(R.string.toast_bt_discovery_failed_to_initiate));
            Log.e(TAG, "discovery failed");
        }
    }

    public void connectToSelected() {
        if (selectedDevice != null) {
            if (deviceIsSupported(selectedDevice)) {
                adapter.cancelDiscovery();
                Log.d(TAG, "cancelled discovery");
                Intent i = new Intent(this, ArduinoConnectionService.class);
                i.putExtra(getString(R.string.key_connection_intent), getString(R.string.msg_connect));
                i.putExtra(getString(R.string.key_mac_address), selectedDevice.getAddress());
                startService(i);
            }
        } else {
            makeToast("no device selected");
        }
    }

    private Boolean deviceIsSupported(BluetoothDevice device) {
        return true;
        //return device.getName().equals(ARD_NAME);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENBT_REQ) {
            handleEnableBluetoothResult(resultCode);
        } else if (requestCode == DISC_BT_REQ) {
            handleMakeDiscoverableResult(resultCode);
        }
    }

    private void handleEnableBluetoothResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            makeToast(getResources().getString(R.string.toast_bt_enabled));
        } else if (resultCode == Activity.RESULT_CANCELED) {
            makeToast(getResources().getString(R.string.toast_bt_disabled));
        }
    }

    private void handleMakeDiscoverableResult(int resultCode) {
        if (resultCode == Activity.RESULT_CANCELED) {
            makeToast(getResources().getString(R.string.toast_bt_discovery_disabled));
        } else if (resultCode == DISC_DUR) {
            makeToast(getResources().getString(R.string.toast_bt_discovery_enabled));
        }
    }

    private void makeLocalDeviceDiscoverable() {
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISC_DUR);
        startActivityForResult(i, DISC_BT_REQ);
    }

    private void makeToast(String message) {
        Toast toast = Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public ArrayList<BluetoothDevice> getDevicesAsArray() {
        Log.d(TAG, "getting devices in connect activity as array");
        ArrayList<BluetoothDevice> result = new ArrayList<>();
        for (BluetoothDevice next : devices) {
            result.add(next);
            Log.d(TAG, next.getName());
        }
        return result;
    }

    public BluetoothDevice getSelectedDevice() {
        return selectedDevice;
    }

    @Override
    public void onDeviceSelected(BluetoothDevice device) {
        if (device != null) {
            Log.d(TAG, "clicked" + device.getName());
            selectedDevice = device;
            connectToSelected();
        }
    }


}
