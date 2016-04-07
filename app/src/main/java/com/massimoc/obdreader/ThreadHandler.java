/*
    ThreadHandler.java

    Author:      Massimo Cannavo

    Date:        Thu May 07 2015 15:31:17 UTC

    Description: A handler that handles the messages sent by the threads of the
                 OBDReader app.
*/

package com.massimoc.obdreader;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

// Custom handler that handles the exchange of the read mBuffer between
// the main thread and the read thread. Prevents memory leak using a weak
// reference to the main activity.
public class ThreadHandler extends Handler
{
    // A weak reference to the main activity of the app.
    private final WeakReference<MainActivity> ACTIVITY;

    // Denotes that the message was read by the handler.
    private final int MESSAGE_READ = 1;

    // Denotes that the Bluetooth connection was successfully
    // initiated.
    private final int SUCCESS_CONNECT = 2;

    // The UI of the app used to interface with the different
    // aspects of the app.
    private UI mUI;

    // Initializes a weak reference to the main activity.
    public ThreadHandler(MainActivity activity)
    {
        ACTIVITY = new WeakReference<>(activity);
        mUI = new UI();
    }

    // Returns the MESSAGE_READ constant.
    public int getMessageRead()
    {
        return MESSAGE_READ;
    }

    // Returns the SUCCESS_CONNECT constant.
    public int getSuccessConnect()
    {
        return SUCCESS_CONNECT;
    }

    // Handles the messages sent by the read thread of the app.
    @Override
    public void handleMessage(Message msg)
    {
        if (ACTIVITY.get() != null)
        {
            switch (msg.what)
            {
                case MESSAGE_READ:
                    mUI.read(msg.obj.toString());
                    break;

                case SUCCESS_CONNECT:
                    mUI.setConnectionResult(true);
                    mUI.checkCodes();
                    break;
            }
        }
    }
}