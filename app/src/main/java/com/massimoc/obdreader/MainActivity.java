/*
    MainActivity.java

    Author:      Massimo Cannavo

    Date:        Wed Apr 01 2015 14:30:19

    Description: The main activity of the OBDReader app is the starting point
                 of the app. It is the interface that the user will use to
                 perform certain actions.
*/

package com.massimoc.obdreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

// The main activity will create a UI for the user to navigate through
// the various different aspects of the app.
public class MainActivity extends ActionBarActivity
{
    // The UI of the app used to interface with the different
    // aspects of the app.
    private UI mUI;

    // A text view that displays the fault codes of the car.
    public static TextView carStatus;

    // A text view that displays the status of the OBD connection.
    public static TextView connectionStatus;

    // Initializes the main activity by overriding the onCreate()
    // method and setting the view to the xml layout file. The xml
    // layout file contains the graphical representation of the activity.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.appBar);
        toolbar.setTitle("Car Summary");
        setSupportActionBar(toolbar);

        mUI = new UI(this, this);
        carStatus = (TextView)findViewById(R.id.carStatus);
        connectionStatus = (TextView)findViewById(R.id.connectionStatus);

        final ArrayList<String> itemsArray = new ArrayList<>();
        itemsArray.add("Connect to a Bluetooth Dongle");
        itemsArray.add("Check for fault codes");
        itemsArray.add("Look up code(s)");
        itemsArray.add("Settings");

        MenuArrayAdapter arrayAdapter = new MenuArrayAdapter(this, itemsArray);
        ListView menuList = (ListView)findViewById(R.id.menu);
        menuList.setDivider(null);
        menuList.setAdapter(arrayAdapter);

        // Listens for the menu item that was selected from the list view.
        // Performs an action based on the menu item selected.
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (itemsArray.get(position))
                {
                    case "Connect to a Bluetooth Dongle":
                        mUI.connect();
                        break;

                    case "Check for fault codes":
                        mUI.diagnostics();
                        break;

                    case "Look up code(s)":
                        mUI.codes();
                        break;

                    case "Settings":
                        mUI.settings();
                        break;
                }
            }
        });
    }

    // Receives the result from the enable Bluetooth intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        final int REQUEST_ENABLE_BT = 1;

        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK)
        {
            Toast.makeText(this, "Bluetooth enabled.", Toast.LENGTH_SHORT).show();
            mUI.discover();
        }
    }
}