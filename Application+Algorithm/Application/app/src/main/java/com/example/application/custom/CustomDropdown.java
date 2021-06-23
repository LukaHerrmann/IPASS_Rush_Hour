package com.example.application.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomDropdown extends BaseAdapter {

    ArrayList<Integer> colors;
    Context context;

    public CustomDropdown(Context context, int colorlistid) {
        this.context = context;
        colors = new ArrayList<Integer>();
        int[] retrieve = context.getResources().getIntArray(colorlistid);
        for (int color : retrieve) {colors.add(color);}
    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public Object getItem(int position) {
        return colors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        text.setBackgroundColor(colors.get(position));
        text.setTextSize(16f);
        text.setText("");
        return convertView;
    }
}
