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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;

import java.util.ArrayList;

public class SavedPostItemAdapter extends RecyclerView.Adapter<SavedPostItemAdapter.ViewHolder> {

    private ArrayList<Post> items;
    private Context context;
    private String idUserLogged;

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parent;
        public ImageView imageView;
        public ImageView iconSavePost;
        public TextView titleView;
        public TextView priceView;

        public ViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            imageView = itemView.findViewById(R.id.savedPostImage);
            titleView = itemView.findViewById(R.id.item_title);
            priceView = itemView.findViewById(R.id.item_price);
            iconSavePost = itemView.findViewById(R.id.iconSavePost);

            iconSavePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    removeItem(position);
                }
            });
        }
    }

    public SavedPostItemAdapter(ArrayList<Post> items, Context context, String idUserLogged) {
        this.items = items;
        this.context = context;
        this.idUserLogged = idUserLogged;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post currentItem = items.get(position);

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callPostActivityIntent = new Intent(context, PostActivity.class);
                callPostActivityIntent.putExtra("postId", currentItem.getId());
                context.startActivity(callPostActivityIntent);
            }
        });

        Glide.with(context).load(currentItem.getImageUrl())
                .placeholder(R.color.white)
                .into(holder.imageView);

        holder.titleView.setText(currentItem.getTitle());
        String priceStr = CommonConverter.convertCurrencyVND(Integer.parseInt(currentItem.getPrice()+""));
        holder.priceView.setText(priceStr);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // add an instance method to remove an item from the adapter
    public void removeItem(int position) {
        if (position != RecyclerView.NO_POSITION) {
            SavedPostDAO savedPostDAO = new SavedPostDAO(context);
            String idPost = items.get(position).getId();
            savedPostDAO.removeSavedPost(idUserLogged, idPost, new MyCallBack<Boolean>() {
                @Override
                public void onCallback(Boolean isSuccess) {
                    if(isSuccess) {
                        items.remove(position); // remove the corresponding item from the data source
                        notifyItemRemoved(position); // update the RecyclerView and remove the item from the view
                        Toast.makeText(context, "Đã bỏ lưu tin này", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Bỏ lưu tin thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
