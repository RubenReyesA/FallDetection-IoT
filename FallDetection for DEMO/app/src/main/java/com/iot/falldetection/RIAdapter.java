package com.iot.falldetection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class RIAdapter extends ArrayAdapter<RIItem> {

    public RIAdapter(Context context, ArrayList<RIItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RIItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_log, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.logText);
        textView.setText(Integer.toString(item.getText()));

        switch (item.getColor()) {
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
