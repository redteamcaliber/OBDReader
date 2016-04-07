/*
    BluetoothActivity.java

    Author:      Massimo Cannavo

    Date:        Wed Apr 01 2015 14:30:19 UTC

    Description: The Bluetooth activity of the OBDReader app will interface with
                 the Bluetooth class to display the available Bluetooth devices
                 that can be connected to.
*/

package com.massimoc.obdreader;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

// The Bluetooth activity will display a list of devices that are enabled for
// communication.
public class BluetoothActivity extends ActionBarActivity
{
    // The Bluetooth adapter used to communicate with
    // the Bluetooth interface of the phone.
    private BluetoothAdapter mBluetoothAdapter;

    // The broadcast receiver that listens for discovered
    // Bluetooth devices.
    private BroadcastReceiver mReceiver;

    // The array adapter used to populate the
    // discovered Bluetooth devices.
    private BluetoothArrayAdapter mArrayAdapter;

    // An array list of discovered Bluetooth devices.
    private ArrayList<String> mDiscoveredDevicesArray;

    // An array list of Bluetooth device objects.
    private ArrayList<BluetoothDevice> mDevicesArray;

    // An array list of the type of discovered Bluetooth devices.
    public static ArrayList<Integer> devicesTypeArray;

    // The connection thread for the Bluetooth connection.
    private ConnectThread mConnection;

    // Initializes the Bluetooth activity by overriding the
    // onCreate() method and setting the view to the xml layout
    // file. The xml layout file contains the graphical representation
    // of the activity.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Enable material design Android 5.0 (API level 21 and above) for
        // Android devices compatible with Android Lollipop or greater.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenDark));

            toolbar.setBackgroundColor(this.getResources().getColor(R.color.green));

            TextView header = (TextView)findViewById(R.id.header);
            header.setBackgroundColor(this.getResources().getColor(R.color.green));
        }

        Bluetooth bluetooth = new Bluetooth();
        mBluetoothAdapter = bluetooth.getBluetoothAdapter();

        mDiscoveredDevicesArray = new ArrayList<>();
        mDevicesArray = new ArrayList<>();
        devicesTypeArray = new ArrayList<>();

        mArrayAdapter = new BluetoothArrayAdapter(this, mDiscoveredDevicesArray);
        ListView bluetoothList = (ListView)findViewById(R.id.bluetoothList);
        bluetoothList.setDivider(null);
        bluetoothList.setAdapter(mArrayAdapter);

        // Navigates back to the parent activity on the click of
        // the up navigation icon.
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavUtils.navigateUpFromSameTask(BluetoothActivity.this);
            }
        });

        // Listens for the Bluetooth device that was selected from the list view.
        // Initiates the thread connection with the selected Bluetooth device.
        bluetoothList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mBluetoothAdapter.cancelDiscovery();

                UI ui = new UI();
                ThreadHandler threadHandler = ui.getThreadHandler();

                mConnection = new ConnectThread(mDevicesArray.get(position),
                        BluetoothActivity.this, threadHandler);

                mConnection.executeConnection();

                if (threadHandler.hasMessages(threadHandler.getSuccessConnect()))
                    BluetoothActivity.this.finish();
            }
        });

        broadcastReceiver();
    }

    // Registers the broadcast receiver to listen for discovered
    // Bluetooth devices that are enabled.
    private void broadcastReceiver()
    {
        mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();

                switch (action)
                {
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE);

                        if (device.getName() != null)
                            mDiscoveredDevicesArray.add(device.getName());

                        else
                            mDiscoveredDevicesArray.add(device.getAddress());

                        mDevicesArray.add(device);
                        devicesTypeArray.add(device.getBluetoothClass().getMajorDeviceClass());
                        mArrayAdapter.notifyDataSetChanged();
                        break;

                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        Toast.makeText(context, "Scanning for Bluetooth devices.",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver, filter);

        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        mBluetoothAdapter.startDiscovery();
    }

    // Populates the action items into the action bar by inflating
    // the xml menu file. The xml menu file contains the layout of
    // the action items in the action bar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    // Unregisters the Bluetooth broadcast receiver and cancel
    // the discovery of Bluetooth devices.
    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        unregisterReceiver(mReceiver);
    }
}