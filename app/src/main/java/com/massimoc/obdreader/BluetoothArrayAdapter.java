/*
    BluetoothArrayAdapter.java

    Author:      Massimo Cannavo

    Date:        Wed Apr 01 2015 14:30:19 UTC

    Description: The BluetoothArrayAdapter class will create a custom adapter
                 used for displaying the Bluetooth devices that were discovered.
*/

package com.massimoc.obdreader;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

// The BluetoothArrayAdapter is a custom adapter used for displaying the
// discovered Bluetooth devices.
public class BluetoothArrayAdapter extends ArrayAdapter<String>
{
    // The ViewHolder class is used by the BluetoothArrayAdapter
    // to update the list view, (lightweight inner class).
    private static class ViewHolder
    {
        private ImageView mBluetoothIcon;
        private TextView mBluetoothDevice;
    }

    // An array list of Bluetooth devices that were discovered.
    private ArrayList<String> mDiscoveredDevicesArray;

    // Initializes the array adapter by setting the view to the xml layout file.
    // The xml layout file contains the graphical representation of the activity.
    public BluetoothArrayAdapter(Context context, ArrayList<String> devicesArray)
    {
        super(context, R.layout.bluetooth_row, R.id.bluetoothDevice, devicesArray);
        mDiscoveredDevicesArray = devicesArray;
    }

    // Initializes the row cells of the list view with the discovered
    // Bluetooth devices.
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewRef;

        // Inflate the view, if empty, otherwise, recycle the view.
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bluetooth_row,
                    parent, false);

            viewRef = new ViewHolder();
            viewRef.mBluetoothIcon = (ImageView)convertView.findViewById(R.id.bluetoothIcon);
            viewRef.mBluetoothDevice = (TextView)convertView.findViewById(R.id.bluetoothDevice);

            convertView.setTag(viewRef);
        }

        else
            viewRef = (ViewHolder)convertView.getTag();

        viewRef.mBluetoothDevice.setText(mDiscoveredDevicesArray.get(position));
        ArrayList<Integer> devicesTypeArray = BluetoothActivity.devicesTypeArray;

        // Enable material design Android 5.0 (API level 21 and above) for
        // Android devices compatible with Android Lollipop or greater.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (devicesTypeArray.get(position) == BluetoothClass.Device.Major.PHONE)
                viewRef.mBluetoothIcon.setImageResource(R.drawable.ic_action_phone_green);

            else if (devicesTypeArray.get(position) == BluetoothClass.Device.Major.COMPUTER)
                viewRef.mBluetoothIcon.setImageResource(R.drawable.ic_action_computer_green);

            else
                viewRef.mBluetoothIcon.setImageResource(R.drawable.ic_action_bluetooth_green);
        }

        else
        {
            if (devicesTypeArray.get(position) == BluetoothClass.Device.Major.PHONE)
                viewRef.mBluetoothIcon.setImageResource(R.drawable.ic_action_phone_blue);

            else if (devicesTypeArray.get(position) == BluetoothClass.Device.Major.COMPUTER)
                viewRef.mBluetoothIcon.setImageResource(R.drawable.ic_action_computer_blue);

            else
                viewRef.mBluetoothIcon.setImageResource(R.drawable.ic_action_bluetooth_blue);
        }

        return convertView;
    }
}