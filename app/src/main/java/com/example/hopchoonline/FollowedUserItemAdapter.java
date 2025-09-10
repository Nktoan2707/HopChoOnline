package com.example.hopchoonline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class FollowedUserItemAdapter extends RecyclerView.Adapter<FollowedUserItemAdapter.ViewHolder> {

    private ArrayList<User> items;
    private Context context;
    private String idUserLogged;
    DatabaseHelper databaseHelper = new DatabaseHelper();
    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parent;
        ShapeableImageView imageViewAvatar;

        public TextView fullname;
        public Button btnFollow;

        public ViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            fullname = itemView.findViewById(R.id.txtUserFullName);
            btnFollow = itemView.findViewById(R.id.btnFollow);
            imageViewAvatar = itemView.findViewById(R.id.imgAvatar);

            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    removeItem(position);
                }
            });
        }
    }

    public FollowedUserItemAdapter(ArrayList<User> items, Context context, String idUserLogged) {
        this.items = items;
        this.context = context;
        this.idUserLogged = idUserLogged;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_followed_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User currentItem = items.get(position);

//        holder.parent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent call = new Intent(context, FollowedUsersActivity.class);
//                callPostActivityIntent.putExtra("userId", currentItem.getId());
//                context.startActivity(callPostActivityIntent);
//            }
//        });

        holder.fullname.setText(currentItem.getFullName());
        Glide.with(context).load(currentItem.getAvatarUrl())
                .placeholder(R.drawable.logo_main)
                .into(holder.imageViewAvatar);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // add an instance method to remove an item from the adapter
    public void removeItem(int position) {

        if (position != RecyclerView.NO_POSITION) {
//            FollowedDAO followedDAO = new FollowedDAO(context);
            String idFollowingUser = items.get(position).getId();
            Follow myFollow = new Follow(idUserLogged, idFollowingUser);
            databaseHelper.unFollowUser(myFollow, new MyCallBack<Boolean>() {
                @Override
                public void onCallback(Boolean check) {
                    if(check) {
                        items.remove(position); // remove the corresponding item from the data source
                        notifyItemRemoved(position); // update the RecyclerView and remove the item from the view
                        Toast.makeText(context, "Đã bỏ theo dõi người này", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Bỏ theo dõi thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
