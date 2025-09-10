package com.example.hopchoonline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class PostedPostItemAdapter extends RecyclerView.Adapter<PostedPostItemAdapter.ViewHolder>{
    private ArrayList<Post> items;
    private Context context;
    private String idUserLogged;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleView;
        public TextView priceView;
        MaterialButton btnDelete;
        MaterialButton btnEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.savedPostImage);
            titleView = itemView.findViewById(R.id.item_title);
            priceView = itemView.findViewById(R.id.item_price);

            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    PostDAO postDAO = new PostDAO(context);
                    String idPost = items.get(position).getId();
                    postDAO.removeMyPost(idPost, idUserLogged, new MyCallBack<Boolean>() {
                        @Override
                        public void onCallback(Boolean isSuccess) {
                            if(isSuccess) {
                                items.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Gỡ bỏ bài thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Gỡ bỏ bài thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String idMyPost = items.get(position).getId();

                    Intent moveToEditPost = new Intent(context, EditPost.class);
                    Bundle myBundle = new Bundle();
                    myBundle.putString("idUserLogged", idUserLogged);
                    myBundle.putString("idMyPost", idMyPost);

                    moveToEditPost.putExtras(myBundle);
                    context.startActivity(moveToEditPost);
                }
            });
        }
    }

    public PostedPostItemAdapter(ArrayList<Post> items, Context context, String idUserLogged) {
        this.items = items;
        this.context = context;
        this.idUserLogged = idUserLogged;
    }

    @Override
    public PostedPostItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_posted_post_item, parent, false);
        return new PostedPostItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post currentItem = items.get(position);

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
}
