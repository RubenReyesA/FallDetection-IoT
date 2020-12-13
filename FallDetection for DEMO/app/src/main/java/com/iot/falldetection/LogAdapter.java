package com.iot.falldetection;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class LogAdapter extends ArrayAdapter<LogItem> {

    public LogAdapter(Context context, ArrayList<LogItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LogItem logItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_log, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.logText);
        textView.setText(logItem.text);

        switch (logItem.color) {
            case 0:
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                break;
            case 1:
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorYellow));
                break;
            case 2:
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                break;
            case 3:
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            default:
                break;
        }


        return convertView;
    }
}
