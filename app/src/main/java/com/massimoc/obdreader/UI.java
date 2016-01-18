/*
    Copyright (C) 2015 Massimo Cannavo

    You should have received a copy of the license
    along with OBDReader; see the file LICENSE.

    Programmer:  Massimo Cannavo
    Email:       Massimocannavo15@gmail.com
    Date:        Thu May 07 2015 23:05:20

    Description: The user interface (UI) of
                 the app creates the interface
                 that the user will use to
                 interact with the app.
*/

package com.massimoc.obdreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

// The UI is the bridge between the different
// components of the app. It creates the interface
// that connects the various aspects of the app.
public class UI extends Activity {

    // An object of the Bluetooth class used
    // to interface with the Bluetooth API.
    private Bluetooth mBluetooth;

    // A handler used for sending and receiving
    // messages from the read thread of the app.
    private static ThreadHandler threadHandler;

    // The connection thread for the Bluetooth connection.
    private static ConnectThread connection;

    // Denotes the result of the Bluetooth connection.
    private static boolean connectionResult;

    // The current context of the app.
    private static Context context;

    // The main activity of the app.
    private Activity mActivity;

    // Default do nothing constructor.
    UI() {}

    // Initializes the Bluetooth interface.
    UI(Context context, MainActivity activity) {
        mBluetooth = new Bluetooth();
        threadHandler = new ThreadHandler(activity);
        connection = new ConnectThread();
        connectionResult = false;
        UI.context = context;
        mActivity = activity;
    }

    // Returns the thread handler.
    public ThreadHandler getThreadHandler() {
        return threadHandler;
    }

    // Sets the result of the connection.
    public void setConnectionResult(boolean result) {
        connectionResult  = result;
        MainActivity.connectionStatus.setText("OK");
    }

    // Attempts to connect to the Wifi or Bluetooth OBD II dongle.
    public void connect() {
        if (!mBluetooth.isSupported()) {
            Toast.makeText(context, "Make sure that your phone supports Bluetooth.",
                    Toast.LENGTH_LONG).show();
        }
        else if (!mBluetooth.isEnabled()) {
            final int REQUEST_ENABLE_BT = 1;
            Intent enableBluetooth = mBluetooth.enableBluetooth();
            mActivity.startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        }
        else {
            discover();
        }
    }

    // Attempts to discover the Bluetooth devices and
    // connect to a device selected by the user.
    public void discover() {
        mBluetooth.discoverDevices(context);
    }

    public void diagnostics() {

    }

    public void codes() {

    }

    public void settings() {

    }

    // Queries the OBD for fault codes. On startup,
    // the checkCodes method will update the user
    // with that status of the car.
    public void checkCodes() {
        if (connectionResult) {

            // Sets the echo to off so that the
            // command is not retransmitted.
            write("AT E0\r");

            // Sets the line feed to off.
            write("AT L0\r");

            // Sets the protocol of OBD to auto.
            write("AT SP 0\r");

            // Queries the logged DTCs
            // (Diagnostic Trouble Codes).
            write("03\r");
        }
    }

    // ???????????????????? Remove and add sleep to write
    public void write(String buffer) {
        connection.write(buffer);

        try {
            Thread.sleep(200);
        }
        catch (InterruptedException interruptException) {
            interruptException.printStackTrace();
        }
    }

    // ????????????????????????????????????
    public void read(String buffer) {
        buffer = buffer.trim();
        buffer = buffer.substring(buffer.lastIndexOf(13) + 1);

        Toast.makeText(context, buffer, Toast.LENGTH_LONG).show();

        // 43 denotes a successful DTC (Diagnostic Trouble Codes) query.
        if (buffer.length() >= 6 && buffer.substring(0, 2).equals("43") &&
                !buffer.substring(2, 4).equals("00")) {
            StringBuilder code = new StringBuilder();

            // Determine the fault code type.
            switch (buffer.substring(2, 3)) {
                case "0":
                    code.append("P0");
                    break;
                case "1":
                    code.append("P1");
                    break;
                case "2":
                    code.append("P2");
                    break;
                case "3":
                    code.append("P3");
                    break;
                case "4":
                    code.append("C0");
                    break;
                case "5":
                    code.append("C1");
                    break;
                case "6":
                    code.append("C2");
                    break;
                case "7":
                    code.append("C3");
                    break;
                case "8":
                    code.append("B0");
                    break;
                case "9":
                    code.append("B1");
                    break;
                case "A":
                    code.append("B2");
                    break;
                case "B":
                    code.append("B3");
                    break;
                case "C":
                    code.append("U0");
                    break;
                case "D":
                    code.append("U1");
                    break;
                case "E":
                    code.append("U2");
                    break;
                case "F":
                    code.append("U3");
                    break;
                default:
                    // Nothing here.
            }

            code.append(buffer.substring(3, 6));
            MainActivity.carStatus.setText(code);
        }
    }
}