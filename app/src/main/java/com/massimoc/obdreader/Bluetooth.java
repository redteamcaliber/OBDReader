/*
    Bluetooth.java

    Author:      Massimo Cannavo

    Date:        Wed Apr 01 2015 18:47:07

    Description: The Bluetooth class interfaces with the Bluetooth API to enable
                 Bluetooth capabilities for the OBDReader app.
*/

package com.massimoc.obdreader;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

// The Bluetooth class will interface with the Bluetooth API to allow the user
// to connect to their Bluetooth OBD II dongle.
public class Bluetooth extends Activity
{
    // The Bluetooth adapter used to communicate with
    // the Bluetooth interface of the phone.
    private static BluetoothAdapter bluetoothAdapter;

    // Initializes the Bluetooth adapter of the phone.
    public Bluetooth()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // Returns the Bluetooth adapter of the Bluetooth class.
    public BluetoothAdapter getBluetoothAdapter()
    {
        return bluetoothAdapter;
    }

    // Returns true if Bluetooth is supported, otherwise,
    // returns false.
    public boolean isSupported()
    {
        return (bluetoothAdapter != null);
    }

    // Returns true if Bluetooth is enabled, otherwise,
    // returns false.
    public boolean isEnabled()
    {
        return (bluetoothAdapter.isEnabled());
    }

    // Returns the intent for the Bluetooth request, if
    // Bluetooth is not enabled and is supported, otherwise,
    // returns null.
    public Intent enableBluetooth()
    {
        if (!bluetoothAdapter.isEnabled())
            return (new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));

        return null; // Bluetooth is already enabled.
    }

    // Discovers Bluetooth devices that are enabled.
    public void discoverDevices(Context context)
    {
        Intent mDevicesDiscovered = new Intent(context, BluetoothActivity.class);
        context.startActivity(mDevicesDiscovered);
    }
}