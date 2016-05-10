package com.example.Gardener.ui;

import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.Gardener.ConnectActivity;
import com.example.Gardener.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Bulat on 2016-05-05.
 */
public class DeviceListFragment extends ListFragment {

    private DeviceListAdapter adapter;
    private ArrayList<BluetoothDevice> devicesAsArrayList = new ArrayList<>();
    private final String TAG = "DLF";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "created device list fragment");
        ConnectActivity a = (ConnectActivity) getActivity();
        //devicesAsArrayList = a.getDevicesAsArray();
        adapter = new DeviceListAdapter(devicesAsArrayList);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_list, null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        BluetoothDevice device = adapter.getItem(position);
        ((DeviceSelectionListener) getActivity()).onDeviceSelected(device);
    }

    public void notifyAdapterOfChanges(){
        adapter.notifyDataSetChanged();
    }

    public void addDeviceToList(BluetoothDevice d){
        if(!devicesAsArrayList.contains(d)) {
            devicesAsArrayList.add(d);
        }
    }

    public void addBondedDevices(){
        Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        int numToAdd = devices.size();
        Log.d(TAG, "try to add " + numToAdd + " devices");
        for(BluetoothDevice next: devices){
            if(!devicesAsArrayList.contains(next)) {
                devicesAsArrayList.add(next);
            }
        }
        notifyAdapterOfChanges();
    }

    private class DeviceListAdapter extends ArrayAdapter<BluetoothDevice>{

        public DeviceListAdapter(ArrayList<BluetoothDevice> devices) {
            super(getActivity(), 0, devices);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "device list adapter getView called:");
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.device_item, null);
            }

            BluetoothDevice device = getItem(position);
            Log.d(TAG, "for " + device.getName());
            TextView name = (TextView) convertView.findViewById(R.id.device_name);
            name.setText(device.getName());
            TextView mac = (TextView) convertView.findViewById(R.id.device_mac);
            mac.setText(device.getAddress());

            return convertView;
        }
    }
}
