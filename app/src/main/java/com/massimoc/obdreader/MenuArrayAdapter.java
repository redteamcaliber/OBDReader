/*
    Copyright (C) 2015 Massimo Cannavo

    You should have received a copy of the license
    along with OBDReader; see the file LICENSE.

    Programmer:  Massimo Cannavo
    Email:       Massimocannavo15@gmail.com
    Date:        Mon May 11 2015 22:39:04

    Description: The MenuArrayAdapter class will create
                 a custom adapter used for displaying the
                 menu of the app.
*/

package com.massimoc.obdreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

// The MenuArrayAdapter is a custom adapter used for
// displaying the interactive menu of the app.
public class MenuArrayAdapter extends ArrayAdapter<String> {

    // The ViewHolder class is used by the
    // MenuArrayAdapter to update the list view.
    private static class ViewHolder {
        private ImageView mItemIcon;
        private TextView mItemText;
    }

    // An array list of menu items (choices).
    private ArrayList<String> mItems;

    // Initializes the array adapter by setting the view to the xml layout file.
    // The xml layout file contains the graphical representation of the activity.
    public MenuArrayAdapter(Context context, ArrayList<String> items) {
        super(context, R.layout.item_row, R.id.item, items);
        mItems = items;
    }

    // Initializes the row cells of the list view with the menu items.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewRef;

        // Inflate the view, if empty, otherwise, recycle the view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_row,
                    parent, false);

            viewRef = new ViewHolder();
            viewRef.mItemIcon = (ImageView)convertView.findViewById(R.id.itemIcon);
            viewRef.mItemText = (TextView)convertView.findViewById(R.id.item);

            convertView.setTag(viewRef);
        }
        else {
            viewRef = (ViewHolder)convertView.getTag();
        }

        viewRef.mItemText.setText(mItems.get(position));

        switch (position) {
            case 0:
                viewRef.mItemIcon.setImageResource(R.drawable.ic_action_bluetooth_blue);
                break;
            case 1:
                viewRef.mItemIcon.setImageResource(R.drawable.ic_action_diagnostics);
                break;
            case 2:
                viewRef.mItemIcon.setImageResource(R.drawable.ic_action_storage);
                break;
            case 3:
                viewRef.mItemIcon.setImageResource(R.drawable.ic_action_settings);
                break;
            default:
                // Nothing here.
        }

        return convertView;
    }
}