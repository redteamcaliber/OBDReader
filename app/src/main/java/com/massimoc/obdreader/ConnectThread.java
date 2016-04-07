/*
    ConnectThread.java

    Author:      Massimo Cannavo

    Date:        Tue Apr 14 2015 19:00:44 UTC

    Description: The ConnectionThread class will interface with the Bluetooth
                 API to initiate and manage a Bluetooth connection.
*/
package com.massimoc.obdreader;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

// The ConnectThread will initiate a Bluetooth connection using the Bluetooth
// device that was selected from the list view. A thread will be used to manage
// the connection between the two Bluetooth devices.
public class ConnectThread extends Thread
{
    // The socket used for the Bluetooth connection.
    private BluetoothSocket mSocket;

    // The current context of the app.
    private Context mContext;

    // The stream used for reading bytes.
    private InputStream mInputStream;

    // The stream used for writing bytes.
    private static OutputStream outputStream;

    // A handler used for sending and receiving
    // messages from the read thread of the app.
    private ThreadHandler mThreadHandler;

    // Default do nothing constructor.
    public ConnectThread() {}

    // Initializes a socket with the UUID for the Bluetooth connection.
    public ConnectThread(BluetoothDevice device, Context context, ThreadHandler handler)
    {
        mContext = context;
        mThreadHandler = handler;

        // The universally unique identifier (UUID) for the Bluetooth connection.
        final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try
        {
            //mSocket = device.createRfcommSocketToServiceRecord(uuid);
            mSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            mInputStream = mSocket.getInputStream();
            outputStream = mSocket.getOutputStream();
        }

        catch (IOException ioException)
        {
            Toast.makeText(mContext, "Error: socket or stream.", Toast.LENGTH_LONG).show();
            ioException.printStackTrace();
        }
    }

    // Attempts to execute the Bluetooth connection with the target
    // Bluetooth server.
    public void executeConnection()
    {
        try
        {
            mSocket.connect();
        }

        catch (IOException ioException)
        {
            Toast.makeText(mContext, "Error: socket connection.", Toast.LENGTH_LONG).show();
            ioException.printStackTrace();
            terminateConnection();
            return;
        }

        mThreadHandler.obtainMessage(mThreadHandler.getSuccessConnect()).sendToTarget();
        read();
    }

    // Reads the input stream while connected.
    public void read()
    {
        Thread readThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                byte b;
                StringBuffer buffer;

                while (true)
                {
                    try
                    {
                        buffer = new StringBuffer();

                        // Read until '>' which denotes start of the prompt.
                        while ((char)(b = (byte)mInputStream.read()) != '>')
                        {
                            buffer.append((char) b);

                            mThreadHandler.obtainMessage(mThreadHandler.getMessageRead(),
                                    -1, -1, buffer).sendToTarget();
                        }
                    }

                    catch (IOException ioException)
                    {
                        ioException.printStackTrace();
                        break;
                    }
                }
            }
        });

        readThread.start();
    }

    // Writes the bytes to the buffer and sends it to the target device.
    public void write(String bytes)
    {
        try
        {
            outputStream.write(bytes.getBytes());
        }

        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    // Terminates the Bluetooth connection.
    public void terminateConnection()
    {
        try
        {
            mSocket.close();
        }

        catch (IOException ioException)
        {
            Toast.makeText(mContext, "Unable to terminate the connection.",
                    Toast.LENGTH_LONG).show();

            ioException.printStackTrace();
        }
    }
}