package com.example.hopchoonline;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CustomOptionAdapter extends ArrayAdapter<Option> {
    Context context;
    ArrayList<Option> options = new ArrayList<>();

    public CustomOptionAdapter(Context context, int layoutToBeInflated, ArrayList<Option> options){
        super(context, R.layout.option, options);
        this.context = context;
        this.options = options;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View row = inflater.inflate(R.layout.option, null);
        ImageView imageView = (ImageView) row.findViewById(R.id.optionIcon);
        TextView txtName = (TextView) row.findViewById(R.id.nameOptionTextView);
        imageView.setImageResource(options.get(position).getIcon());
        txtName.setText(options.get(position).getName());
        return (row);
    }
}
