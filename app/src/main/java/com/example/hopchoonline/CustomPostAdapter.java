package com.example.hopchoonline;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
public class CustomPostAdapter extends ArrayAdapter<Post> {
    Context context;
    ArrayList<Post> posts;

    public CustomPostAdapter(Context context, int layoutToBeInflated, ArrayList<Post> posts){
        super(context, R.layout.option, posts);
        this.context = context;
        this.posts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View row = inflater.inflate(R.layout.post_summary, null);
        ImageView imageView = (ImageView) row.findViewById(R.id.imageProduct);
        TextView nameProductTextView = (TextView) row.findViewById(R.id.nameProductTextView);
        TextView priceProductTextView = (TextView) row.findViewById(R.id.priceProductTextView);

        Glide.with(context).load(posts.get(position).getImageUrl()).placeholder(R.color.white).into(imageView);
        nameProductTextView.setText(posts.get(position).getTitle());
        priceProductTextView.setText(posts.get(position).getPrice()+"");
        return (row);
    }
}


